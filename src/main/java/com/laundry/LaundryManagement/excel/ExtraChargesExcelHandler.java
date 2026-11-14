package com.laundry.LaundryManagement.excel;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.model.ExtraCharges;

@Component
public class ExtraChargesExcelHandler extends AdjustmentExcelHandler<ExtraCharges> {

	@Override
	public String key() {
		return "extraCharges";
	}

	@Override
	public String permission() {
		return "EXTRA_CHARGES";
	}

	@Override
	public String label() {
		return "Extra Charges";
	}

	@Override
	protected String nameColumn() {
		return "Charge Name";
	}

	@Override
	protected String noun() {
		return "extra charges";
	}

	@Override
	protected boolean hasRemarkRequired() {
		return false;
	}

	@Override
	protected List<ExtraCharges> all() {
		return em.createQuery("from ExtraCharges c order by c.activeStatus desc, c.chargeName", ExtraCharges.class)
				.getResultList();
	}

	@Override
	protected Values values(ExtraCharges record) {
		return new Values(record.getChargeName(), record.getChargeType(), record.getChargeValue(), record.isOpen(), false);
	}

	@Override
	protected int activeStatus(ExtraCharges record) {
		return record.getActiveStatus();
	}

	@Override
	protected ExtraCharges create() {
		ExtraCharges charge = new ExtraCharges();
		charge.setCreatedBy(userName());
		charge.setCreatedDate(new Date());
		return charge;
	}

	@Override
	protected void apply(ExtraCharges record, Values values, Integer activeStatus, boolean isNew) {
		record.setChargeName(values.name());
		record.setChargeType(values.type());
		record.setChargeValue(values.value());
		record.setOpen(values.open());
		if (activeStatus != null) {
			record.setActiveStatus(activeStatus);
		}
		if (!isNew) {
			record.setUpdatedBy(userName());
			record.setUpdatedDate(new Date());
		}
	}
}
