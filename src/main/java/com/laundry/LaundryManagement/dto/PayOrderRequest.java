package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.List;

public class PayOrderRequest {

	/** One payment method and the amount taken with it; used to split a payment across Cash and Card. */
	public static class Part {
		private String payMode;
		private double amount;

		public Part() {
		}

		public Part(String payMode, double amount) {
			this.payMode = payMode;
			this.amount = amount;
		}

		public String getPayMode() {
			return payMode;
		}

		public void setPayMode(String payMode) {
			this.payMode = payMode;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}
	}

	private int invID;
	private double amount;
	private String payMode;
	private boolean markDelivered;
	/** When present, replaces amount/payMode; each part becomes its own payment record. */
	private List<Part> parts = new ArrayList<Part>();

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

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}
}
