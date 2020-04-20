package com.almighty.dbc.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.almighty.dbc.model.ConvertTask;
import com.almighty.dbc.model.ProcessLog;
import com.almighty.dbc.model.ScriptQueue;

/**
 * @author trungnt
 *
 */
@Service
public interface TargetService {

	public boolean execute(String sql);

	public boolean createConvertTask(ConvertTask convertTask);

	public boolean deleteConvertTask(long taskId);

	public boolean updateConvertTask(ConvertTask convertTask);

	public ConvertTask findConvertTaskByPrimaryKey(long taskId);

	public List<ConvertTask> getConvertTasks(int start, int end);

	public boolean createScriptQueue(ScriptQueue scriptQueue);

	public boolean deleteScriptQueue(long queue);

	public boolean updateScriptQueue(ScriptQueue scriptQueue);

	public ScriptQueue findScriptQueueByPrimaryKey(long queueId);

	public List<ScriptQueue> getScriptQueues(int start, int end);

	public List<ScriptQueue> findScriptQueueByTaskId(long taskId);

	public boolean createProcessLog(ProcessLog processLog);

	public boolean deleteProcessLog(String sourceTblName);

	public boolean deleteAllProcessLog();

	public ProcessLog findProcessLogBy_STN_SCN_SV(String sourceTblName, String sourceColName, String sourceValue);

	public String findTargetValueBy_STN_SCN_SV(String sourceTblName, String sourceColName, String sourceValue);

	public List<ProcessLog> getProcessLogBy_STN(String sourceTblName, int start, int end);

	public List<String> findScripts(long taskId);

	public String doExecute(SourceService sourceService, TargetService targetService, long taskId, int pagesize);

	public List<Map<String, Object>> receiving(String sql);

	public long findCounter(String name);

	public String findTargetLogvalue(String sql);

	public boolean writeProcessLog(String values);

	public boolean updateCounter(long currentId, String name);
	
	public String doExecuteByPrimaryKey(SourceService sourceService, TargetService targetService, long queueId, int pagesize);
	
}
