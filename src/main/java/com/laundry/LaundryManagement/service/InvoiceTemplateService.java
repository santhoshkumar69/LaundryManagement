package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.dto.InvoiceTemplateSummary;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.InvoiceTemplate;

public interface InvoiceTemplateService {

	List<InvoiceTemplateSummary> listTemplates();

	InvoiceTemplate getTemplate(int templateId);

	InvoiceTemplate getAppliedTemplate();

	InvoiceTemplate saveTemplate(InvoiceTemplate template, GlobalInfo globalInfo);

	List<InvoiceTemplateSummary> applyTemplate(int templateId, GlobalInfo globalInfo);

	List<InvoiceTemplateSummary> deleteTemplate(int templateId, GlobalInfo globalInfo);

	/** A live template of the caller's company, whichever outlet owns it. */
	InvoiceTemplate findCompanyTemplate(int templateId);
}
