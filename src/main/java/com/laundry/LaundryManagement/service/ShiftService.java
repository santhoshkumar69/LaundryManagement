package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.auth.RolePermissions;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.Shift;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.ShiftRepo;
import com.laundry.LaundryManagement.service.ShiftDenominations.Denomination;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tools.jackson.databind.ObjectMapper;

/**
 * Cashier shifts. A user whose role requires shifts has at most one open shift, tied to the outlet it was
 * started in; billing and taking payments there need it. Recorded cash is every cash movement of the shift:
 * cash taken at billing and on the Delivery screen (split payments count their cash part), minus cash payments
 * recalled during the shift, minus cash expenses the cashier recorded during it. Each shift gets a per-outlet
 * number such as T1004-1-SH0001.
 */
@Service
public class ShiftService {

	private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");
	private static final int MAX_NOTE_LENGTH = 255;
	private static final int MAX_COUNT = 1_000_000;

	/**
	 * Times are ISO-8601 instants (e.g. 2026-11-14T10:15:30Z) so the browser timer can count from them.
	 * cashCollected is the net recorded cash; expectedCash = openingCash + cashCollected.
	 */
	public record ShiftView(Long id, String shiftNo, Integer outletId, String outletName, Integer userDbId,
			String loginId, String userName, String status, String startTime, String endTime, long durationMinutes,
			BigDecimal openingCash, String openingNote, BigDecimal billCash, BigDecimal deliveryCash,
			BigDecimal recalledCash, BigDecimal expenseCash, BigDecimal cashCollected, BigDecimal expectedCash,
			BigDecimal closingCash, BigDecimal difference, String closingNote, String endedBy, String currency,
			List<Denomination> denominationOptions, List<DenominationCount> denominations) {
	}

	/** How many of one note or coin were counted, e.g. value 100.00, count 3. */
	public record DenominationCount(BigDecimal value, Integer count) {
	}

	private record CashBreakdown(BigDecimal bills, BigDecimal delivery, BigDecimal recalled, BigDecimal expenses) {
		BigDecimal net() {
			return bills.add(delivery).subtract(recalled).subtract(expenses);
		}
	}

	/** shift is the user's open shift wherever it is; inThisOutlet says whether it is in the current outlet. */
	public record CurrentShift(boolean requiresShift, ShiftView shift, boolean inThisOutlet) {
	}

	public record StartShiftRequest(BigDecimal openingCash, String note) {
	}

	/** The counted cash is the total of the denominations; closingCash is not accepted on its own. */
	public record EndShiftRequest(Long shiftId, String note, List<DenominationCount> denominations) {
	}

	public record Cashier(Integer userDbId, String loginId, String name) {
	}

	@Autowired
	private ShiftRepo shiftRepo;

	@Autowired
	private OutletDetailsRepo outletRepo;

	@Autowired
	private RolePermissions rolePermissions;

	@Autowired
	private OutletSequenceService outletSequenceService;

	@PersistenceContext
	private EntityManager em;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public CurrentShift current() {
		RequestScope.Scope scope = RequestScope.require();
		Shift open = openShift(scope);
		return new CurrentShift(rolePermissions.requiresShift(scope.tenantId(), scope.role()),
				open == null ? null : view(open, outlets()), open != null && isHere(open));
	}

	@Transactional
	public ShiftView start(StartShiftRequest request) {
		RequestScope.Scope scope = RequestScope.require();
		if (!rolePermissions.requiresShift(scope.tenantId(), scope.role())) {
			throw new IllegalArgumentException("Your role does not work in shifts");
		}
		Shift open = openShift(scope);
		if (open != null) {
			throw new IllegalArgumentException("You already have an open shift at " + outletName(open)
					+ ". End it before starting a new one.");
		}
		BigDecimal openingCash = amount(request == null ? null : request.openingCash(), "Opening cash");
		String openingNote = note(request == null ? null : request.note());
		OutletDetails outlet = outletRepo.findById(RequestScope.requireOutletId()).orElseThrow();
		Shift shift = new Shift();
		shift.setShiftNo(String.format("%s-SH%04d", outlet.getOutletCode(),
				outletSequenceService.nextNumber(OutletSequenceService.SHIFT)));
		shift.setUserDbId(scope.userDbId());
		shift.setLoginId(scope.loginId());
		shift.setUserName(scope.userName());
		shift.setStatus(Shift.OPEN);
		shift.setStartTime(new Date());
		shift.setOpeningCash(openingCash);
		shift.setOpeningNote(openingNote);
		return view(shiftRepo.save(shift), outlets());
	}

	/** Ends the caller's own open shift, which must be in the current outlet. */
	@Transactional
	public ShiftView end(EndShiftRequest request) {
		RequestScope.Scope scope = RequestScope.require();
		Shift open = openShift(scope);
		if (open == null) {
			throw new IllegalArgumentException("You have no open shift");
		}
		if (!isHere(open)) {
			throw new IllegalArgumentException("Your shift is open at " + outletName(open)
					+ ". Switch to that outlet to end it.");
		}
		return close(shiftRepo.findById(open.getId()).orElseThrow(), request, scope.userName());
	}

	/** An admin ends a shift someone left open in the current outlet. */
	@Transactional
	public ShiftView forceEnd(EndShiftRequest request) {
		RequestScope.Scope scope = RequestScope.require();
		Shift shift = request == null || request.shiftId() == null ? null
				: shiftRepo.findById(request.shiftId()).orElse(null);
		if (shift == null) {
			throw new IllegalArgumentException("Shift not found");
		}
		if (!Shift.OPEN.equals(shift.getStatus())) {
			throw new IllegalArgumentException("This shift has already ended");
		}
		return close(shift, request, scope.userName());
	}

	/** Current outlet's shifts started in the period (this month by default), plus any still open. */
	public List<ShiftView> report(String from, String to, Integer userDbId) {
		LocalDate start = blank(from) ? LocalDate.now().withDayOfMonth(1) : date(from, "From date");
		LocalDate end = blank(to) ? start.withDayOfMonth(start.lengthOfMonth()) : date(to, "To date");
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("To date must be on or after From date");
		}
		Map<String, OutletDetails> outlets = outlets();
		return shiftRepo.findForReport(startOf(start), startOf(end.plusDays(1)), userDbId).stream()
				.map(s -> view(s, outlets)).toList();
	}

	public List<Cashier> cashiers() {
		return em.createQuery("from UserInfo u order by u.userName", UserInfo.class).getResultList().stream()
				.filter(u -> rolePermissions.requiresShift(u.getTenantId(), u.getRole()))
				.map(u -> new Cashier(u.getId(), u.getUserId(), u.getUserName())).toList();
	}

	/** Refuses billing and payments for a cashier without an open shift in the current outlet. */
	public void requireShiftForCashWork() {
		RequestScope.Scope scope = RequestScope.require();
		if (!rolePermissions.requiresShift(scope.tenantId(), scope.role())) {
			return;
		}
		Shift open = openShift(scope);
		if (open == null) {
			throw new ShiftRequiredException("Start your shift before billing: open Bill > Receiving and enter the opening cash.");
		}
		if (!isHere(open)) {
			String outlet = outletName(open);
			throw new ShiftRequiredException("Your shift is open at " + outlet + ". Switch back to " + outlet
					+ " to bill, or end that shift first.");
		}
	}

	/** The caller's open shift in the current outlet, for tagging the payments they take. */
	public Long openShiftIdHere() {
		RequestScope.Scope scope = RequestScope.require();
		Shift open = openShift(scope);
		return open != null && isHere(open) ? open.getId() : null;
	}

	/**
	 * The shift a recall is counted against: the recaller's open shift here, or else (e.g. an admin recalling) the
	 * shift the payment was taken in while that shift is still open, since the cash leaves its drawer.
	 */
	public Long shiftIdForRecall(Long paymentShiftId) {
		Long mine = openShiftIdHere();
		if (mine != null) {
			return mine;
		}
		return paymentShiftId != null && Shift.OPEN.equals(shiftRepo.statusOf(paymentShiftId)) ? paymentShiftId : null;
	}

	// ---- Helpers ----

	private ShiftView close(Shift shift, EndShiftRequest request, String endedBy) {
		Map<String, OutletDetails> outlets = outlets();
		List<DenominationCount> counted = countedDenominations(request == null ? null : request.denominations(),
				ShiftDenominations.forCurrency(currencyOf(shift, outlets)));
		BigDecimal closing = counted.stream().map(c -> c.value().multiply(BigDecimal.valueOf(c.count())))
				.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
		if (closing.compareTo(MAX_AMOUNT) > 0) {
			throw new IllegalArgumentException("Counted cash is too large");
		}
		CashBreakdown cash = recordedCash(shift);
		BigDecimal collected = cash.net();
		BigDecimal expected = shift.getOpeningCash().add(collected);
		shift.setEndTime(new Date());
		shift.setStatus(Shift.CLOSED);
		shift.setBillCash(cash.bills());
		shift.setDeliveryCash(cash.delivery());
		shift.setRecalledCash(cash.recalled());
		shift.setExpenseCash(cash.expenses());
		shift.setCashCollected(collected);
		shift.setExpectedCash(expected);
		shift.setClosingCash(closing);
		shift.setDenominations(objectMapper.writeValueAsString(counted));
		shift.setDifference(closing.subtract(expected));
		shift.setClosingNote(note(request == null ? null : request.note()));
		shift.setEndedBy(endedBy);
		return view(shiftRepo.save(shift), outlets);
	}

	/**
	 * Checks the counts against the outlet's notes and coins and keeps the non-zero ones, largest first. The list
	 * is required (all zeros is a valid count of an empty drawer).
	 */
	private static List<DenominationCount> countedDenominations(List<DenominationCount> submitted,
			List<Denomination> allowed) {
		if (submitted == null || submitted.isEmpty()) {
			throw new IllegalArgumentException("Enter how many of each note and coin you counted");
		}
		Map<BigDecimal, Integer> counts = new HashMap<>();
		for (DenominationCount entry : submitted) {
			if (entry == null || entry.value() == null) {
				throw new IllegalArgumentException("Each denomination needs a value");
			}
			BigDecimal value = entry.value().setScale(2, RoundingMode.HALF_UP);
			if (allowed.stream().noneMatch(d -> d.value().compareTo(value) == 0)) {
				throw new IllegalArgumentException(value.toPlainString() + " is not a note or coin of this outlet's currency");
			}
			int count = entry.count() == null ? 0 : entry.count();
			if (count < 0 || count > MAX_COUNT) {
				throw new IllegalArgumentException("Count for " + value.toPlainString() + " must be between 0 and " + MAX_COUNT);
			}
			if (counts.put(value, count) != null) {
				throw new IllegalArgumentException(value.toPlainString() + " is listed more than once");
			}
		}
		List<DenominationCount> counted = new ArrayList<>();
		for (Denomination d : allowed) {
			Integer count = counts.get(d.value());
			if (count != null && count > 0) {
				counted.add(new DenominationCount(d.value(), count));
			}
		}
		return counted;
	}

	private List<DenominationCount> savedDenominations(Shift shift) {
		if (blank(shift.getDenominations())) {
			return List.of();
		}
		try {
			return List.of(objectMapper.readValue(shift.getDenominations(), DenominationCount[].class));
		} catch (RuntimeException ex) {
			return List.of();
		}
	}

	private static String currencyOf(Shift shift, Map<String, OutletDetails> outlets) {
		OutletDetails outlet = outlets.get(shift.getLocationID());
		return outlet == null ? null : outlet.getCurrency();
	}

	/** Payments, recalls and expenses tagged with the shift; read as the shift's outlet since they are outlet-scoped. */
	private CashBreakdown recordedCash(Shift shift) {
		return inOutletOf(shift, () -> new CashBreakdown(money(shiftRepo.billCash(shift.getId())),
				money(shiftRepo.deliveryCash(shift.getId())), money(shiftRepo.recalledCash(shift.getId())),
				shiftRepo.expenseCash(shift.getId()).setScale(2, RoundingMode.HALF_UP)));
	}

	private static BigDecimal money(Double value) {
		return BigDecimal.valueOf(value == null ? 0 : value).setScale(2, RoundingMode.HALF_UP);
	}

	private ShiftView view(Shift s, Map<String, OutletDetails> outlets) {
		boolean open = Shift.OPEN.equals(s.getStatus());
		Date end = open ? new Date() : s.getEndTime();
		CashBreakdown cash = open ? recordedCash(s)
				: new CashBreakdown(zeroIfNull(s.getBillCash()), zeroIfNull(s.getDeliveryCash()),
						zeroIfNull(s.getRecalledCash()), zeroIfNull(s.getExpenseCash()));
		BigDecimal collected = open ? cash.net() : s.getCashCollected();
		BigDecimal expected = open ? s.getOpeningCash().add(collected) : s.getExpectedCash();
		long minutes = Math.max(0, (end.getTime() - s.getStartTime().getTime()) / 60000);
		OutletDetails outlet = outlets.get(s.getLocationID());
		String currency = outlet == null ? null : outlet.getCurrency();
		return new ShiftView(s.getId(), s.getShiftNo(), Integer.valueOf(s.getLocationID()),
				outlet == null ? "" : outlet.getOutletName(), s.getUserDbId(), s.getLoginId(), s.getUserName(),
				s.getStatus(), s.getStartTime().toInstant().toString(),
				open ? null : s.getEndTime().toInstant().toString(), minutes, s.getOpeningCash(), s.getOpeningNote(),
				cash.bills(), cash.delivery(), cash.recalled(), cash.expenses(), collected, expected,
				s.getClosingCash(), s.getDifference(), s.getClosingNote(), s.getEndedBy(), currency,
				ShiftDenominations.forCurrency(currency), open ? List.of() : savedDenominations(s));
	}

	private static BigDecimal zeroIfNull(BigDecimal value) {
		return value == null ? BigDecimal.ZERO.setScale(2) : value;
	}

	private Shift openShift(RequestScope.Scope scope) {
		List<Shift> open = RequestScope.asSystem(() -> shiftRepo.findOpen(scope.tenantId(), scope.userDbId()));
		return open.isEmpty() ? null : open.get(0);
	}

	private static boolean isHere(Shift shift) {
		return shift.getLocationID().equals(String.valueOf(RequestScope.requireOutletId()));
	}

	/** Payments are outlet-scoped, so read them as the shift's outlet. */
	private static <T> T inOutletOf(Shift shift, Supplier<T> work) {
		return isHere(shift) ? work.get() : RequestScope.asOutlet(Integer.parseInt(shift.getLocationID()), work);
	}

	private Map<String, OutletDetails> outlets() {
		Map<String, OutletDetails> outlets = new HashMap<>();
		for (OutletDetails outlet : outletRepo.findByTenantIdOrderByIdAsc(RequestScope.requireTenantId())) {
			outlets.put(String.valueOf(outlet.getId()), outlet);
		}
		return outlets;
	}

	private String outletName(Shift shift) {
		OutletDetails outlet = outlets().get(shift.getLocationID());
		return outlet == null ? "another outlet" : outlet.getOutletName();
	}

	private static BigDecimal amount(BigDecimal value, String label) {
		if (value == null) {
			throw new IllegalArgumentException(label + " is required");
		}
		if (value.signum() < 0) {
			throw new IllegalArgumentException(label + " cannot be negative");
		}
		if (value.stripTrailingZeros().scale() > 2) {
			throw new IllegalArgumentException(label + " can have at most 2 decimal places");
		}
		if (value.compareTo(MAX_AMOUNT) > 0) {
			throw new IllegalArgumentException(label + " is too large");
		}
		return value.setScale(2);
	}

	private static String note(String note) {
		if (blank(note)) {
			return null;
		}
		String trimmed = note.trim();
		if (trimmed.length() > MAX_NOTE_LENGTH) {
			throw new IllegalArgumentException("Note can be at most " + MAX_NOTE_LENGTH + " characters");
		}
		return trimmed;
	}

	private static Date startOf(LocalDate day) {
		return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private static LocalDate date(String value, String label) {
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException(label + " must be a date in the form yyyy-MM-dd");
		}
	}

	private static boolean blank(String value) {
		return value == null || value.isBlank();
	}
}
