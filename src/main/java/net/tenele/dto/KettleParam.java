package net.tenele.dto;

import java.io.Serializable;
import java.util.Map;

public class KettleParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8258397979902929858L;
	private String uuid;
	private String kjbPath;
	private Map<String,String> params;
	public String getKjbPath() {
		return kjbPath;
	}
	public void setKjbPath(String kjbPath) {
		this.kjbPath = kjbPath;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
