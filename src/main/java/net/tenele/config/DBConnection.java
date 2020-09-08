package net.tenele.config;

import org.springframework.stereotype.Component;

@Component
public class DBConnection {
	private String connname;
	private String hostname;
    private String dbname;
    private String dbPort;
    private String username;
    private String password;
	public String getConnname() {
		return connname;
	}
	public void setConnname(String connname) {
		this.connname = connname;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getDbPort() {
		return dbPort;
	}
	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
}
