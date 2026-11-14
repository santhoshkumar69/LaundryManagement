package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.auth.RequestScope;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.laundry.LaundryManagement.dto.InvoiceTemplateSummary;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.InvoiceTemplate;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.repository.InvoiceTemplateRepository;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;

@Service
public class InvoiceTemplateServiceImpl implements InvoiceTemplateService {

	private static final int PAPER_WIDTH_MM = 80;
	private static final List<String> FONT_FAMILIES = Arrays.asList("Roboto", "CourierPrime", "PTSerif");
	private static final double MIN_FONT_SIZE = 5;
	private static final double MAX_FONT_SIZE = 24;
	private static final int MAX_LOGO_CHARS = 700_000;

	@Autowired
	private InvoiceTemplateRepository invoiceTemplateRepository;

	@Autowired
	private OutletDetailsRepo outletDetailsRepo;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<InvoiceTemplateSummary> listTemplates() {
		Integer applied = currentOutlet().getInvoiceTemplateId();
		return invoiceTemplateRepository.findLive(RequestScope.requireTenantId()).stream()
				.map(t -> InvoiceTemplateSummary.of(t, t.getTemplateId().equals(applied))).collect(Collectors.toList());
	}

	@Override
	public InvoiceTemplate getTemplate(int templateId) {
		return findLive(templateId);
	}

	@Override
	public InvoiceTemplate getAppliedTemplate() {
		Integer templateId = currentOutlet().getInvoiceTemplateId();
		if (templateId == null) {
			return null;
		}
		try {
			return findCompanyTemplate(templateId);
		} catch (IllegalArgumentException deleted) {
			return null;
		}
	}

	@Override
	public InvoiceTemplate findCompanyTemplate(int templateId) {
		int tenantId = RequestScope.requireTenantId();
		InvoiceTemplate template = RequestScope.asSystem(() -> invoiceTemplateRepository.findById(templateId).orElse(null));
		if (template == null || !Integer.valueOf(1).equals(template.getActiveStatus())
				|| !Integer.valueOf(tenantId).equals(template.getTenantId())) {
			throw new IllegalArgumentException("Invoice template not found");
		}
		return template;
	}

	@Override
	@Transactional
	public InvoiceTemplate saveTemplate(InvoiceTemplate request, GlobalInfo globalInfo) {
		String name = request.getTemplateName() == null ? "" : request.getTemplateName().trim();
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Template name is required");
		}
		if (name.length() > 100) {
			throw new IllegalArgumentException("Template name must be 100 characters or fewer");
		}
		String fontFamily = request.getFontFamily() == null ? "Roboto" : request.getFontFamily();
		if (!FONT_FAMILIES.contains(fontFamily)) {
			throw new IllegalArgumentException("Unsupported font: " + fontFamily);
		}
		double fontSize = request.getFontSize() == null ? 8 : request.getFontSize();
		if (fontSize < MIN_FONT_SIZE || fontSize > MAX_FONT_SIZE) {
			throw new IllegalArgumentException("Font size must be between 5 and 24");
		}
		validateLayout(request.getLayoutJson());
		String logo = request.getLogoData();
		if (logo != null && !logo.isEmpty()) {
			if (!logo.startsWith("data:image/png;base64,") && !logo.startsWith("data:image/jpeg;base64,")) {
				throw new IllegalArgumentException("Logo must be a PNG or JPEG image");
			}
			if (logo.length() > MAX_LOGO_CHARS) {
				throw new IllegalArgumentException("Logo image is too large; use an image under 500 KB");
			}
		}

		InvoiceTemplate template;
		if (request.getTemplateId() != null) {
			template = findLive(request.getTemplateId());
		} else {
			template = new InvoiceTemplate();
			template.setIsActive(0);
			template.setActiveStatus(1);
			template.setTenantId(RequestScope.requireTenantId());
			template.setCreatedBy(userName(globalInfo));
			template.setCreatedDate(new Date());
		}
		boolean nameTaken = invoiceTemplateRepository.findLiveByName(RequestScope.requireTenantId(), name).stream()
				.anyMatch(existing -> !existing.getTemplateId().equals(template.getTemplateId()));
		if (nameTaken) {
			throw new IllegalArgumentException("A template named \"" + name + "\" already exists");
		}

		template.setTemplateName(name);
		template.setPaperWidthMm(PAPER_WIDTH_MM);
		template.setFontFamily(fontFamily);
		template.setFontSize(fontSize);
		template.setLayoutJson(request.getLayoutJson());
		template.setLogoData(logo == null || logo.isEmpty() ? null : logo);
		if (request.getTemplateId() != null) {
			template.setUpdatedBy(userName(globalInfo));
			template.setUpdatedDate(new Date());
		}
		return invoiceTemplateRepository.save(template);
	}

	@Override
	@Transactional
	public List<InvoiceTemplateSummary> applyTemplate(int templateId, GlobalInfo globalInfo) {
		InvoiceTemplate target = templateId > 0 ? findLive(templateId) : null;
		OutletDetails outlet = currentOutlet();
		outlet.setInvoiceTemplateId(target == null ? null : target.getTemplateId());
		outlet.setUpdatedBy(userName(globalInfo));
		outlet.setUpdatedDate(new Date());
		outletDetailsRepo.save(outlet);
		return listTemplates();
	}

	@Override
	@Transactional
	public List<InvoiceTemplateSummary> deleteTemplate(int templateId, GlobalInfo globalInfo) {
		InvoiceTemplate template = findLive(templateId);
		OutletDetails current = currentOutlet();
		List<String> otherUsers = outletDetailsRepo.findByTenantIdOrderByIdAsc(RequestScope.requireTenantId()).stream()
				.filter(o -> template.getTemplateId().equals(o.getInvoiceTemplateId()) && !o.getId().equals(current.getId()))
				.map(OutletDetails::getOutletName).toList();
		if (!otherUsers.isEmpty()) {
			throw new IllegalArgumentException("\"" + template.getTemplateName() + "\" is used by " + String.join(", ", otherUsers)
					+ ". Choose another template for them in Settings > Outlets first.");
		}
		if (template.getTemplateId().equals(current.getInvoiceTemplateId())) {
			current.setInvoiceTemplateId(null);
			outletDetailsRepo.save(current);
		}
		template.setActiveStatus(0);
		template.setIsActive(0);
		template.setUpdatedBy(userName(globalInfo));
		template.setUpdatedDate(new Date());
		invoiceTemplateRepository.save(template);
		return listTemplates();
	}

	private OutletDetails currentOutlet() {
		return outletDetailsRepo.findById(RequestScope.requireOutletId())
				.orElseThrow(() -> new IllegalArgumentException("Choose an outlet first"));
	}

	private InvoiceTemplate findLive(int templateId) {
		InvoiceTemplate template = invoiceTemplateRepository.findById(templateId).orElse(null);
		if (template == null || !Integer.valueOf(1).equals(template.getActiveStatus())
				|| !Integer.valueOf(RequestScope.requireTenantId()).equals(template.getTenantId())) {
			throw new IllegalArgumentException("Template not found");
		}
		return template;
	}

	private void validateLayout(String layoutJson) {
		if (layoutJson == null || layoutJson.trim().isEmpty()) {
			throw new IllegalArgumentException("Template layout is required");
		}
		JsonNode root;
		try {
			root = objectMapper.readTree(layoutJson);
		} catch (Exception e) {
			throw new IllegalArgumentException("Template layout is not valid JSON");
		}
		JsonNode rows = root.get("rows");
		if (rows == null || !rows.isArray() || rows.size() == 0) {
			throw new IllegalArgumentException("Template needs at least one row");
		}
		for (JsonNode row : rows) {
			JsonNode cells = row.get("cells");
			if (cells == null || !cells.isArray() || cells.size() == 0) {
				throw new IllegalArgumentException("Every row needs at least one column");
			}
		}
	}

	private String userName(GlobalInfo globalInfo) {
		return globalInfo != null ? globalInfo.getUserName() : null;
	}
}
