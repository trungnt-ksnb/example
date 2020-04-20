package com.almighty.dbc.controler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.almighty.dbc.model.ConvertTask;
import com.almighty.dbc.model.ProcessLog;
import com.almighty.dbc.model.ScriptQueue;
import com.almighty.dbc.service.impl.DynamicServiceImpl;
import com.almighty.dbc.service.impl.SourceServiceImpl;
import com.almighty.dbc.service.impl.TargetServiceImpl;

/**
 * @author trungnt
 *
 */
@RestController
@RequestMapping("/api/dbc/convert")
public class ConvertControler {
	// private Log LOGGER = LogFactory.getLog(ConvertControler.class);

	@Value("${app.page.size}")
	private int pagesize;

	@Autowired
	private SourceServiceImpl sourceService;

	@Autowired
	private TargetServiceImpl targetService;

	@Autowired
	private DynamicServiceImpl dynamicService;

	//@Autowired
	//private FileStorageServiceImpl fileStorageService;

	@RequestMapping(value = "/converttask/create", method = RequestMethod.POST)
	public ResponseEntity<?> createConvertTask(HttpServletRequest request, HttpSession session,
			@RequestBody ConvertTask convertTask) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.createConvertTask(convertTask));
	}

	@RequestMapping(value = "/converttask/delete/{taskId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteConvertTask(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "taskId") long taskId) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.deleteConvertTask(taskId));
	}

	@RequestMapping(value = "/converttask/update/{taskId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateConvertTask(HttpServletRequest request, HttpSession session,
			@RequestBody ConvertTask convertTaskModel, @PathVariable(value = "taskId") long taskId) {
		ConvertTask convertTask = targetService.findConvertTaskByPrimaryKey(taskId);

		if (convertTask != null) {
			convertTask.setSourceConfig(convertTaskModel.getSourceConfig());
			convertTask.setStatus(convertTaskModel.getStatus());
			convertTask.setTargetConfig(convertTaskModel.getTargetConfig());
			convertTask.setTaskName(convertTaskModel.getTaskName());
			return ResponseEntity.status(HttpStatus.OK).body(targetService.updateConvertTask(convertTask));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found converttask");
		}

	}

	@RequestMapping(value = "/converttask/find/{taskId}", method = RequestMethod.GET)
	public ResponseEntity<?> findConvertTaskByPrimaryKey(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "taskId") long taskId) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.findConvertTaskByPrimaryKey(taskId));
	}

	@RequestMapping(value = "/converttask/get", method = RequestMethod.GET)
	public ResponseEntity<?> getConvertTasks(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "end", defaultValue = "10") int end) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.getConvertTasks(start, end));
	}

	@RequestMapping(value = "/scriptqueue/create", method = RequestMethod.POST)
	public ResponseEntity<?> createScriptQueue(HttpServletRequest request, HttpSession session,
			@RequestBody ScriptQueue scriptQueue) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.createScriptQueue(scriptQueue));
	}

	@RequestMapping(value = "/scriptqueue/delete/{queueId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteScriptQueue(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "queueId") long queueId) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.deleteScriptQueue(queueId));
	}

	@RequestMapping(value = "/scriptqueue/update/{queueId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateScriptQueue(HttpServletRequest request, HttpSession session,
			@RequestBody ScriptQueue scriptQueueModel, @PathVariable(value = "queueId") long queueId) {
		ScriptQueue scriptQueue = targetService.findScriptQueueByPrimaryKey(queueId);
		if (scriptQueue != null) {
			if (scriptQueueModel.getOrder_() >= 0) {
				scriptQueue.setOrder_(scriptQueueModel.getOrder_());
			}

			if (scriptQueueModel.getScript() != null) {
				scriptQueue.setScript(scriptQueueModel.getScript());
			}
			if (scriptQueueModel.getScriptName() != null) {
				scriptQueue.setScriptName(scriptQueueModel.getScriptName());
			}
			if (scriptQueueModel.getStatus() >= -1) {
				scriptQueue.setStatus(scriptQueueModel.getStatus());
			}

			return ResponseEntity.status(HttpStatus.OK).body(targetService.updateScriptQueue(scriptQueue));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found scriptqueue");
		}
	}

	@RequestMapping(value = "/scriptqueue/findone/{queueId}", method = RequestMethod.GET)
	public ResponseEntity<?> findScriptQueueByPrimaryKey(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "queueId") long queueId) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.findScriptQueueByPrimaryKey(queueId));
	}

	@RequestMapping(value = "/scriptqueue/get", method = RequestMethod.GET)
	public ResponseEntity<?> getScriptQueues(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "end", defaultValue = "10") int end) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.getScriptQueues(start, end));
	}

	@RequestMapping(value = "/scriptqueue/find/{taskId}", method = RequestMethod.GET)
	public ResponseEntity<?> findScriptQueueByTaskId(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "taskId") long taskId) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.findScriptQueueByTaskId(taskId));
	}

	@RequestMapping(value = "/processlog/create", method = RequestMethod.POST)
	public ResponseEntity<?> createProcessLog(HttpServletRequest request, HttpSession session,
			@RequestBody ProcessLog processLog) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.createProcessLog(processLog));
	}

	@RequestMapping(value = "/processlog/delete/{sourceTblName}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProcessLog(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "sourceTblName") String sourceTblName) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.deleteProcessLog(sourceTblName));
	}

	@RequestMapping(value = "/processlog/deleteall", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteAllProcessLog(HttpServletRequest request, HttpSession session) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.deleteAllProcessLog());
	}

	@RequestMapping(value = "/processlog/findone/{sourceTblName}/{sourceColName}/{sourceValue}", method = RequestMethod.GET)
	public ResponseEntity<?> findProcessLogBy_STN_SCN_SV(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "sourceTblName") String sourceTblName,
			@PathVariable(value = "sourceColName") String sourceColName,
			@PathVariable(value = "sourceValue") String sourceValue) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(targetService.findProcessLogBy_STN_SCN_SV(sourceTblName, sourceColName, sourceValue));
	}

	@RequestMapping(value = "/processlog/targetvalue/{sourceTblName}/{sourceColName}/{sourceValue}", method = RequestMethod.GET)
	public ResponseEntity<?> findTargetValueBy_STN_SCN_SV(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "sourceTblName") String sourceTblName,
			@PathVariable(value = "sourceColName") String sourceColName,
			@PathVariable(value = "sourceValue") String sourceValue) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(targetService.findTargetValueBy_STN_SCN_SV(sourceTblName, sourceColName, sourceValue));
	}

	@RequestMapping(value = "/processlog/find/{sourceTblName}", method = RequestMethod.GET)
	public ResponseEntity<?> getProcessLogBy_STN(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "sourceTblName") String sourceTblName,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "end", defaultValue = "10") int end) {
		return ResponseEntity.status(HttpStatus.OK).body(targetService.getProcessLogBy_STN(sourceTblName, start, end));
	}

	@RequestMapping(value = "/execute/{taskId}", method = RequestMethod.POST)
	public ResponseEntity<?> doExecute(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "taskId") long taskId) {
		String result = targetService.doExecute(sourceService, null, taskId, pagesize);
		if (result.equals("success")) {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@RequestMapping(value = "/execute/one/{queueId}", method = RequestMethod.POST)
	public ResponseEntity<?> doExecuteOne(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "queueId") long queueId) {
		String result = targetService.doExecuteByPrimaryKey(sourceService, targetService, queueId, pagesize);
		if (result.equals("success")) {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@RequestMapping(value = "/tablecolumn/updatevalue", method = RequestMethod.POST)
	public ResponseEntity<?> doUpdateTableValue(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "driverClassName") String driverClassName, @RequestParam(value = "url") String url,
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
			@RequestParam(value = "dbName") String dbName,
			@RequestParam(value = "primaryColumnName") String primaryColumnName,
			@RequestParam(value = "replaceColumnName") String replaceColumnName,
			@RequestParam("file") MultipartFile file, @RequestParam(value = "excelSheetIndex") int excelSheetIndex,
			@RequestParam(value = "excelColumnIndex") int excelColumnIndex,
			@RequestParam(value = "excelMappingColumnIndex") int excelMappingColumnIndex) {

		JSONObject results = new JSONObject();

		try {

			results = dynamicService.updateTableColumnValue(driverClassName, url, username, password, dbName,
					primaryColumnName, replaceColumnName, file.getInputStream(), excelSheetIndex, excelColumnIndex,
					excelMappingColumnIndex);

			return ResponseEntity.status(HttpStatus.OK).body(results.toJSONString());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}
}
