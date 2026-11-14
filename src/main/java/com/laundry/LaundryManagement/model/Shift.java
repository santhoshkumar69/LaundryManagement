package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.Filter;

import com.laundry.LaundryManagement.auth.ScopeFilters;
import com.laundry.LaundryManagement.auth.ScopeStampingListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** A cashier's shift at one outlet: the cash handed over at the start and counted at the end. */
@Entity
@Table(name = "TB_LA_SHIFT")
@Data
@Filter(name = ScopeFilters.TENANT)
@Filter(name = ScopeFilters.OUTLET)
@EntityListeners(ScopeStampingListener.class)
public class Shift implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String OPEN = "OPEN";
	public static final String CLOSED = "CLOSED";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/** Outlet code + "-SH" + running number, e.g. T1004-1-SH0001. */
	@Column(name = "SHIFT_NO", length = 40)
	private String shiftNo;

	@Column(name = "TENANT_ID")
	private Integer tenantId;

	@Column(name = "LOCATION_ID")
	private String locationID;

	@Column(name = "USER_DB_ID", nullable = false)
	private Integer userDbId;

	@Column(name = "LOGIN_ID", length = 50)
	private String loginId;

	/** Display name; payments and recalls record the user by this name. */
	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "STATUS", nullable = false, length = 10)
	private String status;

	@Column(name = "START_TIME", nullable = false)
	private Date startTime;

	@Column(name = "END_TIME")
	private Date endTime;

	@Column(name = "OPENING_CASH", nullable = false, precision = 12, scale = 2)
	private BigDecimal openingCash;

	@Column(name = "OPENING_NOTE")
	private String openingNote;

	// The recorded-cash breakdown and totals below are filled when the shift ends; open shifts are computed live.

	/** Cash taken at billing (advance / full payment), including cash later recalled. */
	@Column(name = "BILL_CASH", precision = 12, scale = 2)
	private BigDecimal billCash;

	/** Cash taken on the Delivery screen, including the cash part of split payments. */
	@Column(name = "DELIVERY_CASH", precision = 12, scale = 2)
	private BigDecimal deliveryCash;

	/** Cash payments recalled (handed back) during the shift. */
	@Column(name = "RECALLED_CASH", precision = 12, scale = 2)
	private BigDecimal recalledCash;

	/** Expenses paid in cash that the cashier recorded during the shift. */
	@Column(name = "EXPENSE_CASH", precision = 12, scale = 2)
	private BigDecimal expenseCash;

	/** Net recorded cash: bills + delivery - recalls - expenses. */
	@Column(name = "CASH_COLLECTED", precision = 12, scale = 2)
	private BigDecimal cashCollected;

	@Column(name = "EXPECTED_CASH", precision = 12, scale = 2)
	private BigDecimal expectedCash;

	@Column(name = "CLOSING_CASH", precision = 12, scale = 2)
	private BigDecimal closingCash;

	/** Notes and coins counted at close, as JSON [{"value":500.00,"count":2}, ...]; closingCash is their total. */
	@Column(name = "DENOMINATIONS", columnDefinition = "TEXT")
	private String denominations;

	/** Closing cash minus expected cash. */
	@Column(name = "DIFFERENCE_AMT", precision = 12, scale = 2)
	private BigDecimal difference;

	@Column(name = "CLOSING_NOTE")
	private String closingNote;

	@Column(name = "ENDED_BY")
	private String endedBy;
}
