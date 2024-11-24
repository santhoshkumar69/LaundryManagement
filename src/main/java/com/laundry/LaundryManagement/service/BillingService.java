/**
 * 
 */
package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.dto.BilledDetails;
import com.laundry.LaundryManagement.dto.BillingInfos;
import com.laundry.LaundryManagement.dto.ConfirmBilling;
import com.laundry.LaundryManagement.dto.ReceiptDetailDto;
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

}
