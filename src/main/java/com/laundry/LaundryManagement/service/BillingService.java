/**
 * 
 */
package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.dto.BilledDetails;
import com.laundry.LaundryManagement.dto.BillingInfos;
import com.laundry.LaundryManagement.dto.ConfirmBilling;
import com.laundry.LaundryManagement.dto.DashboardDto;
import com.laundry.LaundryManagement.dto.OrderDetailDto;
import com.laundry.LaundryManagement.dto.OrderStatusDto;
import com.laundry.LaundryManagement.dto.PayOrderRequest;
import com.laundry.LaundryManagement.dto.RemoveOrderItemsRequest;
import com.laundry.LaundryManagement.dto.ReceiptDetailDto;
import com.laundry.LaundryManagement.dto.RecallPaymentRequest;
import com.laundry.LaundryManagement.dto.UpdateItemRemarksRequest;
import com.laundry.LaundryManagement.dto.UpdateOrderStatus;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.GlobalInfo;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
public interface BillingService {

	BilledDetails createEstimationDetails(BillingInfos billingReq, GlobalInfo globalInfo);

	ReceiptDetailDto confirmBillingRequest(ConfirmBilling confirmBill, GlobalInfo attribute);

	boolean cancelBillingRequest(ConfirmBilling confirmBill, GlobalInfo attribute);

	CustomerInfo fetchCustomerDetails(Integer cusID, String mobileNo);

	void removeBillItems(int itemID);

	List<OrderStatusDto> fetchOrders(String fromDate, String toDate, String search, String status);

	/**
	 * dateField "DELIVERY" filters on the delivery (due) date; anything else filters on the billing date.
	 * includeOverdue adds undelivered bills due before fromDate (delivery-date mode only).
	 */
	List<OrderStatusDto> fetchOrders(String fromDate, String toDate, String search, String status, String dateField,
			boolean includeOverdue);

	List<OrderStatusDto> updateOrderStatus(UpdateOrderStatus request, GlobalInfo globalInfo);

	DashboardDto fetchDashboard();

	OrderStatusDto updateItemRemarks(UpdateItemRemarksRequest request, GlobalInfo globalInfo);

	OrderDetailDto recallPayment(RecallPaymentRequest request, GlobalInfo globalInfo);

	int deleteOrders(List<Integer> invIDs, GlobalInfo globalInfo);

	OrderDetailDto fetchOrderDetail(int invID);

	OrderDetailDto payOrder(PayOrderRequest request, GlobalInfo globalInfo);

	OrderDetailDto removeOrderItems(RemoveOrderItemsRequest request, GlobalInfo globalInfo);

}
