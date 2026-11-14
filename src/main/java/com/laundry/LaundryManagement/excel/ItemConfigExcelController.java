package com.laundry.LaundryManagement.excel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;

/** Excel export and import for the Item Configuration screens of the current outlet. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("itemConfigExcel")
public class ItemConfigExcelController {

	private static final MediaType XLSX = MediaType
			.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private final Map<String, ExcelScreenHandler> handlers;

	@Autowired
	private OutletDetailsRepo outletRepo;

	public ItemConfigExcelController(List<ExcelScreenHandler> handlers) {
		this.handlers = handlers.stream().collect(Collectors.toMap(ExcelScreenHandler::key, Function.identity()));
	}

	@GetMapping("/{screen}/export")
	public ResponseEntity<byte[]> export(@PathVariable("screen") String screen) {
		ExcelScreenHandler handler = handler(screen);
		String outletCode = outletRepo.findById(RequestScope.requireOutletId()).map(o -> o.getOutletCode()).orElse("outlet");
		String fileName = handler.label().toLowerCase().replace(' ', '-') + "-" + outletCode + "-"
				+ LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";
		return ResponseEntity.ok().contentType(XLSX)
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
				.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
				.body(handler.export());
	}

	@PostMapping("/{screen}/import")
	public ResponseEntity<Object> importFile(@PathVariable("screen") String screen,
			@RequestParam("file") MultipartFile file) throws IOException {
		ExcelScreenHandler handler = handler(screen);
		if (file.isEmpty()) {
			throw new ImportValidationException("Choose an Excel file to import");
		}
		return ResponseEntity.ok(handler.importFile(file.getInputStream()));
	}

	@ExceptionHandler(ImportValidationException.class)
	public ResponseEntity<Object> invalid(ImportValidationException ex) {
		return ResponseEntity.badRequest()
				.body(Map.of("message", ex.getMessage(), "statusCode", 400, "errors", ex.getErrors()));
	}

	@ExceptionHandler(UnknownScreenException.class)
	public ResponseEntity<Object> unknown(UnknownScreenException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage(), "statusCode", 404));
	}

	private ExcelScreenHandler handler(String screen) {
		ExcelScreenHandler handler = handlers.get(screen);
		if (handler == null) {
			throw new UnknownScreenException(screen);
		}
		return handler;
	}

	private static class UnknownScreenException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		UnknownScreenException(String screen) {
			super("Import and export are not available for " + screen);
		}
	}
}
