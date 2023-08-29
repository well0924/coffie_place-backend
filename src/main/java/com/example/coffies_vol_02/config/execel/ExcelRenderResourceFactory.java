package com.example.coffies_vol_02.config.execel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public class ExcelRenderResourceFactory {
	
	public static ExcelRenderResource preparRenderResource(Class<?>type) {
		
		String fileName = getFileName(type);
		
		Map<String,String> headerNamesMap = new LinkedHashMap<>();
		
		List<String>fieldNames = new ArrayList<>();
		
		for(Field field : SuperClassReflectionUtil.getAllFields(type)) {
			if(field.isAnnotationPresent(ExcelColumn.class)) {
				
				ExcelColumn annotation  = field.getAnnotation(ExcelColumn.class);
				
				fieldNames.add(field.getName());
				
				String headerName = annotation.headerName();
				
				headerName = StringUtils.hasText(headerName) ? headerName : field.getName();
				
				headerNamesMap.put(field.getName(), headerName);
			}
		}
		return new ExcelRenderResource(fileName, headerNamesMap, fieldNames);
	}
	
	private static String getFileName(Class<?>type) {
		
		String fileName = type.getSimpleName();
		
		if(type.isAnnotationPresent(ExcelFileName.class)) {
			
			fileName = type.getAnnotation(ExcelFileName.class).fileName();
			
			if(!StringUtils.hasText(fileName))fileName = type.getSimpleName();
		}
		return fileName;
	}
}
