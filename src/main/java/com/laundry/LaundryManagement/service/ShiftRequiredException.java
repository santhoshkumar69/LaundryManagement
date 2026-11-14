package com.laundry.LaundryManagement.service;

/** A cashier tried to bill or take payments without an open shift in this outlet. */
public class ShiftRequiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ShiftRequiredException(String message) {
		super(message);
	}
}
