package kjo.care.msvc_notification.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
public class NotificationController {

    @GetMapping("/hello")
    private String sayHello() {
        return "Hello World!";
    }
}
