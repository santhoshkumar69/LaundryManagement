package com.laundry.LaundryManagement.dto;

import java.util.Date;

import com.laundry.LaundryManagement.model.InvoiceTemplate;
import com.laundry.LaundryManagement.model.OutletDetails;

public class InvoiceTemplateSummary {

	private Integer templateId;
	private String templateName;
	private String fontFamily;
	private Double fontSize;
	private boolean applied;
	private boolean hasLogo;
	private Date updatedDate;
	private Integer outletId;
	private String outletName;

	/** applied: whether the current outlet prints bills with this template; owner: the outlet that created it. */
	public static InvoiceTemplateSummary of(InvoiceTemplate template, boolean applied, OutletDetails owner) {
		InvoiceTemplateSummary summary = new InvoiceTemplateSummary();
		summary.outletId = owner.getId();
		summary.outletName = owner.getOutletName() + (owner.getActiveStatus() == 1 ? "" : " (deleted)");
		summary.templateId = template.getTemplateId();
		summary.templateName = template.getTemplateName();
		summary.fontFamily = template.getFontFamily();
		summary.fontSize = template.getFontSize();
		summary.applied = applied;
		summary.hasLogo = template.getLogoData() != null && !template.getLogoData().isEmpty();
		summary.updatedDate = template.getUpdatedDate() != null ? template.getUpdatedDate()
				: template.getCreatedDate();
		return summary;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public Double getFontSize() {
		return fontSize;
	}

	public boolean isApplied() {
		return applied;
	}

	public boolean isHasLogo() {
		return hasLogo;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public String getOutletName() {
		return outletName;
	}
}
