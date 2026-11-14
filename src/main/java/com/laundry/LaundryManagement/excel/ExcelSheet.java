package com.laundry.LaundryManagement.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Reads the first sheet of an uploaded workbook and writes single-sheet .xlsx exports. */
public final class ExcelSheet {

	public static final int MAX_ROWS = 5000;

	/** A data row: its 1-based row number as Excel shows it, and its cells by lower-cased header. */
	public record ExcelRow(int number, Map<String, String> values) {
		public String get(String header) {
			String value = values.get(header.toLowerCase());
			return value == null ? "" : value.trim();
		}
	}

	public record Parsed(List<String> headers, List<ExcelRow> rows) {
		public boolean hasColumn(String header) {
			return headers.contains(header.toLowerCase());
		}
	}

	private ExcelSheet() {
	}

	public static Parsed read(InputStream in) {
		try (Workbook workbook = WorkbookFactory.create(in)) {
			if (workbook.getNumberOfSheets() == 0) {
				throw new ImportValidationException("The file has no sheets");
			}
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Row headerRow = sheet.getRow(sheet.getFirstRowNum());
			if (headerRow == null) {
				throw new ImportValidationException("The first row must hold the column headings");
			}
			List<String> headers = new ArrayList<>();
			for (int c = 0; c < headerRow.getLastCellNum(); c++) {
				headers.add(text(headerRow.getCell(c), formatter, evaluator).toLowerCase());
			}
			List<ExcelRow> rows = new ArrayList<>();
			for (int r = headerRow.getRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				Map<String, String> values = new LinkedHashMap<>();
				boolean blank = true;
				for (int c = 0; c < headers.size(); c++) {
					String value = text(row.getCell(c), formatter, evaluator);
					blank &= value.isEmpty();
					if (!headers.get(c).isEmpty()) {
						values.put(headers.get(c), value);
					}
				}
				if (!blank) {
					rows.add(new ExcelRow(r + 1, values));
				}
			}
			if (rows.size() > MAX_ROWS) {
				throw new ImportValidationException("A file can hold at most " + MAX_ROWS + " rows");
			}
			return new Parsed(headers, rows);
		} catch (IOException | RuntimeException ex) {
			if (ex instanceof ImportValidationException validation) {
				throw validation;
			}
			throw new ImportValidationException("The file could not be read. Upload an Excel file (.xlsx or .xls).");
		}
	}

	public static byte[] write(String sheetName, List<String> headers, List<List<Object>> rows) {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet(sheetName);
			CellStyle headerStyle = workbook.createCellStyle();
			Font bold = workbook.createFont();
			bold.setBold(true);
			headerStyle.setFont(bold);
			Row header = sheet.createRow(0);
			for (int c = 0; c < headers.size(); c++) {
				Cell cell = header.createCell(c);
				cell.setCellValue(headers.get(c));
				cell.setCellStyle(headerStyle);
			}
			for (int r = 0; r < rows.size(); r++) {
				Row row = sheet.createRow(r + 1);
				List<Object> values = rows.get(r);
				for (int c = 0; c < values.size(); c++) {
					Object value = values.get(c);
					Cell cell = row.createCell(c);
					if (value instanceof Number number) {
						cell.setCellValue(number.doubleValue());
					} else if (value != null) {
						cell.setCellValue(value.toString());
					}
				}
			}
			sheet.createFreezePane(0, 1);
			for (int c = 0; c < headers.size(); c++) {
				sheet.autoSizeColumn(c);
			}
			workbook.write(out);
			return out.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException("Could not create the Excel file", ex);
		}
	}

	private static String text(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
		return cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
	}
}
