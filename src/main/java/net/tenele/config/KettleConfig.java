package net.tenele.config;

import javax.annotation.PostConstruct;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KettleConfig {
	@Value("${KETTLE_HOME}")
	public String KETTLE_HOME;
	@PostConstruct
	public void initEnv() {
		try {
			System.setProperty("KETTLE_REDIRECT_STDOUT", "Y");
			System.setProperty("KETTLE_REDIRECT_STDERR", "Y");
			System.setProperty("KETTLE_HOME",KETTLE_HOME);
			KettleEnvironment.init();
			EnvUtil.environmentInit();
//			Thread heartBeatT = new Thread(new HeartBeatT());
//			heartBeatT.setDaemon(true);
//			heartBeatT.start();
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}
}
