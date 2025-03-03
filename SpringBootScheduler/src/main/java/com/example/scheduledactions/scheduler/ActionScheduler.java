package com.example.scheduledactions.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
class ActionScheduler {
    private final ActionService actionService;

    public ActionScheduler(ActionService actionService) {
        this.actionService = actionService;
    }

    @Scheduled(cron = "0 * * * * *")  // Runs every minute
    public void executeScheduledActions() {
        System.out.println("Scheduler running at " + LocalDateTime.now(ZoneId.of("Africa/Lagos")));
        actionService.processActions();
    }
}

