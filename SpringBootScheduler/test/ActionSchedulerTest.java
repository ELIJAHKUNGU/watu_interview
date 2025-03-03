package com.example.scheduledactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionSchedulerTest {

    @Mock
    private CsvReader csvReader;

    @Mock
    private TimeChecker timeChecker;

    @InjectMocks
    private ActionService actionService;

    @BeforeEach
    void setup() {
        // Mock CSV data: "12:00,1" → Executes on Monday at 12:00
        when(csvReader.readCsv()).thenReturn(List.of(new String[]{"12:00", "1"}));
    }

    @Test
    void testProcessActions_executesWhenConditionsMet() {
        LocalTime testTime = LocalTime.of(12, 0);
        DayOfWeek testDay = DayOfWeek.MONDAY;

        // Mock bitmask logic to return true on Monday
        when(timeChecker.shouldExecuteAction(testDay, 1)).thenReturn(true);

        // Run the action processing
        actionService.processActions();

        // Verify the method processes at least one action
        verify(csvReader, times(1)).readCsv();
        verify(timeChecker, times(1)).shouldExecuteAction(eq(testDay), eq(1));
    }

    @Test
    void testProcessActions_doesNotExecuteOnWrongDay() {
        LocalTime testTime = LocalTime.of(12, 0);
        DayOfWeek testDay = DayOfWeek.TUESDAY;

        // Mock bitmask logic to return false on Tuesday
        when(timeChecker.shouldExecuteAction(testDay, 1)).thenReturn(false);

        // Run the action processing
        actionService.processActions();

        // Ensure the action is NOT executed
        verify(timeChecker, times(1)).shouldExecuteAction(eq(testDay), eq(1));
    }
}
