package com.laundry.LaundryManagement.model;

import java.io.Serializable;
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

/** An outlet's expense category (Accounts & Reports > Expense Categories), e.g. Rent, Supplies. */
@Entity
@Table(name = "TB_LA_EXPENSE_CATEGORY")
@Data
@Filter(name = ScopeFilters.TENANT)
@Filter(name = ScopeFilters.OUTLET)
@EntityListeners(ScopeStampingListener.class)
public class ExpenseCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TENANT_ID")
	private Integer tenantId;

	@Column(name = "LOCATION_ID")
	private String locationID;

	@Column(name = "NAME", nullable = false, length = 100)
	private String name;

	/** 1 = offered on new expenses, 0 = deleted (old expenses keep showing it). */
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
