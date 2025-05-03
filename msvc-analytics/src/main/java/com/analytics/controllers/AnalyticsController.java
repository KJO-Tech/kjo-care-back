package com.analytics.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@Validated
public class AnalyticsController {
    @GetMapping("/test")
    public String test() {
        return "Analytics service is up and running!";
    }
}
