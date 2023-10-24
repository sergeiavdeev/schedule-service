package ru.avdeev.scheduleservice.utils;

import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.exception.InvalidTimeIntervalException;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class DateUtils {

    public static void checkInterval(LocalTime startTime, LocalTime endTime) {

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new InvalidTimeIntervalException("Invalid time interval: %s - %s", startTime, endTime);
        }
    }

    public static void checkIntervals(List<TimeIntervalDto> intervals) {

        intervals.sort(Comparator.comparing(TimeIntervalDto::getStartTime));

        for (int i = 0; i < intervals.size(); i ++) {

            TimeIntervalDto interval = intervals.get(i);
            checkInterval(interval.getStartTime(), interval.getEndTime());

            if (i + 1 < intervals.size()) {
                TimeIntervalDto interval2 = intervals.get(i + 1);
                checkIntervals(interval, interval2);
            }
        }
    }

    private static void checkIntervals(TimeIntervalDto interval1, TimeIntervalDto interval2) {

        if (!interval1.getEndTime().isBefore(interval2.getStartTime())) {
            throw new InvalidTimeIntervalException("Cross time intervals: %s - %s and %s - %s",
                    interval1.getStartTime(), interval1.getEndTime(),
                    interval2.getStartTime(), interval2.getEndTime());
        }
    }
}
