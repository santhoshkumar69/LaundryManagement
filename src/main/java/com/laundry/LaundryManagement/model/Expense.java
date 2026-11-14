package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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

/** An outlet's spending entry (Accounts > Expense Track), e.g. detergent, electricity, rent. */
@Entity
@Table(name = "TB_LA_EXPENSE")
@Data
@Filter(name = ScopeFilters.TENANT)
@Filter(name = ScopeFilters.OUTLET)
@EntityListeners(ScopeStampingListener.class)
public class Expense implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TENANT_ID")
	private Integer tenantId;

	@Column(name = "LOCATION_ID")
	private String locationID;

	@Column(name = "EXPENSE_DATE", nullable = false)
	private LocalDate expenseDate;

	@Column(name = "EXPENSE_NAME", nullable = false, length = 100)
	private String expenseName;

	@Column(name = "CATEGORY", nullable = false, length = 30)
	private String category;

	@Column(name = "AMOUNT", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	/** 1 = in use, 0 = deleted (kept for the record). */
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private int activeStatus = 1;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
}
