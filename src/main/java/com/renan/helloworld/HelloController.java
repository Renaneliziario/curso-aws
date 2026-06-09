package com.renan.helloworld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello World - AWS EC2!";
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "up");
    }

}
