package com.laundry.LaundryManagement.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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
import com.laundry.LaundryManagement.service.ReportsService;

/** Accounts &amp; Reports: daily sales, general, credit and void/recall reports. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("reports")
public class ReportsController {

	private static final MediaType XLSX = MediaType
			.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	@Autowired
	private ReportsService reportsService;

	@Autowired
	private OutletDetailsRepo outletRepo;

	@GetMapping("/outlets")
	public ResponseEntity<Object> outlets() {
		return ResponseEntity.ok(reportsService.reportOutlets());
	}

	@GetMapping("/daily")
	public ResponseEntity<Object> daily(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.daily(from, to, parseIds(outletIds)));
	}

	@GetMapping("/daily/export")
	public ResponseEntity<byte[]> dailyExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("daily-sales", reportsService.exportDaily(from, to, parseIds(outletIds)));
	}

	@GetMapping("/sales")
	public ResponseEntity<Object> sales(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.sales(from, to, parseIds(outletIds)));
	}

	@GetMapping("/sales/export")
	public ResponseEntity<byte[]> salesExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("sales-summary", reportsService.exportSales(from, to, parseIds(outletIds)));
	}

	@GetMapping("/items")
	public ResponseEntity<Object> items(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.items(from, to, parseIds(outletIds)));
	}

	@GetMapping("/items/export")
	public ResponseEntity<byte[]> itemsExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("item-mix", reportsService.exportItems(from, to, parseIds(outletIds)));
	}

	@GetMapping("/operations")
	public ResponseEntity<Object> operations(@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.operations(parseIds(outletIds)));
	}

	@GetMapping("/operations/export")
	public ResponseEntity<byte[]> operationsExport(@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("operations", reportsService.exportOperations(parseIds(outletIds)));
	}

	@GetMapping("/pl")
	public ResponseEntity<Object> pl(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.profitLoss(from, to, parseIds(outletIds)));
	}

	@GetMapping("/pl/export")
	public ResponseEntity<byte[]> plExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("profit-loss", reportsService.exportPl(from, to, parseIds(outletIds)));
	}

	@GetMapping("/expenses/export")
	public ResponseEntity<byte[]> expensesExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to) {
		return xlsx("expenses", reportsService.exportExpenses(from, to));
	}

	@GetMapping("/credit/aging")
	public ResponseEntity<Object> creditAging(@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.creditAging(parseIds(outletIds)));
	}

	@GetMapping("/credit/aging/export")
	public ResponseEntity<byte[]> creditAgingExport(@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("credit-aging", reportsService.exportCreditAging(parseIds(outletIds)));
	}

	@GetMapping("/credit/collections")
	public ResponseEntity<Object> creditCollections(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.creditCollections(from, to, parseIds(outletIds)));
	}

	@GetMapping("/credit/collections/export")
	public ResponseEntity<byte[]> creditCollectionsExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("credit-collections", reportsService.exportCreditCollections(from, to, parseIds(outletIds)));
	}

	@GetMapping("/credit/statement/export")
	public ResponseEntity<byte[]> statementExport(@RequestParam("customerId") int customerId) {
		return xlsx("credit-statement-" + customerId, reportsService.exportStatement(customerId));
	}

	@GetMapping("/recalls")
	public ResponseEntity<Object> recalls(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return ResponseEntity.ok(reportsService.recalls(from, to, parseIds(outletIds)));
	}

	@GetMapping("/recalls/export")
	public ResponseEntity<byte[]> recallsExport(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "outletIds", required = false) String outletIds) {
		return xlsx("recalls", reportsService.exportRecalls(from, to, parseIds(outletIds)));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}

	private ResponseEntity<byte[]> xlsx(String baseName, byte[] body) {
		String outletCode = outletRepo.findById(RequestScope.requireOutletId()).map(o -> o.getOutletCode()).orElse("outlet");
		String fileName = baseName + "-" + outletCode + "-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
				+ ".xlsx";
		return ResponseEntity.ok().contentType(XLSX)
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
				.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(body);
	}

	private static List<Integer> parseIds(String raw) {
		if (raw == null || raw.isBlank()) {
			return List.of();
		}
		try {
			return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::valueOf).toList();
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("outletIds must be a comma-separated list of numbers");
		}
	}
}
