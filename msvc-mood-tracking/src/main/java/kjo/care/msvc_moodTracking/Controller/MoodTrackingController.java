package kjo.care.msvc_moodTracking.Controller;

import kjo.care.msvc_moodTracking.services.Impl.MoodUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mood-tracking")
@RequiredArgsConstructor
@Validated
@Log4j2
public class MoodTrackingController {
    private final MoodUserService moodUserService;

    @GetMapping("")
    public String getMoodTracking() {
        return "Hello Mundo";
    }
}
