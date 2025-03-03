package com.example.scheduledactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ScheduledActionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledActionsApplication.class, args);
        System.out.println("Scheduled Action Execution Service Started...");
    }
}
