package com.example.smartmarketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Smart Marketing POC backend.
 * This Boot application exposes a WebSocket endpoint that the React frontend can connect to.
 */
@SpringBootApplication
public class SmartMarketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMarketingApplication.class, args);
    }
}


