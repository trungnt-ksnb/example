package com.almighty.dbc.service.impl;

import java.io.InputStream;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almighty.dbc.dao.DynamicDAO;
import com.almighty.dbc.service.DynamicService;

/**
 * @author trungnt
 *
 */
@Service
public class DynamicServiceImpl implements DynamicService {
	@Autowired
	private DynamicDAO dynamicDAO;

	@Override
	public JSONObject updateTableColumnValue(String driverClassName, String url, String username, String password,
			String dbName, String primaryColumnName, String replaceColumnName, InputStream inputStream,
			int excelSheetIndex, int excelColumnIndex, int excelMappingColumnIndex) throws Exception {
		return dynamicDAO.updateTableByColumnNameValue(driverClassName, url, username, password, dbName,
				primaryColumnName, replaceColumnName, inputStream, excelSheetIndex, excelColumnIndex,
				excelMappingColumnIndex);
	}
}
