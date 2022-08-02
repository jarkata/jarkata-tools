package cn.jarkata.tools.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public class WeekUtils {

    public static LocalDate getLastDayOfWeek(LocalDate localDate) {
        Objects.requireNonNull(localDate, "local date is null");
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        int value = dayOfWeek.getValue();
        return localDate.plusDays(7 - value);
    }

    public static LocalDate getFirstDayOfWeek(LocalDate localDate) {
        Objects.requireNonNull(localDate, "local date is null");
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        int value = dayOfWeek.getValue();
        return localDate.minusDays(value - 1);
    }
}
