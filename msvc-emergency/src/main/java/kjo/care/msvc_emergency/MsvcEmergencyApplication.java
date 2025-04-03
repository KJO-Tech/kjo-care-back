package kjo.care.msvc_emergency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class MsvcEmergencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcEmergencyApplication.class, args);
	}

}
