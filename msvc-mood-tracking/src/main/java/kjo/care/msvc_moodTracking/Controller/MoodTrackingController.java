package kjo.care.msvc_moodTracking.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoodTrackingController {

    @GetMapping("/mood-tracking")
    public String getMoodTracking() {
        return "Hello Mood Tracking";
    }
}
