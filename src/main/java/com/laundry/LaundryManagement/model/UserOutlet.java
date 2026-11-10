package com.laundry.LaundryManagement.model;

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
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/** Outlets a non-admin user may open (admins may open every outlet of their tenant). */
@Entity
@Data
@Table(name = "TB_LA_USER_OUTLET", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_DB_ID", "OUTLET_ID" }))
@Filter(name = ScopeFilters.TENANT)
@EntityListeners(ScopeStampingListener.class)
public class UserOutlet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/** TB_LA_USER_INFO.ID */
	@Column(name = "USER_DB_ID", nullable = false)
	private Integer userDbId;

	@Column(name = "OUTLET_ID", nullable = false)
	private Integer outletId;

	@Column(name = "TENANT_ID", nullable = false)
	private Integer tenantId;
}
