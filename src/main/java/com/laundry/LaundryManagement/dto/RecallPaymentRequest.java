package com.laundry.LaundryManagement.dto;

public class RecallPaymentRequest {

	private int invID;
	private int payID;
	private String reason;

	public int getInvID() {
		return invID;
	}

	public void setInvID(int invID) {
		this.invID = invID;
	}

	public int getPayID() {
		return payID;
	}

	public void setPayID(int payID) {
		this.payID = payID;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
