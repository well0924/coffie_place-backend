package com.example.coffies_vol_02.config.execel;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;

public class ExcelService<T> {
	
	private final Workbook workbook;
	
	private final Sheet sheet;
	
	private final ExcelRenderResource resource;
	
	private final List<T> dataList;
	
	private int rowIndex =0;
	
	public ExcelService(List<T>dataList,Class<T>type){
		
		this.workbook = new HSSFWorkbook();
		this.sheet =workbook.createSheet();
		this.resource = ExcelRenderResourceFactory.preparRenderResource(type);
		this.dataList = dataList;
	}
	
	public void downloadExcel(HttpServletResponse response) throws Exception {
	
		createHead();
		createBody();
		writeExcel(response);
	
	}
	
	private void createHead() {
	
		Row row = sheet.createRow(rowIndex++);
	
		int columnIndex = 0;
		
		for(String dataFieldName : resource.getDataFieldNames()) {
			
			Cell cell = row.createCell(columnIndex++);
			
			String value = resource.getExcelHeaderName(dataFieldName);
			
			cell.setCellValue(value);
		}
	}
	
	private void createBody()throws Exception{
	
		for(T data: dataList) {
			
			Row row = sheet.createRow(rowIndex++);
			
			int columnIndex = 0;
			
			for(String dataFieldName : resource.getDataFieldNames()) {
				
				Cell cell = row.createCell(columnIndex++);
				
				Field field = SuperClassReflectionUtil.getField(data.getClass(), (dataFieldName));
				
				field.setAccessible(true);
				
				Object cellValue = field.get(data);
				
				field.setAccessible(false);
				
				setCellValue(cell,cellValue);
			}
		}
	}
	
	private void writeExcel(HttpServletResponse response)throws Exception{

		String fileName = new String(resource.getExcelFileName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
		
		response.setContentType("ms-vnd/excel");
		
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,String.format("attachment; filename=\"%s.xls\"",fileName));
		
		workbook.write(response.getOutputStream());
		
		workbook.close();
	}
	
	private void setCellValue(Cell cell, Object cellValue) {
	
		if(cellValue instanceof Number) {
		
			Number numberValue = (Number)cellValue;
			
			cell.setCellValue(numberValue.doubleValue());
			
			return;
		}
		
		cell.setCellValue(ObjectUtils.isEmpty(cellValue)? "": String.valueOf(cellValue));
	
	}
}
