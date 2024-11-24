package com.laundry.LaundryManagement.beans;
// All Rest API can use this class by extending, it contains common attributes

public class BaseResponse {

	protected String status;
	protected String message;
	protected String errors;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the errors
	 */
	public String getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(String errors) {
		this.errors = errors;
	}
	
	
}
