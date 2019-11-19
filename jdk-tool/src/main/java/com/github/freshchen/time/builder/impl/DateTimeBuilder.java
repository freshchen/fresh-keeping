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
        return Custom.INSTANCE;
    }

    @Override
    public Parse parse() {
        return Parse.INSTANCE;
    }

    private DateTimeBuilder() {
    }

    public enum Custom {
        INSTANCE;

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

        private void initialize() {
            this.year = 0;
            this.month = 0;
            this.day = 0;
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
        }

        public LocalDateTime build() {
            LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            initialize();
            return dateTime;
        }

        private Custom() {
        }
    }

    public enum Parse {
        INSTANCE;

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
            LocalDateTime dateTime = formatter == null ? LocalDateTime.parse(dateTimeText) : LocalDateTime.parse(dateTimeText, formatter);
            initialize();
            return dateTime;
        }

        private void initialize() {
            this.formatter = null;
            this.dateTimeText = null;
        }

        private Parse() {
        }
    }

}
