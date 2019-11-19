package com.github.freshchen.time.temporal;

import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 22:04 </p>
 **/
public class ExtAdjusters {

    public static TemporalAdjuster nextWorkingDay = temporal -> {
        DayOfWeek day = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
        int dayToAdd = 1;
        if (day == DayOfWeek.FRIDAY) dayToAdd = 3;
        if (day == DayOfWeek.SATURDAY) dayToAdd = 2;
        return temporal.plus(dayToAdd, ChronoUnit.DAYS);
    };


}
