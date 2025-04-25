package kjo.care.msvc_moodTracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsvcMoodTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsvcMoodTrackingApplication.class, args);
    }
}
