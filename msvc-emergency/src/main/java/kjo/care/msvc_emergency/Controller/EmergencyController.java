package kjo.care.msvc_emergency.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmergencyController {

    @GetMapping("/emergency")
    public String getEmergency() {
        return "Hello Emergency";
    }

}
