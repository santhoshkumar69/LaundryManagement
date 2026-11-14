/**
 * 
 */
package com.laundry.LaundryManagement.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.dto.BilledDetails;
import com.laundry.LaundryManagement.dto.DashboardDto;
import com.laundry.LaundryManagement.dto.BillingComponets;
import com.laundry.LaundryManagement.dto.BillingInfos;
import com.laundry.LaundryManagement.dto.ConfirmBilling;
import com.laundry.LaundryManagement.dto.OrderDetailDto;
import com.laundry.LaundryManagement.dto.OrderStatusDto;
import com.laundry.LaundryManagement.dto.PayOrderRequest;
import com.laundry.LaundryManagement.dto.RemoveOrderItemsRequest;
import com.laundry.LaundryManagement.dto.ReceiptDetailDto;
import com.laundry.LaundryManagement.dto.RecallPaymentRequest;
import com.laundry.LaundryManagement.dto.UpdateItemRemarksRequest;
import com.laundry.LaundryManagement.dto.UpdateOrderStatus;
import com.laundry.LaundryManagement.model.BilledComponents;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.EstimationDetails;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.InvoiceDetails;
import com.laundry.LaundryManagement.model.ItemMasterPriceDetails;
import com.laundry.LaundryManagement.model.PaymentDetails;
import com.laundry.LaundryManagement.repository.BilledComponetRepository;
import com.laundry.LaundryManagement.repository.CustomerInfoRepo;
import com.laundry.LaundryManagement.repository.EstimationRepository;
import com.laundry.LaundryManagement.repository.InvoiceDetailRepository;
import com.laundry.LaundryManagement.repository.ItemMasterPriceDetailsRepo;
import com.laundry.LaundryManagement.repository.ItemMasterRepo;
import com.laundry.LaundryManagement.repository.PaymentRepository;

/**
 * @author pandyarajan
 *
 *         12-Mar-2019
 */

@Service
public class BillingServiceImpl implements BillingService {

	public static final String ORDER_PLACED = "ORDER_PLACED";
	public static final String UNDER_PROCESSING = "UNDER_PROCESSING";
	public static final String READY_TO_DELIVER = "READY_TO_DELIVER";
	public static final String COLLECTED = "COLLECTED";
	public static final String FILTER_OVERDUE = "OVERDUE";
	public static final String FILTER_FUTURE_DELIVERY = "FUTURE_DELIVERY";
	public static final String DATE_FIELD_DELIVERY = "DELIVERY";
	private static final int MAX_REMARKS_LENGTH = 500;
	private static final int MAX_RECALL_REASON_LENGTH = 255;
	public static final String PAY_RECALLED = "RECALLED";
	private static final List<String> ORDER_STATUSES = Arrays.asList(ORDER_PLACED, UNDER_PROCESSING,
			READY_TO_DELIVER, COLLECTED);
	private static final List<String> PAYMENT_MODES = Arrays.asList("Cash", "Card");
	private static final double MONEY_TOLERANCE = 0.005;

	@Autowired
	EstimationRepository estimationRepo;
	@Autowired
	ShiftService shiftService;
	@Autowired
	BilledComponetRepository billingRepo;
	@Autowired
	InvoiceDetailRepository invoiceRepo;
	@Autowired
	PaymentRepository paymentRepo;
	@Autowired
	ItemMasterRepo itemRepo;
	@Autowired
	ItemMasterPriceDetailsRepo itemMasterPriceDetailsRepo;
	@Autowired
	CustomerInfoRepo customRepo;
	@Autowired
	OutletSequenceService outletSequenceService;

	@Override
	@Transactional
	public BilledDetails createEstimationDetails(BillingInfos billingReq, GlobalInfo globalInfo) {
		BilledDetails billedDetails = new BilledDetails();
		try {
			double netAmount = 0;
			EstimationDetails est = new EstimationDetails();
			est.setCusID(billingReq.getCusID());
			est.setDeliveryDate(CommonUtills.convertStringToDate(billingReq.getDeliveryDate(), "dd/MM/yyyy"));
			est.setBillStatus(0);
			CommonUtills.addDefaultCreateInfos(est, est.getClass(), globalInfo);
			estimationRepo.save(est);
			for (BillingComponets eachCompo : billingReq.getComponets()) {
				double totalAmt = 0;
				BilledComponents components = new BilledComponents();
				components.setItemID(eachCompo.getItemID());
				components.setItemName(eachCompo.getItemName());
				components.setEstimateID(est.getEstID());
				components.setQty(eachCompo.getQty() + "");
				components.setRemarks(cleanRemarks(eachCompo.getRemarks()));

				List<ItemMasterPriceDetails> priceRows = itemMasterPriceDetailsRepo
						.findAllByItemMasterId(eachCompo.getItemID());
				ItemMasterPriceDetails price = (priceRows != null && !priceRows.isEmpty())
						? priceRows.get(0)
						: new ItemMasterPriceDetails();
				int unitPrice = eachCompo.isUrgent() ? price.getUrgentPrice() : price.getOrdinaryPrice();
				totalAmt = eachCompo.getQty() * unitPrice;
				components.setTotalAmt(totalAmt);
				netAmount += totalAmt;
				CommonUtills.addDefaultCreateInfos(components, components.getClass(), globalInfo);
				billingRepo.save(components);
			}
			billedDetails.setEstID(est.getEstID());
			billedDetails.setComponents(billingRepo.fetchBillingCompoByEsID(est.getEstID()));
			billedDetails.setTotalAmt(netAmount);
			billedDetails.setCustomerInfo(customRepo.findByQatarId(Long.parseLong(billingReq.getCusID() + "")));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return billedDetails;
	}

	@Override
	@Transactional
	public ReceiptDetailDto confirmBillingRequest(ConfirmBilling confirmBill, GlobalInfo globalInfo) {
		ReceiptDetailDto receiptInfo = new ReceiptDetailDto();
		try{
			InvoiceDetails inv = new InvoiceDetails();
			inv.setCusID(confirmBill.getCusID());
			inv.setInvoicedDate(new Date());
			inv.setInvoicedAmount(confirmBill.getInvoicedAmt());
			inv.setDiscountAmount(confirmBill.getDiscountAmt());
			inv.setDiscountPercent(confirmBill.getDiscountPercent());
			inv.setPaidAmount(confirmBill.getPaidAmount());
			inv.setOutstanding(confirmBill.getAmountPay());
			inv.setEstID(confirmBill.getEstID());
			inv.setActiveStatus(1);
			inv.setOrderStatus(ORDER_PLACED);
			inv.setDeliveryDate(parseDate(confirmBill.getDeliveryDate(), "dd/MM/yyyy HH:mm"));
			inv.setDeliveryAddress(confirmBill.getDeliveryAddress());
			inv.setPickupType(confirmBill.getPickupType());
			inv.setPaymentMode(confirmBill.getPayMode());
			inv.setCardType(confirmBill.getCardType());
			inv.setExtraChargesAmt(confirmBill.getExtraChargesAmt());
			CommonUtills.addDefaultCreateInfos(inv, inv.getClass(), globalInfo);
			invoiceRepo.save(inv);
			inv.setInvoiceNumber("INV"+inv.getInvID()); //to be change
			invoiceRepo.save(inv);
			
			PaymentDetails pay = new PaymentDetails();
			pay.setInvoiceNumber(inv.getInvoiceNumber());
			pay.setPaidAmount(confirmBill.getPaidAmount());
			pay.setPayDate(new Date());
			pay.setPaidMode(confirmBill.getPayMode());
			pay.setPaySource(PaymentDetails.SOURCE_BILLING);
			pay.setShiftId(shiftService.openShiftIdHere());
			CommonUtills.addDefaultCreateInfos(pay, pay.getClass(), globalInfo);
			paymentRepo.save(pay);
			pay.setReceiptNumber(outletSequenceService.next(OutletSequenceService.RECEIPT));
			paymentRepo.save(pay);
			inv.setReceiptNo(pay.getReceiptNumber());
			invoiceRepo.save(inv);
			
			billingRepo.updateInvoiceID(inv.getInvoiceNumber(), confirmBill.getEstID());
			EstimationDetails est = estimationRepo.findById(confirmBill.getEstID()).get();
			est.setBillStatus(1);
			CommonUtills.addDefaultUpdateInfos(est, est.getClass(), globalInfo);
			estimationRepo.save(est);
			
			receiptInfo.setReceiptNo(pay.getReceiptNumber());
			receiptInfo.setPaidAmt(confirmBill.getPaidAmount());
			receiptInfo.setDiscountAmt(confirmBill.getDiscountAmt());
			receiptInfo.setOutstanding(confirmBill.getAmountPay());
			receiptInfo.setCustomer(customRepo.findByQatarId(Long.parseLong(confirmBill.getCusID()+"")));
			receiptInfo.setComponents(billingRepo.fetchBillingCompoByEsID(confirmBill.getEstID()));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return receiptInfo;
	}

	@Override
	public boolean cancelBillingRequest(ConfirmBilling confirmBill, GlobalInfo globalInfo) {
		try{
			EstimationDetails est = estimationRepo.findById(confirmBill.getEstID()).get();
			est.setBillStatus(9);
			est.setComments(confirmBill.getComments());
			CommonUtills.addDefaultUpdateInfos(est, est.getClass(), globalInfo);
			estimationRepo.save(est);
			billingRepo.deleteAddedComponents(confirmBill.getEstID());
			return true;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public CustomerInfo fetchCustomerDetails(Integer cusID, String mobileNo) {
		CustomerInfo cusInfo = new CustomerInfo();
		try {
			if(cusID!=null && cusID>0) cusInfo = customRepo.findByQatarId(Long.parseLong(cusID+""));
			if(cusInfo.getQatarId()!=null && cusInfo.getQatarId()!=0) return cusInfo;
			// if customer ID is null then search with mobile number
			if(cusInfo.getQatarId() == null && mobileNo != null) {
				cusInfo  = customRepo.findByMobNo(mobileNo);
			} else {
				cusInfo.setQatarId(Long.parseLong(cusID+""));
				cusInfo.setMobileNo(mobileNo);
				customRepo.save(cusInfo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cusInfo;
	}

	@Override
	public void removeBillItems(int itemID) {
		try {
			billingRepo.deleteById(itemID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public List<OrderStatusDto> fetchOrders(String fromDate, String toDate, String search, String status) {
		return fetchOrders(fromDate, toDate, search, status, null, false);
	}

	@Override
	public List<OrderStatusDto> fetchOrders(String fromDate, String toDate, String search, String status,
			String dateField, boolean includeOverdue) {
		boolean byDeliveryDate = DATE_FIELD_DELIVERY.equalsIgnoreCase(trimToNull(dateField));
		Date today = startOfDay(new Date());
		Date from = parseDate(fromDate, "yyyy-MM-dd");
		Date to = parseDate(toDate, "yyyy-MM-dd");
		if (from == null) {
			from = byDeliveryDate ? today : addDays(today, -30);
		}
		if (to == null) {
			to = byDeliveryDate ? from : today;
		}
		List<InvoiceDetails> invoices;
		if (byDeliveryDate) {
			invoices = new ArrayList<InvoiceDetails>();
			if (includeOverdue) {
				invoices.addAll(invoiceRepo.findUndeliveredDueBefore(startOfDay(from)));
			}
			invoices.addAll(invoiceRepo.findByDeliveryDateRange(startOfDay(from), addDays(startOfDay(to), 1)));
		} else {
			invoices = invoiceRepo.findByInvoicedDateRange(startOfDay(from), addDays(startOfDay(to), 1));
		}

		String searchFilter = trimToNull(search);
		String statusFilter = trimToNull(status);
		if (statusFilter != null && "ALL".equalsIgnoreCase(statusFilter)) {
			statusFilter = null;
		}
		Date now = new Date();
		Date tomorrow = addDays(today, 1);
		Map<Integer, CustomerInfo> customers = new HashMap<Integer, CustomerInfo>();
		List<OrderStatusDto> orders = new ArrayList<OrderStatusDto>();

		for (InvoiceDetails inv : invoices) {
			OrderStatusDto order = toOrderStatus(inv, customers);

			if (searchFilter != null && !containsIgnoreCase(order.getReceiptNo(), searchFilter)
					&& !containsIgnoreCase(order.getInvoiceNo(), searchFilter)
					&& !containsIgnoreCase(String.valueOf(order.getCustomerId()), searchFilter)
					&& !containsIgnoreCase(order.getPhoneNo(), searchFilter)
					&& !containsIgnoreCase(order.getCustomerName(), searchFilter)) {
				continue;
			}
			if (statusFilter != null) {
				boolean collected = COLLECTED.equals(order.getOrderStatus());
				Date delivery = order.getDeliveryDate();
				if (FILTER_OVERDUE.equals(statusFilter)) {
					if (collected || delivery == null || !delivery.before(now)) {
						continue;
					}
				} else if (FILTER_FUTURE_DELIVERY.equals(statusFilter)) {
					if (collected || delivery == null || delivery.before(tomorrow)) {
						continue;
					}
				} else if (!statusFilter.equals(order.getOrderStatus())) {
					continue;
				}
			}
			orders.add(order);
		}
		return orders;
	}

	@Override
	public DashboardDto fetchDashboard() {
		Date today = startOfDay(new Date());
		Date tomorrow = addDays(today, 1);
		DashboardDto dashboard = new DashboardDto();
		dashboard.setDueToday(invoiceRepo.countUndeliveredDueBetween(today, tomorrow));
		dashboard.setOverdue(invoiceRepo.countUndeliveredDueBefore(today));

		for (String status : ORDER_STATUSES) {
			if (!COLLECTED.equals(status)) {
				dashboard.getOpenByStatus().put(status, 0L);
			}
		}
		Date oldestOpen = null;
		for (Object[] row : invoiceRepo.summariseOpenByStatus()) {
			String status = row[0] == null ? ORDER_PLACED : (String) row[0];
			dashboard.getOpenByStatus().merge(status, ((Number) row[1]).longValue(), Long::sum);
			Date oldest = (Date) row[2];
			if (oldest != null && (oldestOpen == null || oldest.before(oldestOpen))) {
				oldestOpen = oldest;
			}
		}
		dashboard.setOldestOpenBillDate(oldestOpen);

		for (Object[] row : invoiceRepo.summariseBilledBetween(today, tomorrow)) {
			dashboard.setBillsToday(((Number) row[0]).longValue());
			dashboard.setBilledToday(round2(((Number) row[1]).doubleValue()));
		}
		for (String mode : PAYMENT_MODES) {
			dashboard.getReceivedToday().put(mode, 0.0);
		}
		for (Object[] row : paymentRepo.sumReceivedByModeBetween(today, tomorrow)) {
			String mode = row[0] == null ? "Other" : (String) row[0];
			dashboard.getReceivedToday().merge(mode, round2(((Number) row[1]).doubleValue()), Double::sum);
		}
		dashboard.setCreditOutstanding(round2(invoiceRepo.sumOutstanding()));

		Map<Integer, CustomerInfo> customers = new HashMap<Integer, CustomerInfo>();
		for (InvoiceDetails inv : invoiceRepo.findUndeliveredDueBetween(today, tomorrow, PageRequest.of(0, 8))) {
			dashboard.getNextDeliveries().add(toOrderStatus(inv, customers));
		}
		return dashboard;
	}

	@Override
	@Transactional
	public List<OrderStatusDto> updateOrderStatus(UpdateOrderStatus request, GlobalInfo globalInfo) {
		if (request == null || !ORDER_STATUSES.contains(request.getOrderStatus())) {
			throw new IllegalArgumentException("Invalid order status");
		}
		List<Integer> ids = request.getInvIDs() != null && !request.getInvIDs().isEmpty()
				? request.getInvIDs()
				: Arrays.asList(request.getInvID());
		List<InvoiceDetails> invoices = invoiceRepo.findAllById(ids);
		if (invoices.isEmpty()) {
			throw new IllegalArgumentException("Order not found");
		}
		Map<Integer, CustomerInfo> customers = new HashMap<Integer, CustomerInfo>();
		List<OrderStatusDto> updated = new ArrayList<OrderStatusDto>();
		for (InvoiceDetails inv : invoices) {
			if (inv.getActiveStatus() != null && inv.getActiveStatus() == 0) {
				continue;
			}
			inv.setOrderStatus(request.getOrderStatus());
			if (COLLECTED.equals(request.getOrderStatus())) {
				if (inv.getCollectedDate() == null) {
					inv.setCollectedDate(new Date());
				}
			} else {
				inv.setCollectedDate(null);
			}
			CommonUtills.addDefaultUpdateInfos(inv, inv.getClass(), globalInfo);
			invoiceRepo.save(inv);
			updated.add(toOrderStatus(inv, customers));
		}
		return updated;
	}

	@Override
	@Transactional
	public int deleteOrders(List<Integer> invIDs, GlobalInfo globalInfo) {
		if (invIDs == null || invIDs.isEmpty()) {
			throw new IllegalArgumentException("Select at least one order");
		}
		List<InvoiceDetails> invoices = invoiceRepo.findAllById(invIDs);
		if (invoices.isEmpty()) {
			throw new IllegalArgumentException("Order not found");
		}
		List<String> delivered = new ArrayList<String>();
		for (InvoiceDetails inv : invoices) {
			if (COLLECTED.equals(inv.getOrderStatus())) {
				delivered.add(inv.getReceiptNo() != null ? inv.getReceiptNo() : inv.getInvoiceNumber());
			}
		}
		if (!delivered.isEmpty()) {
			throw new IllegalArgumentException(
					"Delivered bills cannot be deleted: " + String.join(", ", delivered) + ". Nothing was deleted.");
		}
		for (InvoiceDetails inv : invoices) {
			String invoiceNo = inv.getInvoiceNumber();
			if (invoiceNo != null) {
				paymentRepo.deleteByInvoiceNumber(invoiceNo);
				billingRepo.deleteByInvoiceNo(invoiceNo);
			}
			if (inv.getEstID() != null) {
				billingRepo.deleteAddedComponents(inv.getEstID());
				estimationRepo.deleteEstimation(inv.getEstID());
			}
			invoiceRepo.delete(inv);
		}
		return invoices.size();
	}

	@Override
	public OrderDetailDto fetchOrderDetail(int invID) {
		return toOrderDetail(findActiveInvoice(invID));
	}

	@Override
	@Transactional
	public OrderDetailDto payOrder(PayOrderRequest request, GlobalInfo globalInfo) {
		if (request == null) {
			throw new IllegalArgumentException("Payment details are required");
		}
		InvoiceDetails inv = findActiveInvoice(request.getInvID());
		List<PayOrderRequest.Part> parts = request.getParts() != null && !request.getParts().isEmpty()
				? request.getParts()
				: Arrays.asList(new PayOrderRequest.Part(request.getPayMode(), request.getAmount()));
		Set<String> usedModes = new HashSet<String>();
		double amount = 0;
		for (PayOrderRequest.Part part : parts) {
			double partAmount = round2(part.getAmount());
			if (partAmount < 0) {
				throw new IllegalArgumentException("Amount cannot be negative");
			}
			if (partAmount > 0) {
				if (!PAYMENT_MODES.contains(part.getPayMode())) {
					throw new IllegalArgumentException("Payment method must be Cash or Card");
				}
				if (!usedModes.add(part.getPayMode())) {
					throw new IllegalArgumentException("Each payment method can only be used once per payment");
				}
			}
			amount += partAmount;
		}
		amount = round2(amount);
		double outstanding = round2(Math.max(inv.getInvoicedAmount() - inv.getPaidAmount(), 0));
		if (amount > outstanding + MONEY_TOLERANCE) {
			throw new IllegalArgumentException("Amount is more than the balance of " + outstanding);
		}
		if (amount <= 0 && !request.isMarkDelivered()) {
			throw new IllegalArgumentException("Enter an amount to pay, or mark the order as delivered");
		}
		for (PayOrderRequest.Part part : parts) {
			double partAmount = round2(part.getAmount());
			if (partAmount <= 0) {
				continue;
			}
			PaymentDetails pay = new PaymentDetails();
			pay.setInvoiceNumber(inv.getInvoiceNumber());
			pay.setPaidAmount(partAmount);
			pay.setPayDate(new Date());
			pay.setPaidMode(part.getPayMode());
			pay.setPaySource(PaymentDetails.SOURCE_DELIVERY);
			pay.setShiftId(shiftService.openShiftIdHere());
			CommonUtills.addDefaultCreateInfos(pay, pay.getClass(), globalInfo);
			paymentRepo.save(pay);
			pay.setReceiptNumber(outletSequenceService.next(OutletSequenceService.PAYMENT));
			paymentRepo.save(pay);
		}
		inv.setPaidAmount(round2(inv.getPaidAmount() + amount));
		inv.setOutstanding(round2(Math.max(inv.getInvoicedAmount() - inv.getPaidAmount(), 0)));
		if (request.isMarkDelivered()) {
			inv.setOrderStatus(COLLECTED);
			if (inv.getCollectedDate() == null) {
				inv.setCollectedDate(new Date());
			}
		}
		CommonUtills.addDefaultUpdateInfos(inv, inv.getClass(), globalInfo);
		invoiceRepo.save(inv);
		return toOrderDetail(inv);
	}

	@Override
	@Transactional
	public OrderDetailDto removeOrderItems(RemoveOrderItemsRequest request, GlobalInfo globalInfo) {
		if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("Select the items to remove");
		}
		InvoiceDetails inv = findActiveInvoice(request.getInvID());
		if (COLLECTED.equals(inv.getOrderStatus())) {
			throw new IllegalArgumentException("Items cannot be removed after the order is delivered");
		}
		Map<Integer, BilledComponents> byId = new HashMap<Integer, BilledComponents>();
		int totalQty = 0;
		for (BilledComponents compo : billingRepo.fetchBillingCompoByInvoiceNo(inv.getInvoiceNumber())) {
			byId.put(compo.getsID(), compo);
			totalQty += parseQty(compo.getQty());
		}
		double removedValue = 0;
		int removedQty = 0;
		for (RemoveOrderItemsRequest.Line line : request.getItems()) {
			if (line.getRemoveQty() <= 0) {
				continue;
			}
			BilledComponents compo = byId.get(line.getsID());
			if (compo == null) {
				throw new IllegalArgumentException("Item does not belong to this order");
			}
			int qty = parseQty(compo.getQty());
			if (line.getRemoveQty() > qty) {
				throw new IllegalArgumentException("Cannot remove more " + compo.getItemName() + " than were billed");
			}
			removedValue += qty > 0 ? compo.getTotalAmt() / qty * line.getRemoveQty() : 0;
			removedQty += line.getRemoveQty();
		}
		if (removedQty == 0) {
			throw new IllegalArgumentException("Select the items to remove");
		}
		if (removedQty >= totalQty) {
			throw new IllegalArgumentException("At least one item must remain on the bill");
		}
		double newTotal = round2(Math.max(inv.getInvoicedAmount() - removedValue, 0));
		if (newTotal < inv.getPaidAmount() - MONEY_TOLERANCE) {
			throw new IllegalArgumentException("Customer has already paid " + round2(inv.getPaidAmount())
					+ ", which is more than the new total of " + newTotal);
		}
		for (RemoveOrderItemsRequest.Line line : request.getItems()) {
			if (line.getRemoveQty() <= 0) {
				continue;
			}
			BilledComponents compo = byId.get(line.getsID());
			int qty = parseQty(compo.getQty());
			int remaining = qty - line.getRemoveQty();
			if (remaining <= 0) {
				billingRepo.delete(compo);
			} else {
				compo.setTotalAmt(round2(compo.getTotalAmt() / qty * remaining));
				compo.setQty(String.valueOf(remaining));
				CommonUtills.addDefaultUpdateInfos(compo, compo.getClass(), globalInfo);
				billingRepo.save(compo);
			}
		}
		inv.setInvoicedAmount(newTotal);
		inv.setOutstanding(round2(Math.max(newTotal - inv.getPaidAmount(), 0)));
		CommonUtills.addDefaultUpdateInfos(inv, inv.getClass(), globalInfo);
		invoiceRepo.save(inv);
		return toOrderDetail(inv);
	}

	private InvoiceDetails findActiveInvoice(int invID) {
		InvoiceDetails inv = invoiceRepo.findById(invID)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		if (inv.getActiveStatus() != null && inv.getActiveStatus() == 0) {
			throw new IllegalArgumentException("Order not found");
		}
		return inv;
	}

	private OrderDetailDto toOrderDetail(InvoiceDetails inv) {
		OrderDetailDto detail = new OrderDetailDto();
		detail.setOrder(toOrderStatus(inv, new HashMap<Integer, CustomerInfo>()));
		if (inv.getInvoiceNumber() != null) {
			for (BilledComponents compo : billingRepo.fetchBillingCompoByInvoiceNo(inv.getInvoiceNumber())) {
				OrderDetailDto.Item item = new OrderDetailDto.Item();
				int qty = parseQty(compo.getQty());
				item.setsID(compo.getsID());
				item.setItemName(compo.getItemName());
				item.setQty(qty);
				item.setTotalAmt(round2(compo.getTotalAmt()));
				item.setUnitPrice(qty > 0 ? round2(compo.getTotalAmt() / qty) : 0);
				item.setRemarks(compo.getRemarks());
				detail.getItems().add(item);
			}
			for (PaymentDetails pay : paymentRepo.findByInvoiceNumber(inv.getInvoiceNumber())) {
				OrderDetailDto.Payment payment = new OrderDetailDto.Payment();
				payment.setReceiptNo(pay.getReceiptNumber());
				payment.setPaidAmt(round2(pay.getPaidAmount()));
				payment.setPaidMode(pay.getPaidMode());
				payment.setPayDate(pay.getPayDate());
				payment.setPayID(pay.getPayID());
				boolean recalled = PAY_RECALLED.equals(pay.getPayStatus());
				payment.setRecalled(recalled);
				payment.setRecallReason(pay.getRecallReason());
				payment.setRecalledDate(pay.getRecalledDate());
				payment.setRecalledBy(pay.getRecalledBy());
				payment.setRecallable(!recalled && pay.getPaidAmount() > 0 && isRecallableToday(pay));
				detail.getPayments().add(payment);
			}
		}
		return detail;
	}

	@Override
	@Transactional
	public OrderDetailDto recallPayment(RecallPaymentRequest request, GlobalInfo globalInfo) {
		if (request == null) {
			throw new IllegalArgumentException("Choose the payment to recall");
		}
		InvoiceDetails inv = findActiveInvoice(request.getInvID());
		PaymentDetails pay = paymentRepo.findById(request.getPayID()).orElse(null);
		if (pay == null || inv.getInvoiceNumber() == null || !inv.getInvoiceNumber().equals(pay.getInvoiceNumber())) {
			throw new IllegalArgumentException("Payment not found on this bill");
		}
		if (PAY_RECALLED.equals(pay.getPayStatus())) {
			throw new IllegalArgumentException(pay.getReceiptNumber() + " is already recalled");
		}
		if (pay.getPaidAmount() <= 0) {
			throw new IllegalArgumentException("There is no amount to recall on " + pay.getReceiptNumber());
		}
		if (!isRecallableToday(pay)) {
			throw new IllegalArgumentException("Payments can only be recalled on the day they were taken");
		}
		String reason = request.getReason() == null ? "" : request.getReason().trim();
		if (reason.isEmpty()) {
			throw new IllegalArgumentException("Enter a reason for the recall");
		}
		if (reason.length() > MAX_RECALL_REASON_LENGTH) {
			throw new IllegalArgumentException("Keep the reason under " + MAX_RECALL_REASON_LENGTH + " characters");
		}
		pay.setPayStatus(PAY_RECALLED);
		pay.setRecalledDate(new Date());
		pay.setRecalledBy(globalInfo != null ? globalInfo.getUserName() : null);
		pay.setRecallReason(reason);
		pay.setRecallShiftId(shiftService.shiftIdForRecall(pay.getShiftId()));
		paymentRepo.save(pay);

		inv.setPaidAmount(round2(Math.max(inv.getPaidAmount() - pay.getPaidAmount(), 0)));
		inv.setOutstanding(round2(Math.max(inv.getInvoicedAmount() - inv.getPaidAmount(), 0)));
		CommonUtills.addDefaultUpdateInfos(inv, inv.getClass(), globalInfo);
		invoiceRepo.save(inv);
		return toOrderDetail(inv);
	}

	private boolean isRecallableToday(PaymentDetails pay) {
		return pay.getPayDate() != null && startOfDay(pay.getPayDate()).equals(startOfDay(new Date()));
	}

	@Override
	@Transactional
	public OrderStatusDto updateItemRemarks(UpdateItemRemarksRequest request, GlobalInfo globalInfo) {
		if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("No item remarks to update");
		}
		InvoiceDetails inv = findActiveInvoice(request.getInvID());
		Map<Integer, BilledComponents> lines = new HashMap<Integer, BilledComponents>();
		if (inv.getInvoiceNumber() != null) {
			for (BilledComponents compo : billingRepo.fetchBillingCompoByInvoiceNo(inv.getInvoiceNumber())) {
				lines.put(compo.getsID(), compo);
			}
		}
		for (UpdateItemRemarksRequest.Line change : request.getItems()) {
			BilledComponents compo = lines.get(change.getsID());
			if (compo == null) {
				throw new IllegalArgumentException("Item " + change.getsID() + " is not part of this bill");
			}
			String remarks = cleanRemarks(change.getRemarks());
			if (remarks == null ? compo.getRemarks() != null : !remarks.equals(compo.getRemarks())) {
				compo.setRemarks(remarks);
				CommonUtills.addDefaultUpdateInfos(compo, compo.getClass(), globalInfo);
				billingRepo.save(compo);
			}
		}
		return toOrderStatus(inv, new HashMap<Integer, CustomerInfo>());
	}

	/** Remarks are optional free text; blank becomes null and long text is cut to the column size. */
	private static String cleanRemarks(String remarks) {
		if (remarks == null || remarks.trim().isEmpty()) {
			return null;
		}
		String value = remarks.trim();
		return value.length() > MAX_REMARKS_LENGTH ? value.substring(0, MAX_REMARKS_LENGTH) : value;
	}

	private static double round2(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	private OrderStatusDto toOrderStatus(InvoiceDetails inv, Map<Integer, CustomerInfo> customers) {
		OrderStatusDto order = new OrderStatusDto();
		order.setInvID(inv.getInvID());
		order.setInvoiceNo(inv.getInvoiceNumber());
		order.setReceiptNo(inv.getReceiptNo() != null ? inv.getReceiptNo() : inv.getInvoiceNumber());
		order.setBillingDate(inv.getInvoicedDate() != null ? inv.getInvoicedDate() : inv.getCreatedDate());
		order.setCollectedDate(inv.getCollectedDate());
		order.setOrderStatus(inv.getOrderStatus() != null ? inv.getOrderStatus() : ORDER_PLACED);
		order.setPickupType(inv.getPickupType());
		order.setDeliveryAddress(inv.getDeliveryAddress());
		order.setPaymentMode(inv.getPaymentMode());
		order.setExtraChargesAmt(inv.getExtraChargesAmt() != null ? inv.getExtraChargesAmt() : 0);
		order.setDiscountAmt(inv.getDiscountAmount());
		if (inv.getInvoiceNumber() != null) {
			StringBuilder methods = new StringBuilder();
			for (PaymentDetails pay : paymentRepo.findByInvoiceNumber(inv.getInvoiceNumber())) {
				String mode = pay.getPaidMode();
				if (pay.getPaidAmount() > 0 && !PAY_RECALLED.equals(pay.getPayStatus()) && mode != null
						&& methods.indexOf(mode) < 0) {
					if (methods.length() > 0) {
						methods.append(", ");
					}
					methods.append(mode);
				}
			}
			order.setPaymentMethods(methods.toString());
		}
		order.setInvoicedAmt(inv.getInvoicedAmount());
		order.setPaidAmt(inv.getPaidAmount());
		order.setOutstandingAmt(inv.getOutstanding());
		if (inv.getOutstanding() <= 0.004) {
			order.setPaymentStatus("PAID");
		} else if (inv.getPaidAmount() <= 0.004) {
			order.setPaymentStatus("CREDIT");
		} else {
			order.setPaymentStatus("PARTIAL");
		}

		CustomerInfo customer = customers.get(inv.getCusID());
		if (customer == null && !customers.containsKey(inv.getCusID())) {
			customer = customRepo.findByQatarId(Long.valueOf(inv.getCusID()));
			customers.put(inv.getCusID(), customer);
		}
		order.setCustomerId(Long.valueOf(inv.getCusID()));
		if (customer != null) {
			order.setCustomerName(customer.getCustomerName());
			order.setPhoneNo(customer.getMobileNo());
			if (order.getDeliveryAddress() == null) {
				order.setDeliveryAddress(customer.getAddress());
			}
		}

		List<BilledComponents> components = inv.getInvoiceNumber() != null
				? billingRepo.fetchBillingCompoByInvoiceNo(inv.getInvoiceNumber())
				: new ArrayList<BilledComponents>();
		Map<String, Integer> qtyByItem = new LinkedHashMap<String, Integer>();
		int totalQty = 0;
		for (BilledComponents compo : components) {
			int qty = parseQty(compo.getQty());
			totalQty += qty;
			String name = compo.getItemName() != null ? compo.getItemName() : "Item";
			Integer current = qtyByItem.get(name);
			qtyByItem.put(name, (current == null ? 0 : current) + qty);
		}
		for (BilledComponents compo : components) {
			OrderStatusDto.ItemLine line = new OrderStatusDto.ItemLine();
			line.setsID(compo.getsID());
			line.setItemName(compo.getItemName() != null ? compo.getItemName() : "Item");
			line.setQty(parseQty(compo.getQty()));
			line.setRemarks(compo.getRemarks());
			order.getItems().add(line);
		}
		StringBuilder details = new StringBuilder();
		for (Map.Entry<String, Integer> entry : qtyByItem.entrySet()) {
			if (details.length() > 0) {
				details.append(", ");
			}
			details.append(entry.getKey()).append(" x ").append(entry.getValue());
		}
		order.setNoOfItems(totalQty);
		order.setItemDetails(details.toString());

		Date delivery = inv.getDeliveryDate();
		if (delivery == null) {
			Integer estID = inv.getEstID();
			if (estID == null && !components.isEmpty()) {
				estID = components.get(0).getEstimateID();
			}
			if (estID != null) {
				EstimationDetails est = estimationRepo.findById(estID).orElse(null);
				if (est != null) {
					delivery = est.getDeliveryDate();
				}
			}
		}
		order.setDeliveryDate(delivery);
		return order;
	}

	private static int parseQty(String qty) {
		try {
			return qty == null ? 0 : (int) Math.round(Double.parseDouble(qty.trim()));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private static Date parseDate(String value, String format) {
		String text = trimToNull(value);
		if (text == null) {
			return null;
		}
		try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			sf.setLenient(false);
			return sf.parse(text);
		} catch (ParseException ex) {
			return null;
		}
	}

	private static Date startOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	private static String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private static boolean containsIgnoreCase(String value, String search) {
		return value != null && value.toLowerCase().contains(search.toLowerCase());
	}

}
