package com.laundry.LaundryManagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.service.CreditService;

/** Customer > Credit List: unpaid balances of the current outlet by customer. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("credit")
public class CreditController {

	private static final MediaType XLSX = MediaType
			.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	@Autowired
	private CreditService creditService;

	@Autowired
	private OutletDetailsRepo outletRepo;

	@GetMapping("/list")
	public ResponseEntity<Object> list(@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "band", required = false) String band,
			@RequestParam(value = "minBalance", required = false) BigDecimal minBalance) {
		return ResponseEntity.ok(creditService.list(search, band, minBalance));
	}

	@GetMapping("/customer")
	public ResponseEntity<Object> customer(@RequestParam("id") int customerId) {
		return ResponseEntity.ok(creditService.customer(customerId));
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> export(@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "band", required = false) String band,
			@RequestParam(value = "minBalance", required = false) BigDecimal minBalance) {
		String outletCode = outletRepo.findById(RequestScope.requireOutletId()).map(o -> o.getOutletCode()).orElse("outlet");
		String fileName = "credit-list-" + outletCode + "-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";
		return ResponseEntity.ok().contentType(XLSX)
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
				.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
				.body(creditService.export(search, band, minBalance));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}
}
