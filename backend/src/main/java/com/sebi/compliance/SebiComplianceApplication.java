package com.sebi.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SebiComplianceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SebiComplianceApplication.class, args);
    }
}
