/**
 * 
 */
package com.laundry.LaundryManagement.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.dto.BilledDetails;
import com.laundry.LaundryManagement.dto.BillingComponets;
import com.laundry.LaundryManagement.dto.BillingInfos;
import com.laundry.LaundryManagement.dto.ConfirmBilling;
import com.laundry.LaundryManagement.dto.ReceiptDetailDto;
import com.laundry.LaundryManagement.model.BilledComponents;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.EstimationDetails;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.InvoiceDetails;
import com.laundry.LaundryManagement.model.ItemMaster;
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

	//@Autowired
	EstimationRepository estimationRepo;
	//@Autowired
	BilledComponetRepository billingRepo;
	@Autowired
	InvoiceDetailRepository invoiceRepo;
	@Autowired
	PaymentRepository paymentRepo;
	//@Autowired
	ItemMasterRepo itemRepo;
	//@Autowired
	ItemMasterPriceDetailsRepo itemMasterPriceDetailsRepo;
	@Autowired
	CustomerInfoRepo customRepo;

	@Override
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
			System.out.println("Estimation ID:" + est.getEstID());
			int[] test = {1,0};
			for(int i=0;i < test.length; i++) {

			}
			for (BillingComponets eachCompo : billingReq.getComponets()) {
				double totalAmt = 0;
				BilledComponents components = new BilledComponents();
				components.setItemID(eachCompo.getItemID());
				components.setItemName(eachCompo.getItemName());
				components.setEstimateID(est.getEstID());
				components.setQty(eachCompo.getQty() + "");

				ItemMaster itemInfo = itemRepo.findById(eachCompo.getItemID()).get();
				ItemMasterPriceDetails itemMasterPriceDetails = new ItemMasterPriceDetails();
				if (itemInfo == null)
					return null;

				if (eachCompo.isIronReq())
					totalAmt += (eachCompo.getQty() * itemMasterPriceDetails.getOrdinaryPrice());
				if (eachCompo.isLaundryReq())
					totalAmt += (eachCompo.getQty() * itemMasterPriceDetails.getUrgentPrice());
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
			CommonUtills.addDefaultCreateInfos(inv, inv.getClass(), globalInfo);
			invoiceRepo.save(inv);
			inv.setInvoiceNumber("INV"+inv.getInvID()); //to be change
			invoiceRepo.save(inv);
			
			PaymentDetails pay = new PaymentDetails();
			pay.setInvoiceNumber(inv.getInvoiceNumber());
			pay.setPaidAmount(confirmBill.getPaidAmount());
			pay.setPayDate(new Date());
			pay.setPaidMode(confirmBill.getPayMode());
			CommonUtills.addDefaultCreateInfos(pay, pay.getClass(), globalInfo);
			paymentRepo.save(pay);
			pay.setReceiptNumber("BILL"+pay.getPayID()); //to be change
			paymentRepo.save(pay);
			
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

}
