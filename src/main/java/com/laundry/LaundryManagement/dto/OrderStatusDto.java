package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderStatusDto {

	public static class ItemLine {
		private int sID;
		private String itemName;
		private int qty;
		private String remarks;

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

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}
	}

	private List<ItemLine> items = new ArrayList<ItemLine>();

	public List<ItemLine> getItems() {
		return items;
	}

	public void setItems(List<ItemLine> items) {
		this.items = items;
	}

	private int invID;
	private String invoiceNo;
	private String receiptNo;
	private Date billingDate;
	private Date deliveryDate;
	private Date collectedDate;
	private Long customerId;
	private String customerName;
	private String phoneNo;
	private int noOfItems;
	private String itemDetails;
	private double invoicedAmt;
	private double paidAmt;
	private double outstandingAmt;
	private String paymentStatus;
	private String paymentMode;
	private String orderStatus;
	private String pickupType;
	private String deliveryAddress;
	private String paymentMethods;
	private double extraChargesAmt;
	private double discountAmt;

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public double getExtraChargesAmt() {
		return extraChargesAmt;
	}

	public void setExtraChargesAmt(double extraChargesAmt) {
		this.extraChargesAmt = extraChargesAmt;
	}

	public double getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(double discountAmt) {
		this.discountAmt = discountAmt;
	}

	public int getInvID() {
		return invID;
	}

	public void setInvID(int invID) {
		this.invID = invID;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Date getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getCollectedDate() {
		return collectedDate;
	}

	public void setCollectedDate(Date collectedDate) {
		this.collectedDate = collectedDate;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public int getNoOfItems() {
		return noOfItems;
	}

	public void setNoOfItems(int noOfItems) {
		this.noOfItems = noOfItems;
	}

	public String getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(String itemDetails) {
		this.itemDetails = itemDetails;
	}

	public double getInvoicedAmt() {
		return invoicedAmt;
	}

	public void setInvoicedAmt(double invoicedAmt) {
		this.invoicedAmt = invoicedAmt;
	}

	public double getPaidAmt() {
		return paidAmt;
	}

	public void setPaidAmt(double paidAmt) {
		this.paidAmt = paidAmt;
	}

	public double getOutstandingAmt() {
		return outstandingAmt;
	}

	public void setOutstandingAmt(double outstandingAmt) {
		this.outstandingAmt = outstandingAmt;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPickupType() {
		return pickupType;
	}

	public void setPickupType(String pickupType) {
		this.pickupType = pickupType;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
}
