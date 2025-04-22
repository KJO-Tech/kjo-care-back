package kjo.care.msvc_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsvcAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcAuthApplication.class, args);
	}

}
