package com.laundry.LaundryManagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/** Last number handed out per outlet for a document series (e.g. BILL receipts, PAY receipts). */
@Entity
@Data
@Table(name = "TB_LA_OUTLET_SEQUENCE", uniqueConstraints = @UniqueConstraint(columnNames = { "OUTLET_ID", "SERIES" }))
public class OutletSequence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "OUTLET_ID", nullable = false)
	private Integer outletId;

	@Column(name = "SERIES", nullable = false)
	private String series;

	@Column(name = "LAST_NUMBER", nullable = false)
	private long lastValue;
}
