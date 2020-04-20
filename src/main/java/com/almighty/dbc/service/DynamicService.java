package com.almighty.dbc.service;

import java.io.InputStream;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author trungnt
 *
 */
@Service
public interface DynamicService {
	public JSONObject updateTableColumnValue(String driverClassName, String url, String username, String password,
			String dbName, String primaryColumnName, String replaceColumnName, InputStream inputStream, int excelSheetIndex,
			int excelColumnIndex, int excelMappingColumnIndex) throws Exception;
}
