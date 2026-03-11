package com.enrichplus.staffing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.enrichplus")
public class StaffingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StaffingServiceApplication.class, args);
    }
}
