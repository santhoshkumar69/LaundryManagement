package com.laundry.LaundryManagement.dto;

public class UpdateOrderStatus {

	private int invID;
	private String orderStatus;

	public int getInvID() {
		return invID;
	}

	public void setInvID(int invID) {
		this.invID = invID;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
}
