package com.laundry.LaundryManagement.dto;

import java.util.List;

public class UpdateOrderStatus {

	private int invID;
	private List<Integer> invIDs;
	private String orderStatus;

	public List<Integer> getInvIDs() {
		return invIDs;
	}

	public void setInvIDs(List<Integer> invIDs) {
		this.invIDs = invIDs;
	}

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
