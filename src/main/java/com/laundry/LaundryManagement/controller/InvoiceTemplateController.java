package com.laundry.LaundryManagement.controller;

import com.laundry.LaundryManagement.auth.RequestScope;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.InvoiceTemplate;
import com.laundry.LaundryManagement.service.InvoiceTemplateService;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("invoiceTemplate")
public class InvoiceTemplateController {

	@Autowired
	private InvoiceTemplateService invoiceTemplateService;

	@GetMapping("/list")
	public ResponseEntity<Object> listTemplates() {
		return ResponseEntity.ok().body(invoiceTemplateService.listTemplates());
	}

	@GetMapping("/get")
	public ResponseEntity<Object> getTemplate(@RequestParam("templateId") int templateId) {
		try {
			return ResponseEntity.ok().body(invoiceTemplateService.getTemplate(templateId));
		} catch (IllegalArgumentException e) {
			return error(e.getMessage());
		}
	}

	@GetMapping("/applied")
	public ResponseEntity<Object> getAppliedTemplate() {
		InvoiceTemplate applied = invoiceTemplateService.getAppliedTemplate();
		return applied == null ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(applied);
	}

	@PostMapping("/save")
	public ResponseEntity<Object> saveTemplate(@RequestBody InvoiceTemplate template, HttpSession httpSession) {
		try {
			return ResponseEntity.ok().body(invoiceTemplateService.saveTemplate(template, globalInfo(httpSession)));
		} catch (IllegalArgumentException e) {
			return error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return error(AppStatus.UNABLE_TO_SAVE);
		}
	}

	@PostMapping("/apply")
	public ResponseEntity<Object> applyTemplate(@RequestParam("templateId") int templateId, HttpSession httpSession) {
		try {
			return ResponseEntity.ok()
					.body(invoiceTemplateService.applyTemplate(templateId, globalInfo(httpSession)));
		} catch (IllegalArgumentException e) {
			return error(e.getMessage());
		}
	}

	@PostMapping("/delete")
	public ResponseEntity<Object> deleteTemplate(@RequestParam("templateId") int templateId, HttpSession httpSession) {
		try {
			return ResponseEntity.ok()
					.body(invoiceTemplateService.deleteTemplate(templateId, globalInfo(httpSession)));
		} catch (IllegalArgumentException e) {
			return error(e.getMessage());
		}
	}

	private GlobalInfo globalInfo(HttpSession httpSession) {
		return RequestScope.globalInfo();
	}

	private ResponseEntity<Object> error(String message) {
		return ResponseEntity.status(500).body(new ReturnResponse(message, AppStatusCode.INTERNAL_ERROR));
	}
}
