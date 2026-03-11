package com.enrichplus.allocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.enrichplus")
public class AllocationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllocationServiceApplication.class, args);
    }
}
