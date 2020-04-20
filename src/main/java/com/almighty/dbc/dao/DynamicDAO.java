package com.almighty.dbc.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almighty.dbc.datasource.DatasourceFactoryImpl;

@Transactional
@Repository
public class DynamicDAO {
	private Log LOGGER = LogFactory.getLog(DynamicDAO.class);

	private List<String[]> getListMapping(InputStream inputStream, int sheetIndex, List<Integer> columnIndexs)
			throws IOException {

		List<String[]> results = new ArrayList<String[]>();

		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

		XSSFSheet sheet = workbook.getSheetAt(sheetIndex);

		Iterator<Row> rowIterator = sheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// For each row, iterate through all the columns
			Iterator<Cell> cellIterator = row.cellIterator();
			int count = 0;
			String[] item = new String[columnIndexs.size()];
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (columnIndexs.contains(count)) {
					// System.out.println(columnIndexs.indexOf(count));
					String value = cell.getStringCellValue();
					item[columnIndexs.indexOf(count)] = value;

				}
				results.add(item);
				count++;
			}

		}
		workbook.close();

		return results;
	}

	private LinkedHashMap<String, String> getDataMapping(InputStream inputStream, int excelSheetIndex,
			int excelColumnIndex, int excelMappingColumnIndex) throws IOException {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

		XSSFSheet sheet = workbook.getSheetAt(excelSheetIndex);

		Iterator<Row> rowIterator = sheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Cell cell = row.getCell(excelColumnIndex);

			String cellValue = cell.getStringCellValue();

			if (cellValue == null || cellValue.isEmpty()) {
				continue;
			}

			Cell cellMapping = row.getCell(excelMappingColumnIndex);

			String cellMappingValue = cellMapping.getStringCellValue();

			if (cellMappingValue == null || cellMappingValue.isEmpty()) {
				continue;
			}

			map.put(cellValue, cellMappingValue);

		}

		workbook.close();

		inputStream.close();

		return map;
	}

	private void getPrimaryColumnName(String dbName, String tableName, JdbcTemplate jdbcTemplate) {
		String sql = "SHOW KEYS FROM " + dbName + "." + tableName + " WHERE Key_name = 'PRIMARY'";
		String primaryColumnName = "";
		List<Map<String, Object>> keysRecord = jdbcTemplate.queryForList(sql);

		if (keysRecord != null && !keysRecord.isEmpty()) {
			primaryColumnName = (String) keysRecord.get(0).get("Column_name");
		}
		setPrimaryColumnName(primaryColumnName);

	}

	@SuppressWarnings("unchecked")
	public JSONObject updateTableByColumnNameValue(String driverClassName, String url, String username, String password,
			String dbName, String primaryColumnName, String replaceColumnName, InputStream inputStream,
			int excelSheetIndex, int excelColumnIndex, int excelMappingColumnIndex) throws Exception {

		LinkedHashMap<String, String> mappingData = getDataMapping(inputStream, excelSheetIndex, excelColumnIndex,
				excelMappingColumnIndex);

		DatasourceFactoryImpl factoryImpl = new DatasourceFactoryImpl(driverClassName, url, username, password);

		JSONObject data = new JSONObject();

		DatabaseMetaData metaData = factoryImpl.createDataSource().getConnection().getMetaData();

		ResultSet tables = metaData.getTables(dbName, dbName, null, new String[] { "TABLE" });

		JdbcTemplate jdbcTemplate = factoryImpl.createJdbcTemplate();

		String path = System.getProperty("user.dir") + "/_tmp/uploads";
		File _tmp = new File(path);
		if (!_tmp.exists()) {
			_tmp.mkdir();
		}

		System.out.println(System.getProperty("user.dir"));
		System.out.println(new File("").getAbsolutePath());
		System.out.println(FileSystems.getDefault().getPath("").toAbsolutePath().toString());
		System.out.println(Paths.get("").toAbsolutePath().toString());

		while (tables.next()) {
			String tableName = tables.getString("TABLE_NAME");

			LOGGER.info("tableName ->>> " + tableName);

			JSONArray matcheds = new JSONArray();

			ResultSet columns = metaData.getColumns(dbName, dbName, tableName, "%");

			while (columns.next()) {

				String columnName = columns.getString("COLUMN_NAME");

				LOGGER.info("columnName ->>> " + columnName);

				if (replaceColumnName.equals(columnName)) {

					matcheds.add(columnName);

					break;
				}
			}

			if (matcheds.size() > 0) {

				data.put(tableName, matcheds);

				getPrimaryColumnName(dbName, tableName, jdbcTemplate);

				for (Map.Entry<String, String> map : mappingData.entrySet()) {
					String key = map.getKey();
					String value = map.getValue();
					String countSql = "SELECT count(*) AS TOTAL FROM " + dbName + "." + tableName + " WHERE "
							+ replaceColumnName + "='" + key + "';";
					LOGGER.info(countSql);
					List<Map<String, Object>> countRecords = jdbcTemplate.queryForList(countSql);

					long total = 0;

					if (countRecords != null && !countRecords.isEmpty()) {
						total = (Long) countRecords.get(0).get("TOTAL");
					}

					if (total == 0) {
						continue;
					}

					String searchSql = "SELECT " + getPrimaryColumnName() + " AS ID_ FROM " + dbName + "." + tableName
							+ " WHERE " + replaceColumnName + "='" + key + "';";
					LOGGER.info(searchSql);

					List<Map<String, Object>> searchRecords = jdbcTemplate.queryForList(searchSql);

					if (searchRecords != null && !searchRecords.isEmpty()) {
						searchRecords.forEach((row) -> {
							row.forEach((k, v) -> {
								long id = (Long) v;
								String updateSql = "UPDATE " + dbName + "." + tableName + " SET " + replaceColumnName
										+ " = '" + value + "'" + " WHERE " + getPrimaryColumnName() + "=" + id;
								LOGGER.info(updateSql);
								jdbcTemplate.execute(updateSql);
							});
						});

					}

				}

			}
		}
		return data;
	}

	public String primaryColumnName;

	public String getPrimaryColumnName() {
		return primaryColumnName;
	}

	public void setPrimaryColumnName(String primaryColumnName) {
		this.primaryColumnName = primaryColumnName;
	}

}
