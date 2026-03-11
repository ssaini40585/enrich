package com.enrichplus.demand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.enrichplus")
public class DemandServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemandServiceApplication.class, args);
    }
}
