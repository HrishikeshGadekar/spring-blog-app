package com.spring.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrialController {

    @GetMapping("/trial")
    public String trialControlHandler() {
        return "Trial";
    }
}
