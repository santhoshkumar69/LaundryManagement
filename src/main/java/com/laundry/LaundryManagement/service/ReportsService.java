package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.auth.AuthService;
import com.laundry.LaundryManagement.auth.AuthService.OutletOption;
import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.excel.ExcelSheet;
import com.laundry.LaundryManagement.model.BilledComponents;
import com.laundry.LaundryManagement.model.Expense;
import com.laundry.LaundryManagement.model.InvoiceDetails;
import com.laundry.LaundryManagement.model.PaymentDetails;
import com.laundry.LaundryManagement.repository.ExpenseRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Accounts &amp; Reports: outlet-scoped (or multi-outlet roll-up) management reports built from bills,
 * payments, expenses and order lines.
 */
@Service
public class ReportsService {

	private static final double OPEN = 0.004;
	private static final String RECALLED = "RECALLED";
	private static final List<String> BANDS = CreditService.BANDS;

	public record NamedAmount(String name, BigDecimal amount) {
	}

	public record DailySales(String from, String to, int bills, BigDecimal billed, BigDecimal discounts,
			BigDecimal extraCharges, BigDecimal collected, BigDecimal recalled, BigDecimal expenses,
			BigDecimal creditOutstanding, Map<String, BigDecimal> byMode, Map<String, BigDecimal> bySource,
			List<DayRow> days) {
	}

	public record DayRow(String date, int bills, BigDecimal billed, BigDecimal collected, BigDecimal recalled,
			BigDecimal expenses) {
	}

	public record SalesSummary(String from, String to, int bills, BigDecimal billed, BigDecimal collected,
			BigDecimal discounts, BigDecimal extraCharges, BigDecimal recalled, BigDecimal expenses,
			BigDecimal creditOutstanding, BigDecimal netCash, Map<String, BigDecimal> byMode,
			Map<String, BigDecimal> bySource, List<DayRow> days) {
	}

	public record ItemRow(String itemName, int lines, BigDecimal qty, BigDecimal amount) {
	}

	public record ItemMix(String from, String to, List<ItemRow> items, BigDecimal totalAmount, BigDecimal totalQty) {
	}

	public record StatusAge(String status, long count, BigDecimal outstanding, long avgAgeDays, long maxAgeDays) {
	}

	public record Operations(LocalDate asOf, long overdueDeliveries, BigDecimal overdueValue, long openOrders,
			BigDecimal openOutstanding, List<StatusAge> byStatus) {
	}

	public record ProfitLoss(String from, String to, BigDecimal collected, BigDecimal expenses, BigDecimal net,
			Map<String, BigDecimal> expensesByCategory, Map<String, BigDecimal> collectedByMode) {
	}

	public record CreditAging(BigDecimal totalBalance, int customers, int openBills, Map<String, BigDecimal> bands,
			List<CreditService.CreditRow> rows) {
	}

	public record CreditCollection(String date, String payMode, String source, String customerName, String billReceiptNo,
			String paymentReceiptNo, BigDecimal amount, String collectedBy, String outletName) {
	}

	public record CreditCollections(String from, String to, BigDecimal total, Map<String, BigDecimal> byMode,
			List<CreditCollection> rows) {
	}

	public record RecallRow(String recalledAt, String payDate, String billReceiptNo, String paymentReceiptNo,
			String payMode, String source, BigDecimal amount, String reason, String recalledBy, String outletName) {
	}

	public record RecallReport(String from, String to, BigDecimal total, int count, List<RecallRow> rows) {
	}

	/** One outlet's slice plus company totals when several outlets are chosen. */
	public record OutletReport<T>(int outletId, String outletCode, String outletName, T data) {
	}

	public record MultiReport<T>(List<OutletReport<T>> outlets, T totals) {
	}

	@Autowired
	private AuthService authService;

	@Autowired
	private CreditService creditService;

	@Autowired
	private ExpenseRepo expenseRepo;

	@Autowired
	private ExpenseService expenseService;

	@PersistenceContext
	private EntityManager em;

	public List<OutletOption> reportOutlets() {
		return authService.outletsForCurrentUser();
	}

	public MultiReport<DailySales> daily(String from, String to, List<Integer> outletIds) {
		LocalDate start = parseFrom(from);
		LocalDate end = parseTo(to, start);
		return multi(outletIds, o -> dailyHere(start, end), this::sumDaily);
	}

	public MultiReport<SalesSummary> sales(String from, String to, List<Integer> outletIds) {
		LocalDate start = parseFrom(from);
		LocalDate end = parseTo(to, start);
		return multi(outletIds, o -> salesHere(start, end), this::sumSales);
	}

	public MultiReport<ItemMix> items(String from, String to, List<Integer> outletIds) {
		LocalDate start = parseFrom(from);
		LocalDate end = parseTo(to, start);
		return multi(outletIds, o -> itemsHere(start, end), this::sumItems);
	}

	public MultiReport<Operations> operations(List<Integer> outletIds) {
		return multi(outletIds, o -> operationsHere(), this::sumOps);
	}

	public MultiReport<ProfitLoss> profitLoss(String from, String to, List<Integer> outletIds) {
		LocalDate start = parseFrom(from);
		LocalDate end = parseTo(to, start);
		return multi(outletIds, o -> plHere(start, end), this::sumPl);
	}

	public MultiReport<CreditAging> creditAging(List<Integer> outletIds) {
		return multi(outletIds, o -> {
			CreditService.CreditList list = creditService.list(null, null, null);
			return new CreditAging(list.summary().totalBalance(), list.summary().customers(), list.summary().openBills(),
					list.summary().bands(), list.rows());
		}, this::sumAging);
	}

	public MultiReport<CreditCollections> creditCollections(String from, String to, List<Integer> outletIds) {
		LocalDate start = parseFrom(from);
		LocalDate end = parseTo(to, start);
		return multi(outletIds, o -> collectionsHere(start, end, o.outletName()), this::sumCollections);
	}

	public MultiReport<RecallReport> recalls(String from, String to, List<Integer> outletIds) {
		LocalDate start = parseFrom(from);
		LocalDate end = parseTo(to, start);
		return multi(outletIds, o -> recallsHere(start, end, o.outletName()), this::sumRecalls);
	}

	public byte[] exportDaily(String from, String to, List<Integer> outletIds) {
		MultiReport<DailySales> report = daily(from, to, outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<DailySales> o : report.outlets()) {
			DailySales d = o.data();
			rows.add(List.of(o.outletCode(), o.outletName(), d.from(), d.to(), d.bills(), d.billed(), d.collected(),
					d.recalled(), d.expenses(), d.creditOutstanding()));
			for (NamedAmount m : toNamed(d.byMode())) {
				rows.add(List.of(o.outletCode(), "by mode", m.name(), "", "", "", m.amount(), "", "", ""));
			}
			for (NamedAmount m : toNamed(d.bySource())) {
				rows.add(List.of(o.outletCode(), "by source", m.name(), "", "", "", m.amount(), "", "", ""));
			}
		}
		return ExcelSheet.write("Daily Sales", List.of("Outlet", "Name / breakdown", "From", "To", "Bills", "Billed",
				"Collected", "Recalled", "Expenses", "Credit outstanding"), rows);
	}

	public byte[] exportSales(String from, String to, List<Integer> outletIds) {
		MultiReport<SalesSummary> report = sales(from, to, outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<SalesSummary> o : report.outlets()) {
			SalesSummary s = o.data();
			rows.add(List.of(o.outletCode(), o.outletName(), s.from(), s.to(), s.bills(), s.billed(), s.collected(),
					s.discounts(), s.extraCharges(), s.recalled(), s.expenses(), s.netCash(), s.creditOutstanding()));
			for (DayRow day : s.days()) {
				rows.add(List.of(o.outletCode(), day.date(), "", "", day.bills(), day.billed(), day.collected(), "", "",
						day.recalled(), day.expenses(), "", ""));
			}
		}
		return ExcelSheet.write("Sales Summary", List.of("Outlet", "Name / day", "From", "To", "Bills", "Billed",
				"Collected", "Discounts", "Extra charges", "Recalled", "Expenses", "Net cash", "Credit outstanding"),
				rows);
	}

	public byte[] exportItems(String from, String to, List<Integer> outletIds) {
		MultiReport<ItemMix> report = items(from, to, outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<ItemMix> o : report.outlets()) {
			for (ItemRow item : o.data().items()) {
				rows.add(List.of(o.outletCode(), o.outletName(), item.itemName(), item.lines(), item.qty(), item.amount()));
			}
		}
		return ExcelSheet.write("Item Mix", List.of("Outlet code", "Outlet", "Item", "Lines", "Qty", "Amount"), rows);
	}

	public byte[] exportOperations(List<Integer> outletIds) {
		MultiReport<Operations> report = operations(outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<Operations> o : report.outlets()) {
			Operations op = o.data();
			rows.add(List.of(o.outletCode(), o.outletName(), "summary", op.overdueDeliveries(), op.overdueValue(),
					op.openOrders(), op.openOutstanding(), "", "", ""));
			for (StatusAge s : op.byStatus()) {
				rows.add(List.of(o.outletCode(), o.outletName(), s.status(), s.count(), s.outstanding(), "", "",
						s.avgAgeDays(), s.maxAgeDays(), ""));
			}
		}
		return ExcelSheet.write("Operations", List.of("Outlet code", "Outlet", "Status", "Count", "Outstanding",
				"Open orders", "Open outstanding", "Avg age days", "Max age days", ""), rows);
	}

	public byte[] exportPl(String from, String to, List<Integer> outletIds) {
		MultiReport<ProfitLoss> report = profitLoss(from, to, outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<ProfitLoss> o : report.outlets()) {
			ProfitLoss p = o.data();
			rows.add(List.of(o.outletCode(), o.outletName(), p.from(), p.to(), p.collected(), p.expenses(), p.net()));
			for (NamedAmount m : toNamed(p.expensesByCategory())) {
				rows.add(List.of(o.outletCode(), "expense", m.name(), "", "", m.amount(), ""));
			}
			for (NamedAmount m : toNamed(p.collectedByMode())) {
				rows.add(List.of(o.outletCode(), "collected", m.name(), "", m.amount(), "", ""));
			}
		}
		return ExcelSheet.write("P and L", List.of("Outlet", "Name / type", "From / category", "To", "Collected",
				"Expenses", "Net"), rows);
	}

	public byte[] exportExpenses(String from, String to) {
		ExpenseService.ExpenseList list = expenseService.list(from, to, null, null);
		List<List<Object>> rows = new ArrayList<>();
		for (ExpenseService.ExpenseRow e : list.entries()) {
			rows.add(List.of(e.expenseDate(), e.expenseName(), e.category(), e.subcategory() == null ? "" : e.subcategory(),
					e.amount(), e.paymentMode() == null ? "" : e.paymentMode(), e.receiptNo() == null ? "" : e.receiptNo(),
					e.createdBy() == null ? "" : e.createdBy()));
		}
		return ExcelSheet.write("Expenses", List.of("Date", "Name", "Category", "Subcategory", "Amount", "Payment mode",
				"Receipt", "Created by"), rows);
	}

	public byte[] exportCreditAging(List<Integer> outletIds) {
		MultiReport<CreditAging> report = creditAging(outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<CreditAging> o : report.outlets()) {
			for (CreditService.CreditRow r : o.data().rows()) {
				rows.add(List.of(o.outletCode(), o.outletName(), r.customerId() == 0 ? "" : r.customerId(), r.customerName(),
						r.mobileNo() == null ? "" : r.mobileNo(), r.openBills(), r.balance(),
						r.oldestBillDate() == null ? "" : r.oldestBillDate(), r.oldestAgeDays(), r.band(),
						r.lastPaymentDate() == null ? "" : r.lastPaymentDate()));
			}
		}
		return ExcelSheet.write("Credit Aging", List.of("Outlet code", "Outlet", "Customer ID", "Customer", "Mobile",
				"Open bills", "Balance", "Oldest bill", "Age days", "Band", "Last payment"), rows);
	}

	public byte[] exportCreditCollections(String from, String to, List<Integer> outletIds) {
		MultiReport<CreditCollections> report = creditCollections(from, to, outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<CreditCollections> o : report.outlets()) {
			for (CreditCollection c : o.data().rows()) {
				rows.add(List.of(o.outletCode(), o.outletName(), c.date(), c.customerName(), c.billReceiptNo(),
						c.paymentReceiptNo(), c.payMode(), c.source(), c.amount(), c.collectedBy() == null ? "" : c.collectedBy()));
			}
		}
		return ExcelSheet.write("Credit Collections", List.of("Outlet code", "Outlet", "Date", "Customer", "Bill",
				"Payment receipt", "Mode", "Source", "Amount", "By"), rows);
	}

	public byte[] exportStatement(int customerId) {
		CreditService.CustomerCredit c = creditService.customer(customerId);
		List<List<Object>> rows = new ArrayList<>();
		for (CreditService.StatementLine line : c.statement()) {
			rows.add(List.of(line.date() == null ? "" : line.date(), line.type(), line.detail(),
					line.reference() == null ? "" : line.reference(), line.billReceiptNo() == null ? "" : line.billReceiptNo(),
					line.debit(), line.credit(), line.balance()));
		}
		return ExcelSheet.write("Statement", List.of("Date", "Type", "Detail", "Reference", "Bill", "Billed", "Paid",
				"Balance"), rows);
	}

	public byte[] exportRecalls(String from, String to, List<Integer> outletIds) {
		MultiReport<RecallReport> report = recalls(from, to, outletIds);
		List<List<Object>> rows = new ArrayList<>();
		for (OutletReport<RecallReport> o : report.outlets()) {
			for (RecallRow r : o.data().rows()) {
				rows.add(List.of(o.outletCode(), o.outletName(), r.recalledAt(), r.payDate(), r.billReceiptNo(),
						r.paymentReceiptNo(), r.payMode(), r.source(), r.amount(), r.reason() == null ? "" : r.reason(),
						r.recalledBy() == null ? "" : r.recalledBy()));
			}
		}
		return ExcelSheet.write("Recalls", List.of("Outlet code", "Outlet", "Recalled at", "Paid at", "Bill",
				"Payment receipt", "Mode", "Source", "Amount", "Reason", "By"), rows);
	}

	// ---- Per-outlet builders ----

	private DailySales dailyHere(LocalDate start, LocalDate end) {
		SalesSummary s = salesHere(start, end);
		return new DailySales(s.from(), s.to(), s.bills(), s.billed(), s.discounts(), s.extraCharges(), s.collected(),
				s.recalled(), s.expenses(), s.creditOutstanding(), s.byMode(), s.bySource(), s.days());
	}

	private SalesSummary salesHere(LocalDate start, LocalDate end) {
		Date from = startOf(start);
		Date to = startOf(end.plusDays(1));
		List<InvoiceDetails> bills = em.createQuery(
				"from InvoiceDetails inv where inv.invoicedDate >= :from and inv.invoicedDate < :to"
						+ " and (inv.activeStatus is null or inv.activeStatus <> 0)",
				InvoiceDetails.class).setParameter("from", from).setParameter("to", to).getResultList();
		int billCount = bills.size();
		BigDecimal billed = money(0);
		BigDecimal discounts = money(0);
		BigDecimal extras = money(0);
		Map<String, DayAcc> days = new TreeMap<>();
		for (InvoiceDetails inv : bills) {
			billed = billed.add(money(inv.getInvoicedAmount()));
			discounts = discounts.add(money(inv.getDiscountAmount()));
			extras = extras.add(money(inv.getExtraChargesAmt() == null ? 0 : inv.getExtraChargesAmt()));
			String day = dayKey(inv.getInvoicedDate());
			DayAcc acc = days.computeIfAbsent(day, k -> new DayAcc());
			acc.bills++;
			acc.billed = acc.billed.add(money(inv.getInvoicedAmount()));
		}

		List<PaymentDetails> payments = em.createQuery(
				"from PaymentDetails pay where pay.payDate >= :from and pay.payDate < :to and pay.paidAmount > 0",
				PaymentDetails.class).setParameter("from", from).setParameter("to", to).getResultList();
		BigDecimal collected = money(0);
		BigDecimal recalled = money(0);
		Map<String, BigDecimal> byMode = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		Map<String, BigDecimal> bySource = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (PaymentDetails pay : payments) {
			boolean isRecall = RECALLED.equals(pay.getPayStatus());
			String day = dayKey(isRecall && pay.getRecalledDate() != null ? pay.getRecalledDate() : pay.getPayDate());
			DayAcc acc = days.computeIfAbsent(day, k -> new DayAcc());
			if (isRecall) {
				// Count recall against the day it was recalled when available.
				String recallDay = dayKey(pay.getRecalledDate() != null ? pay.getRecalledDate() : pay.getPayDate());
				DayAcc recallAcc = days.computeIfAbsent(recallDay, k -> new DayAcc());
				recallAcc.recalled = recallAcc.recalled.add(money(pay.getPaidAmount()));
				recalled = recalled.add(money(pay.getPaidAmount()));
				continue;
			}
			collected = collected.add(money(pay.getPaidAmount()));
			acc.collected = acc.collected.add(money(pay.getPaidAmount()));
			String mode = blankTo(pay.getPaidMode(), "Other");
			String source = blankTo(pay.getPaySource(), "BILLING");
			byMode.merge(mode, money(pay.getPaidAmount()), BigDecimal::add);
			bySource.merge(source, money(pay.getPaidAmount()), BigDecimal::add);
		}

		List<Expense> expenses = expenseRepo.findLive(start, end, null, null);
		BigDecimal expenseTotal = money(0);
		for (Expense e : expenses) {
			expenseTotal = expenseTotal.add(e.getAmount());
			String day = e.getExpenseDate().toString();
			DayAcc acc = days.computeIfAbsent(day, k -> new DayAcc());
			acc.expenses = acc.expenses.add(e.getAmount());
		}

		BigDecimal outstanding = money(((Number) em.createQuery(
				"select coalesce(sum(inv.outstanding), 0) from InvoiceDetails inv where inv.outstanding > :open"
						+ " and (inv.activeStatus is null or inv.activeStatus <> 0)")
				.setParameter("open", OPEN).getSingleResult()).doubleValue());

		List<DayRow> dayRows = days.entrySet().stream()
				.map(e -> new DayRow(e.getKey(), e.getValue().bills, e.getValue().billed, e.getValue().collected,
						e.getValue().recalled, e.getValue().expenses))
				.toList();
		BigDecimal net = collected.subtract(expenseTotal);
		return new SalesSummary(start.toString(), end.toString(), billCount, billed, collected, discounts, extras,
				recalled, expenseTotal, outstanding, net, byMode, bySource, dayRows);
	}

	private ItemMix itemsHere(LocalDate start, LocalDate end) {
		Date from = startOf(start);
		Date to = startOf(end.plusDays(1));
		List<BilledComponents> lines = em.createQuery(
				"select c from BilledComponents c, InvoiceDetails inv where c.invoiceNo = inv.invoiceNumber"
						+ " and inv.invoicedDate >= :from and inv.invoicedDate < :to"
						+ " and (inv.activeStatus is null or inv.activeStatus <> 0)",
				BilledComponents.class).setParameter("from", from).setParameter("to", to).getResultList();
		Map<String, ItemAcc> byItem = new LinkedHashMap<>();
		for (BilledComponents c : lines) {
			String name = blankTo(c.getItemName(), "(unnamed)");
			ItemAcc acc = byItem.computeIfAbsent(name, k -> new ItemAcc());
			acc.lines++;
			acc.amount = acc.amount.add(money(c.getTotalAmt()));
			acc.qty = acc.qty.add(money(parseQty(c.getQty())));
		}
		List<ItemRow> items = byItem.entrySet().stream()
				.map(e -> new ItemRow(e.getKey(), e.getValue().lines, e.getValue().qty, e.getValue().amount))
				.sorted(Comparator.comparing(ItemRow::amount).reversed()).toList();
		BigDecimal totalAmt = items.stream().map(ItemRow::amount).reduce(money(0), BigDecimal::add);
		BigDecimal totalQty = items.stream().map(ItemRow::qty).reduce(money(0), BigDecimal::add);
		return new ItemMix(start.toString(), end.toString(), items, totalAmt, totalQty);
	}

	private Operations operationsHere() {
		LocalDate today = LocalDate.now();
		Date startToday = startOf(today);
		List<InvoiceDetails> open = em.createQuery(
				"from InvoiceDetails inv where (inv.activeStatus is null or inv.activeStatus <> 0)"
						+ " and (inv.orderStatus is null or inv.orderStatus <> 'COLLECTED')",
				InvoiceDetails.class).getResultList();
		Map<String, List<InvoiceDetails>> byStatus = open.stream()
				.collect(Collectors.groupingBy(inv -> blankTo(inv.getOrderStatus(), "ORDER_PLACED"), LinkedHashMap::new,
						Collectors.toList()));
		List<StatusAge> ages = new ArrayList<>();
		for (Map.Entry<String, List<InvoiceDetails>> e : byStatus.entrySet()) {
			long max = 0;
			long sum = 0;
			BigDecimal outstanding = money(0);
			for (InvoiceDetails inv : e.getValue()) {
				long age = ageDays(inv.getInvoicedDate(), today);
				max = Math.max(max, age);
				sum += age;
				outstanding = outstanding.add(money(inv.getOutstanding()));
			}
			long count = e.getValue().size();
			ages.add(new StatusAge(e.getKey(), count, outstanding, count == 0 ? 0 : sum / count, max));
		}
		ages.sort(Comparator.comparing(StatusAge::status));
		long overdue = 0;
		BigDecimal overdueValue = money(0);
		for (InvoiceDetails inv : open) {
			if (inv.getDeliveryDate() != null && inv.getDeliveryDate().before(startToday)) {
				overdue++;
				overdueValue = overdueValue.add(money(inv.getOutstanding()));
			}
		}
		BigDecimal openOutstanding = open.stream().map(inv -> money(inv.getOutstanding())).reduce(money(0), BigDecimal::add);
		return new Operations(today, overdue, overdueValue, open.size(), openOutstanding, ages);
	}

	private ProfitLoss plHere(LocalDate start, LocalDate end) {
		SalesSummary s = salesHere(start, end);
		ExpenseService.ExpenseList expenses = expenseService.list(start.toString(), end.toString(), null, null);
		return new ProfitLoss(start.toString(), end.toString(), s.collected(), s.expenses(), s.netCash(),
				expenses.byCategory(), s.byMode());
	}

	private CreditCollections collectionsHere(LocalDate start, LocalDate end, String outletName) {
		Date from = startOf(start);
		Date to = startOf(end.plusDays(1));
		List<Object[]> rows = em.createQuery(
				"select pay, inv from PaymentDetails pay, InvoiceDetails inv"
						+ " where pay.invoiceNumber = inv.invoiceNumber and pay.paidAmount > 0"
						+ " and pay.payDate >= :from and pay.payDate < :to"
						+ " and (pay.payStatus is null or pay.payStatus <> 'RECALLED')"
						+ " and pay.paySource in ('DELIVERY', 'CREDIT')"
						+ " and (inv.activeStatus is null or inv.activeStatus <> 0)"
						+ " order by pay.payDate desc, pay.payID desc",
				Object[].class).setParameter("from", from).setParameter("to", to).getResultList();
		List<CreditCollection> list = new ArrayList<>();
		Map<String, BigDecimal> byMode = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		BigDecimal total = money(0);
		Set<Integer> cusIds = rows.stream().map(r -> ((InvoiceDetails) r[1]).getCusID()).filter(id -> id > 0)
				.collect(Collectors.toSet());
		Map<Integer, String> names = customerNames(cusIds);
		for (Object[] row : rows) {
			PaymentDetails pay = (PaymentDetails) row[0];
			InvoiceDetails inv = (InvoiceDetails) row[1];
			String customerName = inv.getCusID() <= 0 ? "Walk-in (no customer)"
					: names.getOrDefault(inv.getCusID(), "Customer #" + inv.getCusID());
			BigDecimal amt = money(pay.getPaidAmount());
			total = total.add(amt);
			byMode.merge(blankTo(pay.getPaidMode(), "Other"), amt, BigDecimal::add);
			list.add(new CreditCollection(instant(pay.getPayDate()), blankTo(pay.getPaidMode(), "Other"),
					blankTo(pay.getPaySource(), "DELIVERY"), customerName, inv.getReceiptNo(), pay.getReceiptNumber(), amt,
					pay.getCreatedBy(), outletName));
		}
		return new CreditCollections(start.toString(), end.toString(), total, byMode, list);
	}

	private RecallReport recallsHere(LocalDate start, LocalDate end, String outletName) {
		Date from = startOf(start);
		Date to = startOf(end.plusDays(1));
		List<Object[]> rows = em.createQuery(
				"select pay, inv from PaymentDetails pay, InvoiceDetails inv"
						+ " where pay.invoiceNumber = inv.invoiceNumber and pay.payStatus = 'RECALLED'"
						+ " and ((pay.recalledDate is not null and pay.recalledDate >= :from and pay.recalledDate < :to)"
						+ " or (pay.recalledDate is null and pay.payDate >= :from and pay.payDate < :to))"
						+ " order by pay.payID desc",
				Object[].class).setParameter("from", from).setParameter("to", to).getResultList();
		List<RecallRow> list = new ArrayList<>();
		BigDecimal total = money(0);
		for (Object[] row : rows) {
			PaymentDetails pay = (PaymentDetails) row[0];
			InvoiceDetails inv = (InvoiceDetails) row[1];
			total = total.add(money(pay.getPaidAmount()));
			list.add(new RecallRow(instant(pay.getRecalledDate() != null ? pay.getRecalledDate() : pay.getPayDate()),
					instant(pay.getPayDate()), inv.getReceiptNo(), pay.getReceiptNumber(),
					blankTo(pay.getPaidMode(), "Other"), blankTo(pay.getPaySource(), "BILLING"), money(pay.getPaidAmount()),
					pay.getRecallReason(), pay.getRecalledBy(), outletName));
		}
		list.sort(Comparator.comparing(RecallRow::recalledAt, Comparator.nullsLast(Comparator.reverseOrder())));
		return new RecallReport(start.toString(), end.toString(), total, list.size(), list);
	}

	// ---- Multi-outlet ----

	private <T> MultiReport<T> multi(List<Integer> requested, java.util.function.Function<OutletOption, T> build,
			java.util.function.BinaryOperator<T> sum) {
		List<OutletOption> allowed = authService.outletsForCurrentUser();
		Map<Integer, OutletOption> byId = allowed.stream().collect(Collectors.toMap(OutletOption::id, o -> o, (a, b) -> a,
				LinkedHashMap::new));
		List<OutletOption> chosen;
		if (requested == null || requested.isEmpty()) {
			Integer current = RequestScope.requireOutletId();
			OutletOption one = byId.get(current);
			if (one == null) {
				throw new IllegalArgumentException("Current outlet is not available for reports");
			}
			chosen = List.of(one);
		} else {
			chosen = new ArrayList<>();
			for (Integer id : requested.stream().distinct().toList()) {
				OutletOption o = byId.get(id);
				if (o == null) {
					throw new IllegalArgumentException("Outlet " + id + " is not available for your login");
				}
				chosen.add(o);
			}
		}
		List<OutletReport<T>> slices = new ArrayList<>();
		T totals = null;
		for (OutletOption o : chosen) {
			T data = RequestScope.asOutlet(o.id(), () -> build.apply(o));
			slices.add(new OutletReport<>(o.id(), o.outletCode(), o.outletName(), data));
			totals = totals == null ? data : sum.apply(totals, data);
		}
		return new MultiReport<>(slices, totals);
	}

	private DailySales sumDaily(DailySales a, DailySales b) {
		return new DailySales(a.from(), a.to(), a.bills() + b.bills(), a.billed().add(b.billed()),
				a.discounts().add(b.discounts()), a.extraCharges().add(b.extraCharges()), a.collected().add(b.collected()),
				a.recalled().add(b.recalled()), a.expenses().add(b.expenses()),
				a.creditOutstanding().add(b.creditOutstanding()), mergeMoney(a.byMode(), b.byMode()),
				mergeMoney(a.bySource(), b.bySource()), mergeDays(a.days(), b.days()));
	}

	private SalesSummary sumSales(SalesSummary a, SalesSummary b) {
		return new SalesSummary(a.from(), a.to(), a.bills() + b.bills(), a.billed().add(b.billed()),
				a.collected().add(b.collected()), a.discounts().add(b.discounts()), a.extraCharges().add(b.extraCharges()),
				a.recalled().add(b.recalled()), a.expenses().add(b.expenses()),
				a.creditOutstanding().add(b.creditOutstanding()), a.netCash().add(b.netCash()),
				mergeMoney(a.byMode(), b.byMode()), mergeMoney(a.bySource(), b.bySource()), mergeDays(a.days(), b.days()));
	}

	private ItemMix sumItems(ItemMix a, ItemMix b) {
		Map<String, ItemAcc> map = new LinkedHashMap<>();
		for (ItemRow r : a.items()) {
			ItemAcc acc = map.computeIfAbsent(r.itemName(), k -> new ItemAcc());
			acc.lines += r.lines();
			acc.qty = acc.qty.add(r.qty());
			acc.amount = acc.amount.add(r.amount());
		}
		for (ItemRow r : b.items()) {
			ItemAcc acc = map.computeIfAbsent(r.itemName(), k -> new ItemAcc());
			acc.lines += r.lines();
			acc.qty = acc.qty.add(r.qty());
			acc.amount = acc.amount.add(r.amount());
		}
		List<ItemRow> items = map.entrySet().stream()
				.map(e -> new ItemRow(e.getKey(), e.getValue().lines, e.getValue().qty, e.getValue().amount))
				.sorted(Comparator.comparing(ItemRow::amount).reversed()).toList();
		return new ItemMix(a.from(), a.to(), items, a.totalAmount().add(b.totalAmount()), a.totalQty().add(b.totalQty()));
	}

	private Operations sumOps(Operations a, Operations b) {
		Map<String, StatusAge> map = new LinkedHashMap<>();
		for (StatusAge s : a.byStatus()) {
			map.put(s.status(), s);
		}
		for (StatusAge s : b.byStatus()) {
			StatusAge prev = map.get(s.status());
			if (prev == null) {
				map.put(s.status(), s);
			} else {
				long count = prev.count() + s.count();
				long avg = count == 0 ? 0 : ((prev.avgAgeDays() * prev.count()) + (s.avgAgeDays() * s.count())) / count;
				map.put(s.status(), new StatusAge(s.status(), count, prev.outstanding().add(s.outstanding()), avg,
						Math.max(prev.maxAgeDays(), s.maxAgeDays())));
			}
		}
		return new Operations(a.asOf(), a.overdueDeliveries() + b.overdueDeliveries(),
				a.overdueValue().add(b.overdueValue()), a.openOrders() + b.openOrders(),
				a.openOutstanding().add(b.openOutstanding()), new ArrayList<>(map.values()));
	}

	private ProfitLoss sumPl(ProfitLoss a, ProfitLoss b) {
		return new ProfitLoss(a.from(), a.to(), a.collected().add(b.collected()), a.expenses().add(b.expenses()),
				a.net().add(b.net()), mergeMoney(a.expensesByCategory(), b.expensesByCategory()),
				mergeMoney(a.collectedByMode(), b.collectedByMode()));
	}

	private CreditAging sumAging(CreditAging a, CreditAging b) {
		List<CreditService.CreditRow> rows = new ArrayList<>(a.rows());
		rows.addAll(b.rows());
		rows.sort(Comparator.comparing(CreditService.CreditRow::balance).reversed());
		return new CreditAging(a.totalBalance().add(b.totalBalance()), a.customers() + b.customers(),
				a.openBills() + b.openBills(), mergeMoney(a.bands(), b.bands()), rows);
	}

	private CreditCollections sumCollections(CreditCollections a, CreditCollections b) {
		List<CreditCollection> rows = new ArrayList<>(a.rows());
		rows.addAll(b.rows());
		rows.sort(Comparator.comparing(CreditCollection::date, Comparator.nullsLast(Comparator.reverseOrder())));
		return new CreditCollections(a.from(), a.to(), a.total().add(b.total()), mergeMoney(a.byMode(), b.byMode()), rows);
	}

	private RecallReport sumRecalls(RecallReport a, RecallReport b) {
		List<RecallRow> rows = new ArrayList<>(a.rows());
		rows.addAll(b.rows());
		rows.sort(Comparator.comparing(RecallRow::recalledAt, Comparator.nullsLast(Comparator.reverseOrder())));
		return new RecallReport(a.from(), a.to(), a.total().add(b.total()), a.count() + b.count(), rows);
	}

	// ---- Helpers ----

	private Map<Integer, String> customerNames(Set<Integer> ids) {
		Map<Integer, String> names = new LinkedHashMap<>();
		if (ids == null || ids.isEmpty()) {
			return names;
		}
		List<Object[]> rows = em.createQuery("select c.id, c.customerName from CustomerInfo c where c.id in :ids",
				Object[].class).setParameter("ids", ids).getResultList();
		for (Object[] row : rows) {
			names.put((Integer) row[0], row[1] == null ? null : row[1].toString());
		}
		return names;
	}

	private static List<DayRow> mergeDays(List<DayRow> a, List<DayRow> b) {
		Map<String, DayAcc> map = new TreeMap<>();
		for (DayRow d : a) {
			DayAcc acc = map.computeIfAbsent(d.date(), k -> new DayAcc());
			acc.bills += d.bills();
			acc.billed = acc.billed.add(d.billed());
			acc.collected = acc.collected.add(d.collected());
			acc.recalled = acc.recalled.add(d.recalled());
			acc.expenses = acc.expenses.add(d.expenses());
		}
		for (DayRow d : b) {
			DayAcc acc = map.computeIfAbsent(d.date(), k -> new DayAcc());
			acc.bills += d.bills();
			acc.billed = acc.billed.add(d.billed());
			acc.collected = acc.collected.add(d.collected());
			acc.recalled = acc.recalled.add(d.recalled());
			acc.expenses = acc.expenses.add(d.expenses());
		}
		return map.entrySet().stream().map(e -> new DayRow(e.getKey(), e.getValue().bills, e.getValue().billed,
				e.getValue().collected, e.getValue().recalled, e.getValue().expenses)).toList();
	}

	private static Map<String, BigDecimal> mergeMoney(Map<String, BigDecimal> a, Map<String, BigDecimal> b) {
		Map<String, BigDecimal> out = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		if (a != null) {
			out.putAll(a);
		}
		if (b != null) {
			b.forEach((k, v) -> out.merge(k, v, BigDecimal::add));
		}
		return out;
	}

	private static List<NamedAmount> toNamed(Map<String, BigDecimal> map) {
		return map.entrySet().stream().map(e -> new NamedAmount(e.getKey(), e.getValue())).toList();
	}

	private static LocalDate parseFrom(String from) {
		if (from == null || from.isBlank()) {
			return LocalDate.now();
		}
		try {
			return LocalDate.parse(from.trim());
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("From date must be yyyy-MM-dd");
		}
	}

	private static LocalDate parseTo(String to, LocalDate from) {
		if (to == null || to.isBlank()) {
			return from;
		}
		LocalDate end;
		try {
			end = LocalDate.parse(to.trim());
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("To date must be yyyy-MM-dd");
		}
		if (end.isBefore(from)) {
			throw new IllegalArgumentException("To date must be on or after From date");
		}
		return end;
	}

	private static Date startOf(LocalDate day) {
		return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private static String dayKey(Date date) {
		if (date == null) {
			return "";
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
	}

	private static long ageDays(Date date, LocalDate today) {
		if (date == null) {
			return 0;
		}
		return Math.max(0, ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), today));
	}

	private static String instant(Date date) {
		return date == null ? null : date.toInstant().toString();
	}

	private static BigDecimal money(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
	}

	private static String blankTo(String value, String fallback) {
		return value == null || value.isBlank() ? fallback : value.trim();
	}

	private static double parseQty(String qty) {
		if (qty == null || qty.isBlank()) {
			return 0;
		}
		try {
			return Double.parseDouble(qty.trim());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private static final class DayAcc {
		int bills;
		BigDecimal billed = money(0);
		BigDecimal collected = money(0);
		BigDecimal recalled = money(0);
		BigDecimal expenses = money(0);
	}

	private static final class ItemAcc {
		int lines;
		BigDecimal qty = money(0);
		BigDecimal amount = money(0);
	}
}
