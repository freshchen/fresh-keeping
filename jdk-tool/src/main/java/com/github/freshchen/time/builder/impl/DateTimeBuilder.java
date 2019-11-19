package com.github.freshchen.time.builder.impl;

import com.github.freshchen.time.builder.IBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 23:18 </p>
 **/
public enum DateTimeBuilder implements IBuilder {
    INSTANCE;

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public Custom custom() {
        return new Custom();
    }

    @Override
    public Parse parse() {
        return new Parse();
    }

    private DateTimeBuilder() {
    }

    public class Custom {

        private int year;
        private short month;
        private short day;
        private int hour;
        private int minute;
        private int second;

        public Custom year(int year) {
            this.year = year;
            return this;
        }

        public Custom month(int month) {
            this.month = (short) month;
            return this;
        }

        public Custom day(int day) {
            this.day = (short) day;
            return this;
        }

        public Custom hour(int hour) {
            this.hour = hour;
            return this;
        }

        public Custom minute(int minute) {
            this.minute = minute;
            return this;
        }

        public Custom second(int second) {
            this.second = second;
            return this;
        }

        public LocalDateTime build() {
            return LocalDateTime.of(year, month, day, hour, minute, second);
        }

    }

    public class Parse {

        private DateTimeFormatter formatter;
        private String dateTimeText;

        public Parse formatter(String formatter) {
            this.formatter = DateTimeFormatter.ofPattern(formatter);
            return this;
        }

        public Parse dateTimeText(String dateTimeText) {
            this.dateTimeText = dateTimeText;
            return this;
        }

        public LocalDateTime build() {
            return formatter == null ? LocalDateTime.parse(dateTimeText) : LocalDateTime.parse(dateTimeText, formatter);
        }
    }

}
