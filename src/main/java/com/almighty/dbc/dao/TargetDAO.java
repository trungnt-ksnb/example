package com.almighty.dbc.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almighty.dbc.model.ConvertTask;
import com.almighty.dbc.model.ProcessLog;
import com.almighty.dbc.model.ScriptQueue;

/**
 * @author trungnt
 *
 */
@Transactional
@Repository
public class TargetDAO {
	@Autowired
	@Qualifier("jdbcTemplateTarget")
	private JdbcTemplate jdbcTemplate;
	private Log LOGGER = LogFactory.getLog(TargetDAO.class);

	public boolean execute(String sql) {
		LOGGER.info("TargetDAO:execute " + sql);
		int result = jdbcTemplate.update(sql);
		return result == 1 ? true : false;
	}

	public boolean execute(String[] sqls) {
		LOGGER.info("TargetDAO:execute " + Arrays.toString(sqls));

		int[] results = jdbcTemplate.batchUpdate(sqls);

		if (results == null || results.length == 0) {
			return false;
		}

		if (results[0] == 1) {
			return true;
		}

		return false;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> received(String sql) {

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		return rows;
	}

	public ConvertTask findConvertTaskByPrimaryKey(String sql, long taskId) {
		LOGGER.info("TargetDAO:findConvertTaskByPrimaryKey " + sql + "|" + taskId);

		List<ConvertTask> convertTasks = jdbcTemplate.query(sql, new Object[] { taskId },
				new BeanPropertyRowMapper<ConvertTask>(ConvertTask.class));
		if (convertTasks != null && !convertTasks.isEmpty()) {
			return convertTasks.get(0);
		}

		return null;
	}

	public List<ConvertTask> getConvertTasks(String sql) {
		List<ConvertTask> convertTasks = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<ConvertTask>(ConvertTask.class));
		return convertTasks;
	}

	public ScriptQueue findScriptQueueByPrimaryKey(String sql, long queueId) {
		LOGGER.info("TargetDAO:findScriptQueueByPrimaryKey " + sql + "|" + queueId);

		List<ScriptQueue> scriptQueues = jdbcTemplate.query(sql, new Object[] { queueId },
				new BeanPropertyRowMapper<ScriptQueue>(ScriptQueue.class));
		if (scriptQueues != null && !scriptQueues.isEmpty()) {
			return scriptQueues.get(0);
		}

		return null;
	}

	public List<ScriptQueue> getScriptQueue(String sql) {
		List<ScriptQueue> scriptQueues = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<ScriptQueue>(ScriptQueue.class));
		return scriptQueues;
	}

	public List<ProcessLog> getProcessLog(String sql) {
		List<ProcessLog> processLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ProcessLog>(ProcessLog.class));
		return processLogs;
	}

	public ProcessLog findProcessLogBy_STN_SCN_SV(String sql, String sourceTblName, String sourceColName,
			String sourceValue) {
		LOGGER.info("TargetDAO:findProcessLogBy_STN_SCN_SV " + sql + "|" + sourceTblName + "|" + sourceColName + "|"
				+ sourceValue);

		List<ProcessLog> processLogs = jdbcTemplate.query(sql,
				new Object[] { sourceTblName, sourceColName, sourceValue },
				new BeanPropertyRowMapper<ProcessLog>(ProcessLog.class));
		if (processLogs != null && !processLogs.isEmpty()) {
			return processLogs.get(0);
		}

		return null;
	}

	public String findTargetValueBy_STN_SCN_SV(String sql, String sourceTblName, String sourceColName,
			String sourceValue) {
		LOGGER.info("TargetDAO:findTargetValueBy_STN_SCN_SV " + sql + "|" + sourceTblName + "|" + sourceColName + "|"
				+ sourceValue);

		List<String> processLogs = jdbcTemplate.query(sql, new Object[] { sourceTblName, sourceColName, sourceValue },
				new BeanPropertyRowMapper<String>(String.class));
		if (processLogs != null && !processLogs.isEmpty()) {
			return processLogs.get(0);
		}

		return null;
	}

	public List<String> findScripts(String sql) {
		LOGGER.info("TargetDAO:findScripts " + sql);

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		if (rows != null) {
			List<String> results = new ArrayList<>();
			rows.forEach((row) -> {
				row.forEach((k, v) -> {
					results.add(v.toString());
				});
			});
			return results;
		}

		return null;
	}

	public long findCounter(String sql) {
		/*List<Long> counters = jdbcTemplate.query(sql, new Object[] { name },
				new BeanPropertyRowMapper<Long>(Long.class));
		if (counters != null && !counters.isEmpty()) {
			return counters.get(0);
		}*/

		LOGGER.info("TargetDAO:invoke>>> " + sql);

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		if (rows != null && !rows.isEmpty()) {
			return (Long) rows.get(0).get("currentId");
		}

		return -1;
	}

	public String findTargetLogvalue(String sql) {
		/*List<String> logvalues = jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<String>(String.class));
		if (logvalues != null && !logvalues.isEmpty()) {
			return logvalues.get(0);
		}*/

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		if (rows != null && !rows.isEmpty()) {
			return (String) rows.get(0).get("target_value");
		}

		return "";
	}

}
