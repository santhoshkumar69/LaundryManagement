package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;

/** Checks values against the mandatory fields of the outlet the user is working in. */
@Component
public class OutletMandatoryFields {

	private final OutletDetailsRepo outletRepo;

	public OutletMandatoryFields(OutletDetailsRepo outletRepo) {
		this.outletRepo = outletRepo;
	}

	/** Labels of required fields left blank; empty when everything required is filled in. */
	public List<String> missing(Map<String, String> values) {
		List<String> required = outletRepo.findById(RequestScope.requireOutletId())
				.map(outlet -> MandatoryFields.parse(outlet.getMandatoryFields())).orElse(List.of());
		return MandatoryFields.missing(required, values);
	}

	/** Map.of() rejects nulls; blank and null both mean "not filled in". */
	public static String value(String value) {
		return value == null ? "" : value;
	}
}
