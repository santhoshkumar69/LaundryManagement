package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoveOrderItemsRequest {

	private int invID;
	private List<Line> items = new ArrayList<Line>();

	public int getInvID() {
		return invID;
	}

	public void setInvID(int invID) {
		this.invID = invID;
	}

	public List<Line> getItems() {
		return items;
	}

	public void setItems(List<Line> items) {
		this.items = items;
	}

	public static class Line {
		@JsonProperty("sID")
		private int sID;
		private int removeQty;

		public int getsID() {
			return sID;
		}

		public void setsID(int sID) {
			this.sID = sID;
		}

		public int getRemoveQty() {
			return removeQty;
		}

		public void setRemoveQty(int removeQty) {
			this.removeQty = removeQty;
		}
	}
}
