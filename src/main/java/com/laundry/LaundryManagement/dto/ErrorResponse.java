package com.laundry.LaundryManagement.dto;

/**
 * @author pandyarajan
 *
 *         26-Dec-2018
 */
public class ErrorResponse {

	public String message;
	public Integer statusCode;
	
	public ErrorResponse(String message,Integer statusCode){
		this.message = message;
		this.statusCode = statusCode;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}
	
	
}
