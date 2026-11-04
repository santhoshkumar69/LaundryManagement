package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.List;

public class UpdateItemRemarksRequest {

	public static class Line {
		private int sID;
		private String remarks;

		public int getsID() {
			return sID;
		}

		public void setsID(int sID) {
			this.sID = sID;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}
	}

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
}
