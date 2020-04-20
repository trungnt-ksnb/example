package com.almighty.dbc.scriptqueue.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.almighty.dbc.service.SourceService;
import com.almighty.dbc.service.TargetService;
import com.almighty.dbc.util.StringUtil;

/**
 * @author trungnt
 *
 */

public class ScriptEngine {

	private Log LOGGER = LogFactory.getLog(ScriptEngine.class);

	private SourceService sourceService;

	private TargetService targetService;

	private String datasource;

	private List<String> errorMessages = new ArrayList<String>();

	private List<Map<String, Object>> data;

	private List<Pattern> patterns = new ArrayList<Pattern>();

	private int pagesize = 100;

	private long defineTotal = 0;

	private Map<String, Object> variables = new HashMap<String, Object>();
	private Map<String, Object> targetVariable = new HashMap<String, Object>();
	private Map<String, Object> sourceVariables = new HashMap<String, Object>();

	public ScriptEngine(SourceService sourceService, TargetService targetService, String script, int pagesize) {
		init();
		this.sourceService = sourceService;
		this.targetService = targetService;
		this.pagesize = pagesize;

		if (pagesize <= 0) {
			// errorMessages.add("Pagesize less than zero");
			setErrors("Pagesize less than zero");
			return;
		}
		try {

			// System.out.println(script);
			Object object = new JSONParser().parse(script);

			JSONObject json = (JSONObject) object;

			parseScript(json);

		} catch (Exception e) {
			// LOGGER.error(e);
			// errorMessages.add(e.getMessage());
			setErrors(e.getMessage());
		}
	}

	protected void init() {
		// @log{variable1}{variable2}{variable3}
		Pattern p1 = Pattern.compile(
				"@log[{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\\\\\_-]*[}]$");
		// @log{variable1}{variable2}{#1}
		Pattern p2 = Pattern.compile(
				"@log[{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{][#][0-9]+[}]$");
		// {variable}
		Pattern p3 = Pattern.compile("[{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}]");
		// {#1}
		Pattern p4 = Pattern.compile("[{][#][0-9]+[}]");
		// {!countername}
		Pattern p5 = Pattern.compile("[{][!][a-zA-Z0-9._]+[}]");
		// SQL select statement
		// Pattern p6 = Pattern.compile("^select[ ][a-zA-Z0-9,_ *]*[ ]from");
		// Pattern p6 = Pattern.compile("^select[ ][a-zA-Z0-9,_ *]*[ ]from",
		// Pattern.CASE_INSENSITIVE);
		Pattern p6 = Pattern.compile("^select[ ](.*)[ ]from", Pattern.CASE_INSENSITIVE);
		patterns.add(p1);
		patterns.add(p2);
		patterns.add(p3);
		patterns.add(p4);
		patterns.add(p5);
		patterns.add(p6);
	}

	protected void buildVariable(Map<String, Object> defineSource, Map<String, Object> defineTarget,
			Map<String, Object> defineVariables, Map<String, Object> dynamicSource, Map<String, Object> dynamicTarget) {

		if (defineSource != null) {
			sourceVariables.putAll(defineSource);
			variables.putAll(defineSource);
		}

		if (defineTarget != null) {
			targetVariable.putAll(defineTarget);
			variables.putAll(defineTarget);
		}

		if (defineVariables != null) {
			variables.putAll(defineVariables);
		}

		if (dynamicSource != null) {
			sourceVariables.putAll(dynamicSource);
			variables.putAll(dynamicSource);
		}

		if (dynamicTarget != null) {
			targetVariable.putAll(dynamicTarget);
			variables.putAll(dynamicTarget);
		}
	}

	protected void parseScript(JSONObject json) {

		LOGGER.info("Starting...");

		datasource = (String) json.get("datasource");

		Map<String, Object> defineSource = ((Map) json.get("define_source"));

		Map<String, Object> defineTarget = ((Map) json.get("define_target"));

		// variables = ((Map) json.get("variables"));
		Map<String, Object> defineVariables = ((Map) json.get("variables"));

		Map<String, Object> dynamicSource = ((Map) json.get("dynamic_source"));

		Map<String, Object> dynamicTarget = ((Map) json.get("dynamic_target"));

		JSONArray updatePatterns = (JSONArray) json.get("update");

		JSONArray writelogPatterns = (JSONArray) json.get("log");
		
		try {
			defineTotal = (long) json.get("count");
		} catch (Exception e) {
			LOGGER.error("not define total");
		}

		if (datasource == null && datasource.isEmpty()) {
			// errorMessages.add("Datasource is empty!");
			setErrors("Datasource is empty!");
			return;
		}

		datasource = datasource.toLowerCase();
		datasource = datasource.replaceAll(" +", " ").trim();

		// calculate
		// Pattern pattern = Pattern.compile("^select[ ][a-z0-9,_ *]*[ ]from");
		Matcher matcher = patterns.get(5).matcher(datasource);
		String selectQuery = "";
		while (matcher.find()) {
			selectQuery = matcher.group();
		}

		if (selectQuery == "") {
			// errorMessages.add("Datasource incorect!");
			setErrors("Datasource incorect!");
			return;
		}

		long total = -1;
		
		if(defineTotal > 0) {
			total = defineTotal;
		}else {
			String countQuery = datasource.replace(selectQuery, "select count(*) as total from");
			LOGGER.info("get total:" + countQuery);
			total = invokeTotalDataSource(countQuery);
		}
		
		if (total < 0) {
			// errorMessages.add("Can't get total datasource");
			setErrors("Can't get total datasource");
			return;
		}

		int page = 1;

		if (total > pagesize) {
			page = (int) total / pagesize;

			if (total % pagesize != 0) {
				page = page + 1;
			}
		}

		for (int p = 0; p < page; p++) {
			int start = p * pagesize;

			LOGGER.info("get data:" + datasource + " LIMIT " + start + "," + pagesize);
			data = invokeDataSource(datasource + " LIMIT " + start + "," + pagesize);

			if (data != null) {
				data.forEach((row) -> {
					
					List<Object> _dataTmp = new ArrayList<Object>();
					JSONObject jsonRowData = new JSONObject();
					jsonRowData.putAll( row );
					LOGGER.info(jsonRowData.toJSONString());
					List<String> colNames = new ArrayList<>();
					row.forEach((k, v) -> {
						colNames.add(k);
						//LOGGER.info("col : " + k + ":" + v);
						// System.out.println(Arrays.toString(row.entrySet().toArray()));
						_dataTmp.add(v);
						
					});
					
					LOGGER.info(Arrays.toString(colNames.toArray()));
					
					if (_dataTmp == null || _dataTmp.isEmpty()) {
						LOGGER.info("===> EMPTY DATA...");
						return;
					}
					
					/*Object[] _dataArrayTmp = new Object[row.size()];
					int _countTmp = 0;
					for(Map.Entry entry : row.entrySet()) {
						_dataArrayTmp[_countTmp] = entry.getValue();
						_countTmp++;
					}
					
					if (_dataArrayTmp == null || _dataArrayTmp.length == 0) {
						LOGGER.info("===> EMPTY DATA...");
						return;
					}*/

					buildVariable(defineSource, defineTarget, defineVariables, dynamicSource, dynamicTarget);

					variables = recursive(variables, _dataTmp.toArray(), 0);
					
					//variables = recursive(variables, _dataArrayTmp, 0);

					// Validate variable
					variables.forEach((k, v) -> {
						// LOGGER.info(k + " <<<:>>> " + v);
						if (v != null && !String.valueOf(v).isEmpty()
								&& String.valueOf(String.valueOf(v).charAt(0)).equals("{")
								&& String.valueOf(String.valueOf(v).charAt(String.valueOf(v).length() - 1))
										.equals("}")) {
							// errorMessages.add("Variable error " + k + "=" + v);
							setErrors("Variable error " + k + "=" + v);
						}
					});

					if (!errorMessages.isEmpty()) {
						return;
					}
					LOGGER.info("===> PROCESS INSERT STATEMENTS...");
					Iterator<String> updateIterator = updatePatterns.iterator();

					while (updateIterator.hasNext()) {
						String sql = updateIterator.next();
						sql = (String) replaceVariableCode(sql, _dataTmp.toArray());
						//sql = (String) replaceVariableCode(sql, _dataArrayTmp);
						sql = new Converter().TCVN3ToUnicode(sql);
						sql = sql.replace("''", "NULL");
						sql = sql.replace("'null'", "NULL");
						sql = sql.replace("\\NULL", "\\''");
						LOGGER.info("===>update " + sql);
						boolean result = targetService.execute(sql);
						if (!result) {
							// errorMessages.add("Can't execute statement " + sql);
							setErrors("Can't execute statement " + sql);
							break;
						}
					}
					LOGGER.info("===> END INSERT STATEMENTS...");
					if (!errorMessages.isEmpty()) {
						return;
					}

					LOGGER.info("===> PROCESS WRITE LOG...");

					Iterator<String> writeLogIterator = writelogPatterns.iterator();

					while (writeLogIterator.hasNext()) {
						String writelogPattern = writeLogIterator.next();
						String insertValues = (String) replaceVariableCode(writelogPattern, _dataTmp.toArray());
						//String insertValues = (String) replaceVariableCode(writelogPattern, _dataArrayTmp);
						LOGGER.info("===>insertValues " + insertValues);
						boolean writeLog = targetService.writeProcessLog(insertValues);

						if (!writeLog) {
							// errorMessages.add("Can't write process log " + insertValues);
							setErrors("Can't write process log " + insertValues);
							break;
						}
					}
					LOGGER.info("===> END WRITE LOG...");
				});
			}
		}

	}

	protected void setErrors(String msg) {
		if (!errorMessages.contains(msg)) {
			errorMessages.add(msg);
		}
	}

	protected String replaceVariableCode(String variableCode, Object[] data) {
		Pattern[] patterns = new Pattern[] { getPatterns().get(2), getPatterns().get(3) };

		for (int i = 0; i < patterns.length; i++) {
			Matcher matcher = patterns[i].matcher(variableCode);
			while (matcher.find()) {

				String group = matcher.group();

				if (group.startsWith("{") && group.endsWith("}")) {

					if (group.contains("#")) {
						try {
							String numberOfIndex = group.replace("#", "").replace("{", "").replace("}", "");
							//LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>::: " + data.length + "|" + numberOfIndex);
							Object _tmp = data[Integer
									.parseInt(numberOfIndex)];
							if (_tmp == null) {
								variableCode = "";
							} else {
								variableCode = variableCode.replace(group,
										StringUtil.escapeApostrophe(_tmp.toString()));
							}

						} catch (Exception e) {
							// LOGGER.error(e);
							setErrors(e.getMessage());
							// errorMessages.add("Can't get value at possition:" + group);
							setErrors("Can't get value at possition:" + group + "|" + Arrays.toString(data));
							break;
						}
					} else {
						// If variables not contain then return null
						String _tmp = group.replace("{", "").replace("}", "");

						if (variables.containsKey(_tmp)) {
							String _valueTmp = variables.get(_tmp).toString();
							boolean hasDone = true;
							for (int p = 0; p < getPatterns().size(); p++) {
								Matcher _matcherTmp = getPatterns().get(p).matcher(_valueTmp);
								while (_matcherTmp.find()) {
									hasDone = false;
									break;
								}

								if (!hasDone) {
									break;
								}
							}

							if (hasDone) {
								// System.out.println("=============================>>>> _valueTmp " +
								// _valueTmp);
								variableCode = variableCode.replace(group, StringUtil.escapeApostrophe(_valueTmp));
							}
							/*String variableCodeTemp = variableCode;
							variableCodeTemp = variableCodeTemp.replace(group, StringUtil
									.escapeApostrophe((String) variables.get(group.replace("{", "").replace("}", ""))));*/
							/*variableCode = variableCode.replace(group, StringUtil
									.escapeApostrophe((String) variables.get(group.replace("{", "").replace("}", ""))));*/

						}

					}

				}
			}
		}

		return variableCode;
	}
	
	/*public static void main(String[] args) {
		String test = "select (case when max(id) is null then 1 else max(id)+1 end) as id from vr_vehicletypecertificate";
		Pattern p6 = Pattern.compile("^select[ ](.*)[ ]from", Pattern.CASE_INSENSITIVE);
		Matcher matcher = p6.matcher(test);
		while (matcher.find()) {
			System.out.println(matcher.group());
		}
	}
	*/
	/*public static void main(String[] args) {
	
		Object[] data = new Object[] {1,2};
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("v1", "2");
		variables.put("v2", "3");
		variables.put("V3", "@log{v1}{v2}{#1}");
		variables.put("v4", "{V3}");
		variables.put("v5", "{V3}");
		variables.put("v6", "{V3}");
		variables.put("v7", "{V3}");
		variables = recursive2(variables, data, 0);
		
		variables.forEach((k, v) -> {
			System.out.println(k + " <<<:>>> " + v);
			
		});
	}
	
	public static Map<String, Object> recursive2(Map<String, Object> map, Object[] data, int count) {
		boolean recursive = false;
	
		for (Map.Entry<String, Object> entry : map.entrySet()) {
	
			String key = entry.getKey();
			String value = (String) entry.getValue();
			fillingVariable2(map, data, key, value);
			if (String.valueOf(map.get(key)).contains("{") && String.valueOf(map.get(key)).contains("}")) {
				recursive = true;
			}
			Pattern p6 = Pattern.compile("^select[ ](.*)[ ]from", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p6.matcher(map.get(key).toString());
			while (matcher.find()) {
				recursive = true;
				break;
			}
			// System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		System.out.println("Count>>>> " + count + ">>>>> " + map.size());
		if (recursive && count <= map.size() * map.size()) {
			count++;
			recursive2(map, data, count);
		}
		return map;
	}
	
	public static void fillingVariable2(Map<String, Object> variables, Object[] data, String key, String value) {
		
		Pattern p1 = Pattern.compile(
				"@log[{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\\\\\_-]*[}]$");
		// @log{variable1}{variable2}{#1}
		Pattern p2 = Pattern.compile(
				"@log[{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}][{][#][0-9]+[}]$");
		// {variable}
		Pattern p3 = Pattern.compile("[{](([a-zA-Z])+|(_))[0-9a-zA-Z\\\\_-]*[}]");
		// {#1}
		Pattern p4 = Pattern.compile("[{][#][0-9]+[}]");
		// {!countername}
		Pattern p5 = Pattern.compile("[{][!][a-zA-Z0-9._]+[}]");
		// SQL select statement
		// Pattern p6 = Pattern.compile("^select[ ][a-zA-Z0-9,_ *]*[ ]from");
		//Pattern p6 = Pattern.compile("^select[ ][a-zA-Z0-9,_  *]*[ ]from", Pattern.CASE_INSENSITIVE);
		Pattern p6 = Pattern.compile("^select[ ](.*)[ ]from", Pattern.CASE_INSENSITIVE);
		List<Pattern> patterns = new ArrayList<Pattern>();
		patterns.add(p1);
		patterns.add(p2);
		patterns.add(p3);
		patterns.add(p4);
		patterns.add(p5);
		patterns.add(p6);
		
		String _valueTmp = value;
		_valueTmp = _valueTmp.trim();
		System.out.println("key:" + key + "<<<|>>>value:" + value);
		for (int i = 0; i < patterns.size(); i++) {
			Matcher matcher = patterns.get(i).matcher(_valueTmp);
			while (matcher.find()) {
	
				String group = matcher.group();
	
				if (group.startsWith("{") && group.endsWith("}")) {
	
					if (group.contains("#")) {
						try {
							value = value.replace(group,
									data[Integer.parseInt(group.replace("#", "").replace("{", "").replace("}", ""))]
											.toString());
							Object _tmp = data[Integer
									.parseInt(group.replace("#", "").replace("{", "").replace("}", ""))];
							if (_tmp == null) {
								value = "";
							} else {
								value = value.replace(group, _tmp.toString());
							}
	
							variables.put(key, value);
							break;
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					} else if (group.contains("!")) {
						String counternamespace = group.replace("{", "").replace("!", "").replace("}", "");
						// String sql = "SELECT currentId FROM counter WHERE name = '" +
						// counternamespace + "';";
						//long currentId = targetService.findCounter(counternamespace);
						long currentId = 10;
						
					} else {
						// If variables not contain then return null
						String _tmp = group.replace("{", "").replace("}", "");
						// select a from b where x = {v1}
						// _tmp = v1
						// _tmp = "select c from d"
						// System.out.println(value + ">>>>>>>>>>>>>>>>" + _tmp);
						if (variables.containsKey(_tmp)) {
							String _tmpValue = (String) variables.get(_tmp);
	
							// Pattern p6 = Pattern.compile("^select[ ][a-zA-Z0-9,_ *]*[ ]from",
							// Pattern.CASE_INSENSITIVE);
	
							Matcher submatcher = patterns.get(5).matcher(_tmpValue);
	
							boolean flag = false;
	
							while (submatcher.find()) {
								flag = true;
								break;
							}
	
							System.out.println(">>>>>>>>>>>>>>>>_tmpValue" + _tmpValue + "|" + flag);
	
							if (!flag) {
								value = value.replace(group, _tmpValue);
							}
							variables.put(key, value);
							break;
						}
	
					}
	
				} else if (group.toLowerCase().contains("@log")) {
					group = group.replaceFirst("@log", "");
					String sql = "SELECT target_value FROM ddc_processlog";
					List<Object> params = new ArrayList<>();
					for (int s = 0; s < patterns.size(); s++) {
						Matcher subMatcher = patterns.get(s).matcher(group);
						while (subMatcher.find()) {
							String subgroup = subMatcher.group();
							Object _valuetmp = "";
							if (subgroup.contains("#")) {
								try {
									_valuetmp = data[Integer
											.parseInt(subgroup.replace("#", "").replace("{", "").replace("}", ""))];
								} catch (Exception e) {
	
									break;
								}
							} else {
								// If variables not contain then return null
								_valuetmp = variables.get(subgroup.replace("{", "").replace("}", ""));
							}
							// params[count] = _valuetmp;
							// count++;
							params.add(_valuetmp);
						}
					}
	
					if (params.size() != 3) {
						// TODO add error
						break;
					}
					sql += " WHERE source_tbl_name ='" + params.get(0) + "'";
					sql += " AND source_col_name ='" + params.get(1) + "'";
					sql += " AND source_value = '" + params.get(2) + "'";
					value = sql;
					variables.put(key, value);
				} else if (group.toLowerCase().contains("select")) {
					System.out.println(">>>>>>>>>>>>>>>group:" + group);	
					value = "1000";
					variables.put(key, value);
	
					break;
	
				}
			}
		}
	}*/

	protected void fillingVariable(Map<String, Object> variables, Object[] data, String key, String value) {
		String _valueTmp = value;
		_valueTmp = _valueTmp.trim();
		// System.out.println("key:" + key + "<<<|>>>value:" + value);
		for (int i = 0; i < patterns.size(); i++) {
			Matcher matcher = patterns.get(i).matcher(_valueTmp);
			while (matcher.find()) {

				String group = matcher.group();

				if (group.startsWith("{") && group.endsWith("}")) {

					if (group.contains("#")) {
						try {
							/*value = value.replace(group,
									data[Integer.parseInt(group.replace("#", "").replace("{", "").replace("}", ""))]
											.toString());*/
							String numberOfIndex = group.replace("#", "").replace("{", "").replace("}", "");
							//LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>2::: " + data.length + "|" + numberOfIndex);
							Object _tmp = data[Integer
									.parseInt(numberOfIndex)];
							if (_tmp == null) {
								value = "";
							} else {
								value = value.replace(group, _tmp.toString());
							}

							variables.put(key, value);
							break;
						} catch (Exception e) {
							// LOGGER.error(e);
							setErrors(e.getMessage());
							// errorMessages.add("Can't get value at possition:" + group);
							setErrors("Can't get value at possition:" + group + "|" + Arrays.toString(data));
							break;
						}
					} else if (group.contains("!")) {
						String counternamespace = group.replace("{", "").replace("!", "").replace("}", "");
						// String sql = "SELECT currentId FROM counter WHERE name = '" +
						// counternamespace + "';";
						long currentId = targetService.findCounter(counternamespace);
						if (currentId > 0) {
							value = String.valueOf(currentId);
							variables.put(key, value);
							currentId++;
							// Update count
							boolean updateCounter = targetService.updateCounter(currentId, counternamespace);
							if (!updateCounter) {
								// errorMessages.add("Can't update counter with name:" + counternamespace + "|"
								// + currentId);
								setErrors("Can't update counter with name:" + counternamespace + "|" + currentId);
								break;
							}
						} else {
							// errorMessages.add("Can't get counter with name:" + counternamespace);
							setErrors("Can't get counter with name:" + counternamespace);
							break;
						}
					} else {
						// If variables not contain then return null
						String _tmp = group.replace("{", "").replace("}", "");
						// select a from b where x = {v1}
						// _tmp = v1
						// _tmp = "select c from d"
						// System.out.println(value + ">>>>>>>>>>>>>>>>" + _tmp);
						if (variables.containsKey(_tmp)) {
							String _tmpValue = (String) variables.get(_tmp);

							// Pattern p6 = Pattern.compile("^select[ ][a-zA-Z0-9,_ *]*[ ]from",
							// Pattern.CASE_INSENSITIVE);

							boolean flag = false;

							Pattern[] _patternsTmp = new Pattern[] { patterns.get(5), patterns.get(0),
									patterns.get(1) };

							for (int p = 0; p < _patternsTmp.length; p++) {
								Matcher submatcher = _patternsTmp[p].matcher(_tmpValue);
								while (submatcher.find()) {
									flag = true;
									break;
								}
								if (flag) {
									break;
								}
							}

							// System.out.println(">>>>>>>>>>>>>>>>_tmpValue" + _tmpValue + "|" + flag);

							if (!flag) {
								value = value.replace(group, _tmpValue);
							}

							variables.put(key, value);

							break;
						}

					}

				} else if (group.contains("@log")) {
					group = group.replaceFirst("@log", "");
					String sql = "select target_value FROM dbc_processlog";
					List<Object> params = new ArrayList<>();
					for (int s = 0; s < patterns.size(); s++) {
						Matcher subMatcher = patterns.get(s).matcher(group);
						while (subMatcher.find()) {
							String subgroup = subMatcher.group();
							Object _valuetmp = "";
							if (subgroup.contains("#")) {
								try {
									_valuetmp = data[Integer
											.parseInt(subgroup.replace("#", "").replace("{", "").replace("}", ""))];
								} catch (Exception e) {

									break;
								}
							} else {
								// If variables not contain then return null
								_valuetmp = variables.get(subgroup.replace("{", "").replace("}", ""));
							}
							// params[count] = _valuetmp;
							// count++;
							if (_valuetmp != null) {
								params.add(_valuetmp);
							}

						}
					}

					if (params.size() != 3) {
						// TODO add error
						break;
					}
					sql += " WHERE source_tbl_name ='" + params.get(0) + "'";
					sql += " AND source_col_name ='" + params.get(1) + "'";
					sql += " AND source_value = '" + params.get(2) + "'";
					value = sql;
					variables.put(key, value);
				} else if (group.toLowerCase().contains("select")) {
					System.out.println("group:" + group + "|" + value);
					if (!value.contains("{") && !value.contains("}")) {
						// System.out.println("invoke:" + value + ">>>>:" + group);
						// invoke sql
						List<Map<String, Object>> _tmp = null;
						String from = "";
						if (targetVariable.containsKey(key)) {
							from = "target";
						} else if (sourceVariables.containsKey(key)) {
							from = "source";
						}
						if (from.equals("target")) {
							_tmp = targetService.receiving(value);
						} else {
							_tmp = sourceService.receiving(value);
						}

						if (_tmp != null && !_tmp.isEmpty()) {

							Map<String, Object> objects = _tmp.get(0);
							if (objects != null) {
								Map.Entry<String, Object> item = objects.entrySet().iterator().next();
								value = String.valueOf(item.getValue());
							}
						} else {
							value = "";
						}
					}

					variables.put(key, value);

					break;

				}
			}
		}
	}

	public Map<String, Object> recursive(Map<String, Object> map, Object[] data, int count) {
		boolean recursive = false;

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = (String) entry.getValue();
			fillingVariable(map, data, key, value);
			if (String.valueOf(map.get(key)).contains("{") && String.valueOf(map.get(key)).contains("}")) {
				recursive = true;
			} else {
				Matcher matcher = patterns.get(5).matcher(map.get(key).toString());
				while (matcher.find()) {
					recursive = true;
					break;
				}
			}

			// System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		// System.out.println("Count>>>> " + count + ">>>>> " + map.size());
		if (recursive && count <= map.size() * map.size()) {
			count++;
			recursive(map, data, count);
		}
		return map;
	}

	protected List<Map<String, Object>> invokeDataSource(String datasource) {
		try {
			// Check pattern datasource sql query or 2-dimensions array
			return sourceService.receiving(datasource);
		} catch (Exception e) {

			// LOGGER.error(e);

			// errorMessages.add("Can't invoke datasource:" + datasource);

			setErrors("Can't invoke datasource:" + datasource);

			return null;
		}
	}

	protected long invokeTotalDataSource(String datasource) {
		try {
			return sourceService.getTotal(datasource);
		} catch (Exception e) {

			// LOGGER.error(e);

			// errorMessages.add("Can't invoke total datasource:" + datasource);

			setErrors("Can't invoke total datasource:" + datasource);
			setErrors(e.getMessage());

			return -1;
		}
	}

	class Converter {
		private char[] tcvnchars = { 'µ', '¸', '¶', '·', '¹', '¨', '»', '¾', '¼', '½', 'Æ', '©', 'Ç', 'Ê', 'È', 'É',
				'Ë', '®', 'Ì', 'Ð', 'Î', 'Ï', 'Ñ', 'ª', 'Ò', 'Õ', 'Ó', 'Ô', 'Ö', '×', 'Ý', 'Ø', 'Ü', 'Þ', 'ß', 'ã', 'á',
				'â', 'ä', '«', 'å', 'è', 'æ', 'ç', 'é', '¬', 'ê', 'í', 'ë', 'ì', 'î', 'ï', 'ó', 'ñ', 'ò', 'ô', '­', 'õ',
				'ø', 'ö', '÷', 'ù', 'ú', 'ý', 'û', 'ü', 'þ', '¡', '¢', '§', '£', '¤', '¥', '¦' };

		private char[] unichars = { 'à', 'á', 'ả', 'ã', 'ạ', 'ă', 'ằ', 'ắ', 'ẳ', 'ẵ', 'ặ', 'â', 'ầ', 'ấ', 'ẩ', 'ẫ', 'ậ',
				'đ', 'è', 'é', 'ẻ', 'ẽ', 'ẹ', 'ê', 'ề', 'ế', 'ể', 'ễ', 'ệ', 'ì', 'í', 'ỉ', 'ĩ', 'ị', 'ò', 'ó', 'ỏ', 'õ',
				'ọ', 'ô', 'ồ', 'ố', 'ổ', 'ỗ', 'ộ', 'ơ', 'ờ', 'ớ', 'ở', 'ỡ', 'ợ', 'ù', 'ú', 'ủ', 'ũ', 'ụ', 'ư', 'ừ', 'ứ',
				'ử', 'ữ', 'ự', 'ỳ', 'ý', 'ỷ', 'ỹ', 'ỵ', 'Ă', 'Â', 'Đ', 'Ê', 'Ô', 'Ơ', 'Ư' };

		private char[] convertTable;

		public Converter() {
			convertTable = new char[256];
			for (int i = 0; i < 256; i++)
				convertTable[i] = (char) i;
			for (int i = 0; i < tcvnchars.length; i++)
				convertTable[tcvnchars[i]] = unichars[i];
		}

		public String TCVN3ToUnicode(String value) {
			char[] chars = value.toCharArray();
			for (int i = 0; i < chars.length; i++)
				if (chars[i] < (char) 256)
					chars[i] = convertTable[chars[i]];
			String rstr = new String(chars);
			return rstr;
		}
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public List<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
	}

	public long getDefineTotal() {
		return defineTotal;
	}

	public void setDefineTotal(long defineTotal) {
		this.defineTotal = defineTotal;
	}

}
