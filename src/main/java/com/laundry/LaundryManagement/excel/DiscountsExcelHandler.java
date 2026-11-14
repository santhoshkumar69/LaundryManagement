package com.laundry.LaundryManagement.excel;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.model.Discounts;

@Component
public class DiscountsExcelHandler extends AdjustmentExcelHandler<Discounts> {

	@Override
	public String key() {
		return "discounts";
	}

	@Override
	public String permission() {
		return "DISCOUNTS";
	}

	@Override
	public String label() {
		return "Discounts";
	}

	@Override
	protected String nameColumn() {
		return "Discount Name";
	}

	@Override
	protected String noun() {
		return "discounts";
	}

	@Override
	protected boolean hasRemarkRequired() {
		return true;
	}

	@Override
	protected List<Discounts> all() {
		return em.createQuery("from Discounts d order by d.activeStatus desc, d.discountName", Discounts.class)
				.getResultList();
	}

	@Override
	protected Values values(Discounts record) {
		return new Values(record.getDiscountName(), record.getDiscountType(), record.getValue(), record.isOpen(),
				record.isRemarkRequired());
	}

	@Override
	protected int activeStatus(Discounts record) {
		return record.getActiveStatus();
	}

	@Override
	protected Discounts create() {
		Discounts discount = new Discounts();
		discount.setCreatedBy(userName());
		discount.setCreatedDate(new Date());
		return discount;
	}

	@Override
	protected void apply(Discounts record, Values values, Integer activeStatus, boolean isNew) {
		record.setDiscountName(values.name());
		record.setDiscountType(values.type());
		record.setValue(values.value());
		record.setOpen(values.open());
		record.setRemarkRequired(values.remarkRequired());
		if (activeStatus != null) {
			record.setActiveStatus(activeStatus);
		}
		if (!isNew) {
			record.setUpdatedBy(userName());
			record.setUpdatedDate(new Date());
		}
	}
}
