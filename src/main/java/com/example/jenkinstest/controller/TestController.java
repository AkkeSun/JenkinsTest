package com.example.jenkinstest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/info")
    public String infoLog() throws InterruptedException {
        Thread.sleep(70000);
        log.info("info");
        return "info";
    }

    @GetMapping("/error")
    public String errorLog() {
        log.error("error");
        return "error";
    }
}
