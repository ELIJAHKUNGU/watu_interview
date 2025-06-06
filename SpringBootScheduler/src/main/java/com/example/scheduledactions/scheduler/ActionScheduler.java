package com.example.scheduledactions.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
class ActionScheduler {
    private static final Logger log = LoggerFactory.getLogger(ActionScheduler.class);
    private final ActionService actionService;

    public ActionScheduler(ActionService actionService) {
        this.actionService = actionService;
    }

    @Scheduled(cron = "0 * * * * *")  // Runs every minute
    public void executeScheduledActions() {
        try {
            log.info("Executing scheduled actions");
            System.out.println("Scheduler running at " + LocalDateTime.now(ZoneId.of("Africa/Lagos")));
            actionService.processActions();

        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}

