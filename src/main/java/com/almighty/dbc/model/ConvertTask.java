package com.almighty.dbc.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author trungnt
 *
 */
@Entity
@Table(name = "dbc_converttask")
public class ConvertTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private long taskId;

	@Column(name = "task_name")
	@NotBlank
	private String taskName;

	@Column(name = "source_config")
	private String sourceConfig;

	@Column(name = "target_config")
	private String targetConfig;

	@Column(name = "status")
	private int status;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ScriptQueue> scriptQueues;

	public ConvertTask() {

	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getSourceConfig() {
		return sourceConfig;
	}

	public void setSourceConfig(String sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	public String getTargetConfig() {
		return targetConfig;
	}

	public void setTargetConfig(String targetConfig) {
		this.targetConfig = targetConfig;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Set<ScriptQueue> getScriptQueues() {
		return scriptQueues;
	}

	public void setScriptQueues(Set<ScriptQueue> scriptQueues) {
		this.scriptQueues = scriptQueues;
	}

}
