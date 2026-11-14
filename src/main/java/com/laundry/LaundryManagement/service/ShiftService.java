package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

	/**
	 * Times are ISO-8601 instants (e.g. 2026-11-14T10:15:30Z) so the browser timer can count from them.
	 * cashCollected is the net recorded cash; expectedCash = openingCash + cashCollected.
	 */
	public record ShiftView(Long id, String shiftNo, Integer outletId, String outletName, Integer userDbId,
			String loginId, String userName, String status, String startTime, String endTime, long durationMinutes,
			BigDecimal openingCash, String openingNote, BigDecimal billCash, BigDecimal deliveryCash,
			BigDecimal recalledCash, BigDecimal expenseCash, BigDecimal cashCollected, BigDecimal expectedCash,
			BigDecimal closingCash, BigDecimal difference, String closingNote, String endedBy) {
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

	public record EndShiftRequest(Long shiftId, BigDecimal closingCash, String note) {
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

	public CurrentShift current() {
		RequestScope.Scope scope = RequestScope.require();
		Shift open = openShift(scope);
		return new CurrentShift(rolePermissions.requiresShift(scope.tenantId(), scope.role()),
				open == null ? null : view(open, outletNames()), open != null && isHere(open));
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
		return view(shiftRepo.save(shift), outletNames());
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
		Map<String, String> names = outletNames();
		return shiftRepo.findForReport(startOf(start), startOf(end.plusDays(1)), userDbId).stream()
				.map(s -> view(s, names)).toList();
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
		BigDecimal closing = amount(request == null ? null : request.closingCash(), "Closing cash");
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
		shift.setDifference(closing.subtract(expected));
		shift.setClosingNote(note(request == null ? null : request.note()));
		shift.setEndedBy(endedBy);
		return view(shiftRepo.save(shift), outletNames());
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

	private ShiftView view(Shift s, Map<String, String> outletNames) {
		boolean open = Shift.OPEN.equals(s.getStatus());
		Date end = open ? new Date() : s.getEndTime();
		CashBreakdown cash = open ? recordedCash(s)
				: new CashBreakdown(zeroIfNull(s.getBillCash()), zeroIfNull(s.getDeliveryCash()),
						zeroIfNull(s.getRecalledCash()), zeroIfNull(s.getExpenseCash()));
		BigDecimal collected = open ? cash.net() : s.getCashCollected();
		BigDecimal expected = open ? s.getOpeningCash().add(collected) : s.getExpectedCash();
		long minutes = Math.max(0, (end.getTime() - s.getStartTime().getTime()) / 60000);
		return new ShiftView(s.getId(), s.getShiftNo(), Integer.valueOf(s.getLocationID()),
				outletNames.getOrDefault(s.getLocationID(), ""), s.getUserDbId(), s.getLoginId(), s.getUserName(),
				s.getStatus(), s.getStartTime().toInstant().toString(),
				open ? null : s.getEndTime().toInstant().toString(), minutes, s.getOpeningCash(), s.getOpeningNote(),
				cash.bills(), cash.delivery(), cash.recalled(), cash.expenses(), collected, expected,
				s.getClosingCash(), s.getDifference(), s.getClosingNote(), s.getEndedBy());
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

	private Map<String, String> outletNames() {
		Map<String, String> names = new HashMap<>();
		for (OutletDetails outlet : outletRepo.findByTenantIdOrderByIdAsc(RequestScope.requireTenantId())) {
			names.put(String.valueOf(outlet.getId()), outlet.getOutletName());
		}
		return names;
	}

	private String outletName(Shift shift) {
		return outletNames().getOrDefault(shift.getLocationID(), "another outlet");
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
