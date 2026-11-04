package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDetailDto {

	private OrderStatusDto order;
	private List<Item> items = new ArrayList<Item>();
	private List<Payment> payments = new ArrayList<Payment>();

	public OrderStatusDto getOrder() {
		return order;
	}

	public void setOrder(OrderStatusDto order) {
		this.order = order;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public static class Item {
		private int sID;
		private String itemName;
		private int qty;
		private double unitPrice;
		private double totalAmt;
		private String remarks;

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public int getsID() {
			return sID;
		}

		public void setsID(int sID) {
			this.sID = sID;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public int getQty() {
			return qty;
		}

		public void setQty(int qty) {
			this.qty = qty;
		}

		public double getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(double unitPrice) {
			this.unitPrice = unitPrice;
		}

		public double getTotalAmt() {
			return totalAmt;
		}

		public void setTotalAmt(double totalAmt) {
			this.totalAmt = totalAmt;
		}
	}

	public static class Payment {
		private String receiptNo;
		private double paidAmt;
		private String paidMode;
		private Date payDate;

		public String getReceiptNo() {
			return receiptNo;
		}

		public void setReceiptNo(String receiptNo) {
			this.receiptNo = receiptNo;
		}

		public double getPaidAmt() {
			return paidAmt;
		}

		public void setPaidAmt(double paidAmt) {
			this.paidAmt = paidAmt;
		}

		public String getPaidMode() {
			return paidMode;
		}

		public void setPaidMode(String paidMode) {
			this.paidMode = paidMode;
		}

		public Date getPayDate() {
			return payDate;
		}

		public void setPayDate(Date payDate) {
			this.payDate = payDate;
		}

		private int payID;
		private boolean recalled;
		private boolean recallable;
		private String recallReason;
		private Date recalledDate;
		private String recalledBy;

		public int getPayID() {
			return payID;
		}

		public void setPayID(int payID) {
			this.payID = payID;
		}

		public boolean isRecalled() {
			return recalled;
		}

		public void setRecalled(boolean recalled) {
			this.recalled = recalled;
		}

		public boolean isRecallable() {
			return recallable;
		}

		public void setRecallable(boolean recallable) {
			this.recallable = recallable;
		}

		public String getRecallReason() {
			return recallReason;
		}

		public void setRecallReason(String recallReason) {
			this.recallReason = recallReason;
		}

		public Date getRecalledDate() {
			return recalledDate;
		}

		public void setRecalledDate(Date recalledDate) {
			this.recalledDate = recalledDate;
		}

		public String getRecalledBy() {
			return recalledBy;
		}

		public void setRecalledBy(String recalledBy) {
			this.recalledBy = recalledBy;
		}
	}
}
