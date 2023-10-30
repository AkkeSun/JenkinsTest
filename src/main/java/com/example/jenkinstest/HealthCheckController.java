package com.example.jenkinstest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthCheckController {

    @GetMapping
    public String healthCheck () {
        return "success";
    }
}
