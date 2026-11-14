package com.laundry.LaundryManagement.excel;

import java.util.List;

/** An import that was rejected as a whole; nothing was saved. */
public class ImportValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public record RowError(int row, String message) {
	}

	private final transient List<RowError> errors;

	public ImportValidationException(String message) {
		this(List.of(new RowError(0, message)));
	}

	public ImportValidationException(List<RowError> errors) {
		super(errors.size() == 1 ? errors.get(0).message()
				: errors.size() + " problems found in the file; nothing was imported");
		this.errors = errors;
	}

	public List<RowError> getErrors() {
		return errors;
	}
}
