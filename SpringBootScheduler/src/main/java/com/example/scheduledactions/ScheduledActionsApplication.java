package com.example.scheduledactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ScheduledActionsApplication {
    private static final Logger log = LoggerFactory.getLogger(ScheduledActionsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ScheduledActionsApplication.class, args);
        System.out.println("Scheduled Action Execution Service Started...");
        log.info("Scheduled Action Execution Service Started...");
    }
}
