package com.github.freshchen.time;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 22:07 </p>
 **/
public class TimeBuilder implements Serializable {

    private static final long serialVersionUID = -6973892093775865183L;

    private DateTimeFormatter formatter;
    private int year;
    private int month;
    private int day;

    public static TimeBuilder date(){
        return new TimeBuilder();
    }

    public LocalDate nowDate() {
        return LocalDate.now();
    }

    public LocalDate buildDate() {
        return LocalDate.of(year, month, day);
    }

    public TimeBuilder format(String format) {
        formatter = DateTimeFormatter.ofPattern(format);
        return this;
    }

    public TimeBuilder year(int year) {
        this.year = year;
        return this;
    }

    public TimeBuilder month(int month) {
        this.month = month;
        return this;
    }

    public TimeBuilder day(int day) {
        this.day = day;
        return this;
    }

    private TimeBuilder() {
    }
}
