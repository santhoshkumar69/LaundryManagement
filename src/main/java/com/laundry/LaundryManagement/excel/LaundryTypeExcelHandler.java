package com.laundry.LaundryManagement.excel;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.model.WashingType;

@Component
public class LaundryTypeExcelHandler extends NamedExcelHandler<WashingType> {

	@Override
	public String key() {
		return "laundryType";
	}

	@Override
	public String permission() {
		return "LAUNDRY_TYPE";
	}

	@Override
	public String label() {
		return "Laundry Types";
	}

	@Override
	protected String nameColumn() {
		return "Laundry Type";
	}

	@Override
	protected String noun() {
		return "laundry types";
	}

	@Override
	protected List<WashingType> all() {
		return em.createQuery("from WashingType w order by w.activeStatus desc, w.processTypeName", WashingType.class)
				.getResultList();
	}

	@Override
	protected String name(WashingType record) {
		return record.getProcessTypeName();
	}

	@Override
	protected int activeStatus(WashingType record) {
		return record.getActiveStatus();
	}

	@Override
	protected WashingType create(String name) {
		WashingType type = new WashingType();
		type.setCreatedBy(userName());
		type.setCreatedDate(new Date());
		return type;
	}

	@Override
	protected void apply(WashingType record, String name, Integer activeStatus, boolean isNew) {
		record.setProcessTypeName(name);
		if (activeStatus != null) {
			record.setActiveStatus(activeStatus);
		}
		if (!isNew) {
			record.setUpdatedBy(userName());
			record.setUpdatedDate(new Date());
		}
	}
}
