package com.laundry.LaundryManagement.dto;

import java.util.Date;

import com.laundry.LaundryManagement.model.InvoiceTemplate;

public class InvoiceTemplateSummary {

	private Integer templateId;
	private String templateName;
	private String fontFamily;
	private Double fontSize;
	private boolean applied;
	private boolean hasLogo;
	private Date updatedDate;

	public static InvoiceTemplateSummary of(InvoiceTemplate template) {
		InvoiceTemplateSummary summary = new InvoiceTemplateSummary();
		summary.templateId = template.getTemplateId();
		summary.templateName = template.getTemplateName();
		summary.fontFamily = template.getFontFamily();
		summary.fontSize = template.getFontSize();
		summary.applied = Integer.valueOf(1).equals(template.getIsActive());
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
}
