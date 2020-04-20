package com.almighty.dbc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author trungnt
 *
 */
@Entity
@Table(name = "dbc_processlog")
public class ProcessLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id")
	private long logId;

	@Column(name = "source_tbl_name")
	@NotBlank
	private String sourceTblName;

	@Column(name = "source_col_name")
	@NotBlank
	private String sourceColName;

	@Column(name = "source_value")
	// @NotBlank
	private String sourceValue;

	@Column(name = "target_tbl_name")
	private String targetTblName;

	@Column(name = "target_col_name")
	private String targetColName;

	@Column(name = "target_value")
	private String targetValue;

	public ProcessLog() {

	}

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	public String getSourceTblName() {
		return sourceTblName;
	}

	public void setSourceTblName(String sourceTblName) {
		this.sourceTblName = sourceTblName;
	}

	public String getSourceColName() {
		return sourceColName;
	}

	public void setSourceColName(String sourceColName) {
		this.sourceColName = sourceColName;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getTargetTblName() {
		return targetTblName;
	}

	public void setTargetTblName(String targetTblName) {
		this.targetTblName = targetTblName;
	}

	public String getTargetColName() {
		return targetColName;
	}

	public void setTargetColName(String targetColName) {
		this.targetColName = targetColName;
	}

	public String getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}

}
