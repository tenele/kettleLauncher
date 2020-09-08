package net.tenele;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan(basePackages = {"com.parkson.*"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class KettleLauncherApplication {

	public static void main(String[] args) throws Exception{
		SpringApplication.run(KettleLauncherApplication.class, args);
	}

}
