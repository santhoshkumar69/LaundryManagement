package com.laundry.LaundryManagement.dto;

public class PayOrderRequest {

	private int invID;
	private double amount;
	private String payMode;
	private boolean markDelivered;

	public int getInvID() {
		return invID;
	}

	public void setInvID(int invID) {
		this.invID = invID;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public boolean isMarkDelivered() {
		return markDelivered;
	}

	public void setMarkDelivered(boolean markDelivered) {
		this.markDelivered = markDelivered;
	}
}
