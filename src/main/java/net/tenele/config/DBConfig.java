package net.tenele.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
@Configuration
@Component
@ConfigurationProperties(prefix = "kettledb")
public class DBConfig {
	private List<DBConnection> dbConnections;

	public  List<DBConnection> getDbConnections() {
		return dbConnections;
	}

	public void setDbConnections(List<DBConnection> dbConnections) {
		this.dbConnections = dbConnections;
	}
	
}

