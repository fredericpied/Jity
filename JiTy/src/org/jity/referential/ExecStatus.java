package org.jity.referential;

import java.io.File;
import java.util.Date;

public class ExecStatus {

	private long id;
	private Date execDate;
	private Date begin;
	private Date end;
	private int status;
	private Job job;
	private String statusMessage;
	private String logFile;
	
	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public static final int NOT_PLANED = 1;
	public static final int PLANED = 2;
	public static final int RUNNING = 3;
	public static final int OK = 4;
	public static final int KO = 5;

	public ExecStatus() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Date getExecDate() {
		return execDate;
	}

	public void setExecDate(Date execDate) {
		this.execDate = execDate;
	}

	
	
}
