package com.almighty.dbc.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almighty.dbc.constant.Constants;
import com.almighty.dbc.dao.TargetDAO;
import com.almighty.dbc.model.ConvertTask;
import com.almighty.dbc.model.ProcessLog;
import com.almighty.dbc.model.ScriptQueue;
import com.almighty.dbc.scriptqueue.engine.ScriptEngine;
import com.almighty.dbc.service.SourceService;
import com.almighty.dbc.service.TargetService;
import com.almighty.dbc.util.StringUtil;

/**
 * @author trungnt
 *
 */
@Service
public class TargetServiceImpl implements TargetService {
	@Autowired
	private TargetDAO targetDAO;

	@Override
	public boolean createConvertTask(ConvertTask convertTask) {
		String sql = "INSERT INTO dbc_converttask(" + "task_name," + "source_config," + "target_config," + "status"
				+ ")" + "VALUES(";
		sql += "'" + convertTask.getTaskName() + "',";
		sql += "'" + convertTask.getSourceConfig() + "',";
		sql += "'" + convertTask.getTargetConfig() + "',";

		sql += "'" + convertTask.getStatus() + "');\n";

		return targetDAO.execute(sql);
	}

	@Override
	public boolean deleteConvertTask(long taskId) {
		String sql1 = "DELETE FROM dbc_converttask WHERE task_id = " + taskId + ";";
		String sql2 = "DELETE FROM dbc_scriptqueue WHERE task_id = " + taskId + ";";
		String[] sqls = new String[] { sql1, sql2 };
		return targetDAO.execute(sqls);
	}

	@Override
	public boolean updateConvertTask(ConvertTask convertTask) {
		String sql = "UPDATE dbc_converttask SET " + "task_name = '" + convertTask.getTaskName() + "',"
				+ "source_config = '" + convertTask.getSourceConfig() + "'," + "target_config = '"
				+ convertTask.getTargetConfig() + "'," + "status = " + convertTask.getStatus() + " WHERE task_id = "
				+ convertTask.getTaskId() + ";";

		return targetDAO.execute(sql);
	}

	@Override
	public ConvertTask findConvertTaskByPrimaryKey(long taskId) {
		String sql = "SELECT * FROM dbc_converttask WHERE task_id = ?;";

		ConvertTask convertTask = targetDAO.findConvertTaskByPrimaryKey(sql, taskId);
		if (convertTask != null) {
			List<ScriptQueue> scriptQueues = findScriptQueueByTaskId(taskId);
			if (scriptQueues != null) {
				convertTask.setScriptQueues(new HashSet<>(scriptQueues));
			}
		}

		return convertTask;
	}

	@Override
	public List<ConvertTask> getConvertTasks(int start, int end) {
		String sql = "SELECT * FROM dbc_converttask";
		if (start >= 0 && end > start) {
			sql += " LIMIT " + start + "," + end;
		}

		List<ConvertTask> convertTasks = targetDAO.getConvertTasks(sql);

		if (convertTasks != null) {
			for (ConvertTask convertTask : convertTasks) {
				List<ScriptQueue> scriptQueues = findScriptQueueByTaskId(convertTask.getTaskId());
				if (scriptQueues != null) {
					convertTask.setScriptQueues(new HashSet<>(scriptQueues));
				}

			}
		}

		return convertTasks;
	}

	@Override
	public boolean createScriptQueue(ScriptQueue scriptQueue) {
		String sql = "INSERT INTO dbc_scriptqueue(" + "task_id," + "script_name," + "script," + "order_," + "status"
				+ ")" + "VALUES(";
		sql += "'" + scriptQueue.getTaskId() + "',";
		sql += "'" + scriptQueue.getScriptName() + "',";
		sql += "'" + scriptQueue.getScript() + "',";
		sql += "'" + scriptQueue.getOrder_() + "',";
		sql += "'" + scriptQueue.getStatus() + "');\n";

		return targetDAO.execute(sql);
	}

	@Override
	public boolean deleteScriptQueue(long queueId) {
		String sql = "DELETE FROM dbc_scriptqueue WHERE queue_id = " + queueId + ";";
		return targetDAO.execute(sql);
	}

	@Override
	public boolean updateScriptQueue(ScriptQueue scriptQueue) {
		String sql = "UPDATE dbc_scriptqueue SET " + "script_name = '"
				+ StringUtil.escapeApostrophe(scriptQueue.getScriptName()) + "'," + "script = '"
				+ StringUtil.escapeApostrophe(scriptQueue.getScript()) + "'," + "order_ = '" + scriptQueue.getOrder_()
				+ "'," + "status = " + scriptQueue.getStatus() + " WHERE queue_id = " + scriptQueue.getQueueId() + ";";

		return targetDAO.execute(sql);
	}

	@Override
	public ScriptQueue findScriptQueueByPrimaryKey(long queueId) {
		String sql = "SELECT * FROM dbc_scriptqueue WHERE queue_id = ?;";
		return targetDAO.findScriptQueueByPrimaryKey(sql, queueId);
	}

	@Override
	public List<ScriptQueue> getScriptQueues(int start, int end) {
		String sql = "SELECT * FROM dbc_scriptqueue";
		if (start >= 0 && end > start) {
			sql += " LIMIT " + start + "," + end;
		}

		return targetDAO.getScriptQueue(sql);
	}

	@Override
	public List<ScriptQueue> findScriptQueueByTaskId(long taskId) {
		String sql = "SELECT * FROM dbc_scriptqueue WHERE task_id = " + taskId;
		return targetDAO.getScriptQueue(sql);
	}

	@Override
	public boolean createProcessLog(ProcessLog processLog) {
		String sql = "INSERT INTO dbc_processlog(" + "source_tbl_name," + "source_col_name," + "source_value,"
				+ "target_tbl_name," + "target_col_name," + "target_value)" + "VALUES(";
		sql += "'" + processLog.getSourceTblName() + "',";
		sql += "'" + processLog.getSourceColName() + "',";
		sql += "'" + processLog.getSourceValue() + "',";
		sql += "'" + processLog.getTargetTblName() + "',";
		sql += "'" + processLog.getTargetColName() + "',";
		sql += "'" + processLog.getTargetValue() + "');\n";

		return targetDAO.execute(sql);
	}

	@Override
	public boolean deleteProcessLog(String sourceTblName) {
		String sql = "DELETE FROM dbc_processlog WHERE source_tbl_name = " + sourceTblName + ";";
		return targetDAO.execute(sql);
	}

	@Override
	public boolean deleteAllProcessLog() {
		String sql = "DELETE FROM dbc_processlog;";
		return targetDAO.execute(sql);
	}

	@Override
	public ProcessLog findProcessLogBy_STN_SCN_SV(String sourceTblName, String sourceColName, String sourceValue) {
		String sql = "SELECT * FROM dbc_processlog WHERE source_tbl_name = ? AND source_col_name = ? AND source_value = ?;";
		return targetDAO.findProcessLogBy_STN_SCN_SV(sql, sourceTblName, sourceColName, sourceValue);
	}

	@Override
	public String findTargetValueBy_STN_SCN_SV(String sourceTblName, String sourceColName, String sourceValue) {
		String sql = "SELECT target_value FROM dbc_processlog WHERE source_tbl_name = ? AND source_col_name = ? AND source_value = ?;";
		return targetDAO.findTargetValueBy_STN_SCN_SV(sql, sourceTblName, sourceColName, sourceValue);
	}

	@Override
	public List<ProcessLog> getProcessLogBy_STN(String sourceTblName, int start, int end) {
		String sql = "SELECT * FROM dbc_processlog WHERE source_tbl_name = " + sourceTblName;
		if (start >= 0 && end > start) {
			sql += " LIMIT " + start + "," + end;
		}

		return targetDAO.getProcessLog(sql);
	}

	@Override
	public List<String> findScripts(long taskId) {
		String sql = "SELECT script FROM dbc_scriptqueue WHERE task_id = " + taskId + " AND status = "
				+ Constants.ACTIVE + " ORDER BY order_ ASC;";
		return targetDAO.findScripts(sql);
	}

	@Override
	public String doExecute(SourceService sourceService, TargetService targetService, long taskId, int pagesize) {
		List<String> scripts = findScripts(taskId);
		if (scripts != null) {
			for (String script : scripts) {
				ScriptEngine scriptEngine = new ScriptEngine(sourceService,
						targetService == null ? this : targetService, script, pagesize);
				if (!scriptEngine.getErrorMessages().isEmpty()) {
					return Arrays.toString(scriptEngine.getErrorMessages().toArray());
				}
			}
		}

		return "success";
	}

	@Override
	public String doExecuteByPrimaryKey(SourceService sourceService, TargetService targetService, long queueId,
			int pagesize) {
		ScriptQueue scriptQueue = findScriptQueueByPrimaryKey(queueId);
		if (scriptQueue != null && scriptQueue.getScript() != null && !scriptQueue.getScript().isEmpty()) {
			ScriptEngine scriptEngine = new ScriptEngine(sourceService, targetService == null ? this : targetService,
					scriptQueue.getScript(), pagesize);
			if (!scriptEngine.getErrorMessages().isEmpty()) {
				return Arrays.toString(scriptEngine.getErrorMessages().toArray());
			}
		}

		return "success";
	}

	@Override
	public List<Map<String, Object>> receiving(String sql) {
		return targetDAO.received(sql);
	}

	@Override
	public long findCounter(String name) {
		String sql = "SELECT currentId FROM counter WHERE name = '" + name + "';";
		return targetDAO.findCounter(sql);
	}

	@Override
	public boolean updateCounter(long currentId, String name) {
		String sql = "UPDATE counter SET currentId = " + currentId + "  WHERE name = '" + name + "';";
		return targetDAO.execute(sql);
	}

	@Override
	public String findTargetLogvalue(String sql) {

		return targetDAO.findTargetLogvalue(sql);
	}

	@Override
	public boolean writeProcessLog(String values) {
		String sql = "INSERT INTO dbc_processlog(source_tbl_name, source_col_name, source_value, target_tbl_name, target_col_name, target_value) VALUES(";
		sql += values;
		sql += ");";
		return targetDAO.execute(sql);
	}

	@Override
	public boolean execute(String sql) {
		return targetDAO.execute(sql);
	}
}
