package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.excel.ExcelSheet;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.InvoiceDetails;
import com.laundry.LaundryManagement.model.PaymentDetails;
import com.laundry.LaundryManagement.repository.CustomerInfoRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Credit List: unpaid bill balances of the current outlet, grouped by customer. Walk-in bills (no customer)
 * with a balance are grouped under customer ID 0 so the totals match the dashboard's Credit outstanding.
 */
@Service
public class CreditService {

	/** Customer ID used for bills without a customer. */
	public static final int WALK_IN = 0;
	private static final double OPEN = 0.004;
	private static final String RECALLED = "RECALLED";

	/** Age bands by days since the bill date; the last one is open-ended. */
	public static final List<String> BANDS = List.of("0-30", "31-60", "61-90", "90+");

	public record CreditRow(int customerId, String customerName, String mobileNo, int openBills,
			BigDecimal balance, String oldestBillDate, long oldestAgeDays, String band, String lastPaymentDate) {
	}

	public record Summary(BigDecimal totalBalance, int customers, int openBills, Map<String, BigDecimal> bands) {
	}

	public record CreditList(Summary summary, List<CreditRow> rows) {
	}

	public record OpenBill(int invId, String receiptNo, String billDate, String deliveryDate, String orderStatus,
			BigDecimal total, BigDecimal paid, BigDecimal balance, long ageDays) {
	}

	/**
	 * A statement line, newest first. type is BILL, PAYMENT or RECALL; debit raises the balance, credit lowers
	 * it; balance is what was owed after the line.
	 */
	public record StatementLine(String date, String type, String reference, String billReceiptNo, String detail,
			BigDecimal debit, BigDecimal credit, BigDecimal balance) {
	}

	public record CustomerCredit(int customerId, String customerName, String mobileNo, String address,
			BigDecimal balance, String firstOpenDeliveryDate, String lastOpenDeliveryDate, List<OpenBill> openBills,
			List<StatementLine> statement) {
	}

	@Autowired
	private CustomerInfoRepo customerRepo;

	@PersistenceContext
	private EntityManager em;

	/** Customers owing this outlet money, largest balance first, filtered by search, oldest-bill band and minimum. */
	public CreditList list(String search, String band, BigDecimal minBalance) {
		if (band != null && !band.isBlank() && !BANDS.contains(band)) {
			throw new IllegalArgumentException("Age band must be one of " + String.join(", ", BANDS));
		}
		List<InvoiceDetails> open = openBills();
		Map<Integer, List<InvoiceDetails>> byCustomer = open.stream()
				.collect(Collectors.groupingBy(inv -> inv.getCusID() > 0 ? inv.getCusID() : WALK_IN));
		Map<Integer, CustomerInfo> customers = customers(byCustomer.keySet());
		Map<Integer, Date> lastPayments = lastPaymentDates();
		LocalDate today = LocalDate.now();

		Map<String, BigDecimal> bands = new LinkedHashMap<>();
		BANDS.forEach(b -> bands.put(b, money(0)));
		BigDecimal total = money(0);
		for (InvoiceDetails inv : open) {
			BigDecimal balance = money(inv.getOutstanding());
			bands.merge(band(age(inv.getInvoicedDate(), today)), balance, BigDecimal::add);
			total = total.add(balance);
		}

		String term = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
		List<CreditRow> rows = new ArrayList<>();
		for (Map.Entry<Integer, List<InvoiceDetails>> entry : byCustomer.entrySet()) {
			int customerId = entry.getKey();
			List<InvoiceDetails> bills = entry.getValue();
			CustomerInfo customer = customers.get(customerId);
			Date oldest = bills.stream().map(InvoiceDetails::getInvoicedDate).filter(d -> d != null)
					.min(Comparator.naturalOrder()).orElse(null);
			long ageDays = age(oldest, today);
			BigDecimal balance = bills.stream().map(inv -> money(inv.getOutstanding())).reduce(money(0), BigDecimal::add);
			CreditRow row = new CreditRow(customerId, name(customerId, customer), customer == null ? null : customer.getMobileNo(),
					bills.size(), balance, day(oldest), ageDays, band(ageDays), day(lastPayments.get(customerId)));
			if (!term.isEmpty() && !matches(row, term)) {
				continue;
			}
			if (band != null && !band.isBlank() && !band.equals(row.band())) {
				continue;
			}
			if (minBalance != null && row.balance().compareTo(minBalance) < 0) {
				continue;
			}
			rows.add(row);
		}
		rows.sort(Comparator.comparing(CreditRow::balance).reversed().thenComparing(CreditRow::customerName));
		return new CreditList(new Summary(total, (int) byCustomer.keySet().stream().filter(id -> id != WALK_IN).count(),
				open.size(), bands), rows);
	}

	/** One customer's open bills and statement at this outlet; customer ID 0 is the walk-in bills with a balance. */
	public CustomerCredit customer(int customerId) {
		CustomerInfo customer = null;
		List<InvoiceDetails> bills;
		if (customerId == WALK_IN) {
			bills = openBills().stream().filter(inv -> inv.getCusID() <= 0).toList();
		} else {
			customer = customerRepo.findById(customerId).orElse(null);
			bills = em.createQuery("from InvoiceDetails inv where inv.cusID = :cus"
					+ " and (inv.activeStatus is null or inv.activeStatus <> 0)", InvoiceDetails.class)
					.setParameter("cus", customerId).getResultList();
			if (customer == null && bills.isEmpty()) {
				throw new IllegalArgumentException("Customer not found");
			}
		}
		LocalDate today = LocalDate.now();
		List<InvoiceDetails> open = bills.stream().filter(inv -> inv.getOutstanding() > OPEN)
				.sorted(Comparator.comparing(InvoiceDetails::getInvoicedDate, Comparator.nullsLast(Comparator.naturalOrder())))
				.toList();
		List<OpenBill> openBills = open.stream().map(inv -> new OpenBill(inv.getInvID(), inv.getReceiptNo(),
				instant(inv.getInvoicedDate()), instant(inv.getDeliveryDate()), inv.getOrderStatus(),
				money(inv.getInvoicedAmount()), money(inv.getPaidAmount()), money(inv.getOutstanding()),
				age(inv.getInvoicedDate(), today))).toList();
		BigDecimal balance = openBills.stream().map(OpenBill::balance).reduce(money(0), BigDecimal::add);
		Date firstDelivery = open.stream().map(InvoiceDetails::getDeliveryDate).filter(d -> d != null)
				.min(Comparator.naturalOrder()).orElse(null);
		Date lastDelivery = open.stream().map(InvoiceDetails::getDeliveryDate).filter(d -> d != null)
				.max(Comparator.naturalOrder()).orElse(null);
		return new CustomerCredit(customerId, name(customerId, customer), customer == null ? null : customer.getMobileNo(),
				customer == null ? null : customer.getAddress(), balance, day(firstDelivery), day(lastDelivery),
				openBills, statement(bills));
	}

	/** The filtered list as an Excel sheet. */
	public byte[] export(String search, String band, BigDecimal minBalance) {
		List<List<Object>> rows = new ArrayList<>();
		for (CreditRow r : list(search, band, minBalance).rows()) {
			rows.add(List.<Object>of(r.customerId() == WALK_IN ? "" : r.customerId(), r.customerName(),
					r.mobileNo() == null ? "" : r.mobileNo(), r.openBills(), r.balance(),
					r.oldestBillDate() == null ? "" : r.oldestBillDate(), r.oldestAgeDays(), r.band(),
					r.lastPaymentDate() == null ? "" : r.lastPaymentDate()));
		}
		return ExcelSheet.write("Credit List", List.of("Customer ID", "Customer", "Mobile", "Open bills", "Balance",
				"Oldest bill", "Age (days)", "Age band", "Last payment"), rows);
	}

	// ---- Helpers ----

	private List<InvoiceDetails> openBills() {
		return em.createQuery("from InvoiceDetails inv where inv.outstanding > :open"
				+ " and (inv.activeStatus is null or inv.activeStatus <> 0)", InvoiceDetails.class)
				.setParameter("open", OPEN).getResultList();
	}

	private Map<Integer, CustomerInfo> customers(Set<Integer> ids) {
		Map<Integer, CustomerInfo> customers = new HashMap<>();
		List<Integer> real = ids.stream().filter(id -> id != WALK_IN).toList();
		if (!real.isEmpty()) {
			customerRepo.findAllById(real).forEach(c -> customers.put(c.getId(), c));
		}
		return customers;
	}

	/** Latest payment that still counts, per customer, over all of the customer's bills at this outlet. */
	private Map<Integer, Date> lastPaymentDates() {
		Map<Integer, Date> dates = new HashMap<>();
		List<Object[]> rows = em.createQuery("select inv.cusID, max(pay.payDate) from PaymentDetails pay, InvoiceDetails inv"
				+ " where pay.invoiceNumber = inv.invoiceNumber and pay.paidAmount > 0"
				+ " and (pay.payStatus is null or pay.payStatus <> 'RECALLED')"
				+ " and (inv.activeStatus is null or inv.activeStatus <> 0) group by inv.cusID", Object[].class)
				.getResultList();
		for (Object[] row : rows) {
			int id = ((Number) row[0]).intValue();
			dates.merge(id > 0 ? id : WALK_IN, (Date) row[1], (a, b) -> a.after(b) ? a : b);
		}
		return dates;
	}

	private List<StatementLine> statement(List<InvoiceDetails> bills) {
		record Entry(Date date, int order, String type, String reference, String billReceiptNo, String detail,
				BigDecimal debit, BigDecimal credit) {
		}
		List<Entry> entries = new ArrayList<>();
		Map<String, InvoiceDetails> byInvoiceNo = new HashMap<>();
		for (InvoiceDetails inv : bills) {
			byInvoiceNo.put(inv.getInvoiceNumber(), inv);
			entries.add(new Entry(inv.getInvoicedDate(), 0, "BILL", inv.getReceiptNo(), inv.getReceiptNo(), "Bill",
					money(inv.getInvoicedAmount()), money(0)));
		}
		if (!byInvoiceNo.isEmpty()) {
			List<PaymentDetails> payments = em.createQuery("from PaymentDetails pay where pay.invoiceNumber in :nos"
					+ " and pay.paidAmount > 0", PaymentDetails.class).setParameter("nos", byInvoiceNo.keySet()).getResultList();
			for (PaymentDetails pay : payments) {
				InvoiceDetails inv = byInvoiceNo.get(pay.getInvoiceNumber());
				String receipt = inv == null ? null : inv.getReceiptNo();
				entries.add(new Entry(pay.getPayDate(), 1, "PAYMENT", pay.getReceiptNumber(), receipt,
						"Payment (" + (pay.getPaidMode() == null ? "-" : pay.getPaidMode()) + ")", money(0),
						money(pay.getPaidAmount())));
				if (RECALLED.equals(pay.getPayStatus())) {
					String reason = pay.getRecallReason() == null || pay.getRecallReason().isBlank() ? ""
							: ": " + pay.getRecallReason().trim();
					entries.add(new Entry(pay.getRecalledDate() != null ? pay.getRecalledDate() : pay.getPayDate(), 2,
							"RECALL", pay.getReceiptNumber(), receipt, "Payment recalled" + reason,
							money(pay.getPaidAmount()), money(0)));
				}
			}
		}
		entries.sort(Comparator.comparing(Entry::date, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparingInt(Entry::order));
		List<StatementLine> lines = new ArrayList<>();
		BigDecimal balance = money(0);
		for (Entry e : entries) {
			balance = balance.add(e.debit()).subtract(e.credit());
			lines.add(new StatementLine(instant(e.date()), e.type(), e.reference(), e.billReceiptNo(), e.detail(),
					e.debit(), e.credit(), balance));
		}
		java.util.Collections.reverse(lines);
		return lines;
	}

	private static boolean matches(CreditRow row, String term) {
		return contains(row.customerName(), term) || contains(row.mobileNo(), term)
				|| (row.customerId() != WALK_IN && String.valueOf(row.customerId()).equals(term));
	}

	private static boolean contains(String value, String term) {
		return value != null && value.toLowerCase(Locale.ROOT).contains(term);
	}

	private static String name(int customerId, CustomerInfo customer) {
		if (customerId == WALK_IN) {
			return "Walk-in (no customer)";
		}
		String name = customer == null ? null : customer.getCustomerName();
		return name == null || name.isBlank() ? "Customer #" + customerId : name.trim();
	}

	private static long age(Date date, LocalDate today) {
		if (date == null) {
			return 0;
		}
		return Math.max(0, ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), today));
	}

	static String band(long ageDays) {
		return ageDays <= 30 ? BANDS.get(0) : ageDays <= 60 ? BANDS.get(1) : ageDays <= 90 ? BANDS.get(2) : BANDS.get(3);
	}

	private static BigDecimal money(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
	}

	private static String day(Date date) {
		return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
	}

	private static String instant(Date date) {
		return date == null ? null : date.toInstant().toString();
	}
}
