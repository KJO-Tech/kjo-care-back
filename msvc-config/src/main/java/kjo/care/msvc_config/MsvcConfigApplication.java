package kjo.care.msvc_config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MsvcConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcConfigApplication.class, args);
	}

}
