package com.laundry.LaundryManagement.excel;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.model.Unit;

@Component
public class UnitExcelHandler extends NamedExcelHandler<Unit> {

	@Override
	public String key() {
		return "unit";
	}

	@Override
	public String permission() {
		return "UNIT";
	}

	@Override
	public String label() {
		return "Units";
	}

	@Override
	protected String nameColumn() {
		return "Unit";
	}

	@Override
	protected String noun() {
		return "units";
	}

	@Override
	protected List<Unit> all() {
		return em.createQuery("from Unit u order by u.activeStatus desc, u.unit", Unit.class).getResultList();
	}

	@Override
	protected String name(Unit record) {
		return record.getUnit();
	}

	@Override
	protected int activeStatus(Unit record) {
		return record.getActiveStatus();
	}

	@Override
	protected Unit create(String name) {
		Unit unit = new Unit();
		unit.setCreatedBy(userName());
		unit.setCreatedDate(new Date());
		return unit;
	}

	@Override
	protected void apply(Unit record, String name, Integer activeStatus, boolean isNew) {
		record.setUnit(name);
		if (activeStatus != null) {
			record.setActiveStatus(activeStatus);
		}
		if (!isNew) {
			record.setUpdatedBy(userName());
			record.setUpdatedDate(new Date());
		}
	}
}
