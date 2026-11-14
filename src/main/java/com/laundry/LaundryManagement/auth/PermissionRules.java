package com.laundry.LaundryManagement.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Permissions a non-admin needs to call each create/edit/delete endpoint; holding any one of the
 * listed codes is enough. Read endpoints are mostly not listed because several screens share them (the
 * bill screens read categories and items); the app hides screens a role cannot view. Expense Track's
 * list is the exception, as only that screen reads it.
 */
final class PermissionRules {

	private static final Map<String, List<String>> RULES = new HashMap<>();

	static {
		crud("CATEGORY", "/itemCategory/createItemCategory", "/itemCategory/updateItemsCategory",
				"/itemCategory/deActiveItemCategory", "/itemCategory/activateItemCategory");
		crud("UNIT", "/unit/createUnit", "/unit/updateUnit", "/unit/deActiveUnit", "/unit/activateUnit");
		crud("LAUNDRY_TYPE", "/WashingType/createWashingType", "/WashingType/updateWashingType",
				"/WashingType/deActiveWashingType", "/WashingType/activeWashingType");
		crud("ITEMS", "/master/createitem", "/master/updateitem", "/master/deactivateitem", "/master/activateItem");
		rule("/bulkUpload/importExcel", "ITEMS.CREATE");

		// Saving with an ID edits, without one creates; the controllers check which.
		rule("/manage/saveExtraCharges", "EXTRA_CHARGES.CREATE", "EXTRA_CHARGES.EDIT");
		rule("/manage/activateExtraCharges", "EXTRA_CHARGES.DELETE");
		rule("/manage/deActivateExtraCharges", "EXTRA_CHARGES.DELETE");
		rule("/manage/saveDiscounts", "DISCOUNTS.CREATE", "DISCOUNTS.EDIT");
		rule("/manage/activateDiscounts", "DISCOUNTS.DELETE");
		rule("/manage/deActivateDiscounts", "DISCOUNTS.DELETE");

		rule("/payment/savePaymentMode", "PAYMENT_MODES.CREATE", "PAYMENT_MODES.EDIT");
		rule("/payment/activatePaymentMode", "PAYMENT_MODES.DELETE");
		rule("/payment/deActivatePaymentMode", "PAYMENT_MODES.DELETE");

		rule("/billing/createEstimation", "RECEIVING.CREATE");
		rule("/billing/confirmBilling", "RECEIVING.CREATE");
		rule("/billing/cancelBilling", "RECEIVING.CREATE");
		rule("/billing/removeItem", "RECEIVING.CREATE");
		rule("/billing/updateOrderStatus", "ORDER_STATUS.EDIT", "DELIVERY.EDIT");
		rule("/billing/updateItemRemarks", "ORDER_STATUS.EDIT", "DELIVERY.EDIT");
		rule("/billing/payOrder", "DELIVERY.EDIT", "ORDER_STATUS.EDIT");
		rule("/billing/recallPayment", "DELIVERY.EDIT", "ORDER_STATUS.EDIT");
		rule("/billing/removeOrderItems", "DELIVERY.EDIT", "ORDER_STATUS.EDIT");
		rule("/billing/deleteOrders", "ORDER_STATUS.DELETE", "DELIVERY.DELETE");

		// Receiving can add a new customer while billing.
		rule("/customerInfo/createCustomerInfo", "CUSTOMERS.CREATE", "RECEIVING.CREATE");
		// The app has no customer edit screen, so editing stays with admins.
		rule("/customerInfo/updateCustomerInfo", "CUSTOMERS.EDIT");
		rule("/customerInfo/deActiveCustomerInfo", "CUSTOMERS.DELETE");
		rule("/customerInfo/activeCustomer", "CUSTOMERS.DELETE");

		// Excel export needs View; import needs Create, Edit or Delete, refined by what the file changes.
		excel("category", "CATEGORY");
		excel("unit", "UNIT");
		excel("laundryType", "LAUNDRY_TYPE");
		excel("items", "ITEMS");
		excel("extraCharges", "EXTRA_CHARGES");
		excel("discounts", "DISCOUNTS");

		// Expenses are financial records, so even reading them needs View.
		rule("/expenses/list", "EXPENSES.VIEW");
		rule("/expenses/categories", "EXPENSES.VIEW", "EXPENSES.CREATE", "EXPENSES.EDIT");
		rule("/expenses/options", "EXPENSES.VIEW", "EXPENSES.CREATE", "EXPENSES.EDIT");
		rule("/expenseCategories/list", "EXPENSE_CATEGORIES.VIEW");
		rule("/expenseCategories/saveCategory", "EXPENSE_CATEGORIES.CREATE", "EXPENSE_CATEGORIES.EDIT");
		rule("/expenseCategories/saveSubcategory", "EXPENSE_CATEGORIES.CREATE", "EXPENSE_CATEGORIES.EDIT");
		rule("/expenseCategories/deleteCategory", "EXPENSE_CATEGORIES.DELETE");
		rule("/expenseCategories/restoreCategory", "EXPENSE_CATEGORIES.DELETE");
		rule("/expenseCategories/deleteSubcategory", "EXPENSE_CATEGORIES.DELETE");
		rule("/expenseCategories/restoreSubcategory", "EXPENSE_CATEGORIES.DELETE");
		rule("/expenses/save", "EXPENSES.CREATE", "EXPENSES.EDIT");
		rule("/expenses/delete", "EXPENSES.DELETE");

		// Cashiers start and end their own shifts; the report and force-ending are for supervisors.
		rule("/shifts/report", "SHIFTS.VIEW");
		rule("/shifts/cashiers", "SHIFTS.VIEW");
		rule("/shifts/forceEnd", "SHIFTS.EDIT");

		rule("/invoiceTemplate/save", "INVOICE_TEMPLATE.CREATE", "INVOICE_TEMPLATE.EDIT");
		rule("/invoiceTemplate/apply", "INVOICE_TEMPLATE.EDIT");
		rule("/invoiceTemplate/delete", "INVOICE_TEMPLATE.DELETE");
	}

	private PermissionRules() {
	}

	/** Codes that allow the path, or null when the path needs no specific permission. */
	static List<String> requiredFor(String path) {
		return RULES.get(path.endsWith("/") ? path.substring(0, path.length() - 1) : path);
	}

	private static void crud(String screen, String create, String edit, String delete, String restore) {
		rule(create, screen + ".CREATE");
		rule(edit, screen + ".EDIT");
		rule(delete, screen + ".DELETE");
		rule(restore, screen + ".DELETE");
	}

	private static void excel(String key, String screen) {
		rule("/itemConfigExcel/" + key + "/export", screen + ".VIEW");
		rule("/itemConfigExcel/" + key + "/import", screen + ".CREATE", screen + ".EDIT", screen + ".DELETE");
	}

	private static void rule(String path, String... codes) {
		RULES.put(path, List.of(codes));
	}
}
