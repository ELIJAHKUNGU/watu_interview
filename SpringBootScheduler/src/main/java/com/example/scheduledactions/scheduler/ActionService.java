package com.example.scheduledactions.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class ActionService {
    private static final Logger log = LoggerFactory.getLogger(ActionService.class);
    private final CsvReader csvReader;
    private final TimeChecker timeChecker;

    public ActionService(CsvReader csvReader, TimeChecker timeChecker) {
        this.csvReader = csvReader;
        this.timeChecker = timeChecker;
    }

    public void processActions() {
        try {
            LocalDateTime now = LocalDateTime.now(TimeChecker.LAGOS_ZONE);
            DayOfWeek today = now.getDayOfWeek();
            LocalTime currentTime = now.toLocalTime();

            List<String[]> actions = csvReader.readCsv();
            for (String[] action : actions) {
                LocalTime actionTime = LocalTime.parse(action[0].trim());
                int bitmask = Integer.parseInt(action[1].trim());
                if (currentTime.equals(actionTime) && timeChecker.shouldExecuteAction(today, bitmask)) {
                    System.out.println("Executing action at " + action[0] + " on " + today);
                    log.info("Executing action at " + action[0] + " on " + today);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());

        }

    }
}
