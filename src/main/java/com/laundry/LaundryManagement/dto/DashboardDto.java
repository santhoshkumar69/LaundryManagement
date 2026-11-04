package com.laundry.LaundryManagement.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDto {

	private long dueToday;
	private long overdue;
	private Map<String, Long> openByStatus = new LinkedHashMap<String, Long>();
	private Date oldestOpenBillDate;
	private long billsToday;
	private double billedToday;
	private Map<String, Double> receivedToday = new LinkedHashMap<String, Double>();
	private double creditOutstanding;
	private List<OrderStatusDto> nextDeliveries = new ArrayList<OrderStatusDto>();

	public long getDueToday() {
		return dueToday;
	}

	public void setDueToday(long dueToday) {
		this.dueToday = dueToday;
	}

	public long getOverdue() {
		return overdue;
	}

	public void setOverdue(long overdue) {
		this.overdue = overdue;
	}

	public Map<String, Long> getOpenByStatus() {
		return openByStatus;
	}

	public void setOpenByStatus(Map<String, Long> openByStatus) {
		this.openByStatus = openByStatus;
	}

	public Date getOldestOpenBillDate() {
		return oldestOpenBillDate;
	}

	public void setOldestOpenBillDate(Date oldestOpenBillDate) {
		this.oldestOpenBillDate = oldestOpenBillDate;
	}

	public long getBillsToday() {
		return billsToday;
	}

	public void setBillsToday(long billsToday) {
		this.billsToday = billsToday;
	}

	public double getBilledToday() {
		return billedToday;
	}

	public void setBilledToday(double billedToday) {
		this.billedToday = billedToday;
	}

	public Map<String, Double> getReceivedToday() {
		return receivedToday;
	}

	public void setReceivedToday(Map<String, Double> receivedToday) {
		this.receivedToday = receivedToday;
	}

	public double getCreditOutstanding() {
		return creditOutstanding;
	}

	public void setCreditOutstanding(double creditOutstanding) {
		this.creditOutstanding = creditOutstanding;
	}

	public List<OrderStatusDto> getNextDeliveries() {
		return nextDeliveries;
	}

	public void setNextDeliveries(List<OrderStatusDto> nextDeliveries) {
		this.nextDeliveries = nextDeliveries;
	}
}
