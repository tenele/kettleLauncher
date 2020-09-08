package net.tenele.util;

import org.pentaho.di.core.Result;

public class JobResult {
	StringBuffer log;
	Result result;
	public StringBuffer getLog() {
		return log;
	}
	public void setLog(StringBuffer log) {
		this.log = log;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
}
