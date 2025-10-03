package kjo.care.msvc_dailyActivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsvcDailyActivityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcDailyActivityApplication.class, args);
	}

}
