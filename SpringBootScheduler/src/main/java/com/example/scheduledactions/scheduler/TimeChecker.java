package com.example.scheduledactions.scheduler;

import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.ZoneId;

@Component
public class TimeChecker {
    public static final ZoneId LAGOS_ZONE = ZoneId.of("Africa/Lagos");

    public boolean shouldExecuteAction(DayOfWeek day, int bitmask) {
        return (bitmask & (1 << (day.getValue() - 1))) != 0;
    }
}
