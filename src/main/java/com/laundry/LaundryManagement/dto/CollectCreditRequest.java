package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.List;

/** Take a payment against a customer's open bills at the current outlet. */
public class CollectCreditRequest {

	/** One payment method and the amount taken with it. */
	public static class Part {
		private String payMode;
		private double amount;
		/** Required when the outlet's payment mode asks for a card type. */
		private String cardType;

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

		public String getCardType() {
			return cardType;
		}

		public void setCardType(String cardType) {
			this.cardType = cardType;
		}
	}

	private int customerId;
	/** When empty, all of the customer's open bills at this outlet; otherwise only these. */
	private List<Integer> invIds = new ArrayList<>();
	private List<Part> parts = new ArrayList<>();

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public List<Integer> getInvIds() {
		return invIds;
	}

	public void setInvIds(List<Integer> invIds) {
		this.invIds = invIds;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}
}
