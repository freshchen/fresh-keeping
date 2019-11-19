package com.github.freshchen.time.builder.impl;

import com.github.freshchen.time.builder.IBuilder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 23:18 </p>
 **/
public enum TimeBuilder implements IBuilder {
    INSTANCE;

    @Override
    public LocalTime now() {
        return LocalTime.now();
    }

    @Override
    public Custom custom() {
        return Custom.INSTANCE;
    }

    @Override
    public Parse parse() {
        return Parse.INSTANCE;
    }

    private TimeBuilder() {
    }

    public enum Custom {
        INSTANCE;

        private int hour;
        private int minute;
        private int second;

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
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
        }

        public LocalTime build() {
            LocalTime time = LocalTime.of(hour, minute, second);
            initialize();
            return time;
        }

        private Custom() {
        }
    }

    public enum Parse {
        INSTANCE;

        private DateTimeFormatter formatter;
        private String timeText;

        public Parse formatter(String formatter) {
            this.formatter = DateTimeFormatter.ofPattern(formatter);
            return this;
        }

        public Parse timeText(String timeText) {
            this.timeText = timeText;
            return this;
        }

        public LocalTime build() {
            LocalTime time = formatter == null ? LocalTime.parse(timeText) : LocalTime.parse(timeText, formatter);
            initialize();
            return time;
        }

        private void initialize() {
            this.formatter = null;
            this.timeText = null;
        }

        private Parse() {
        }
    }

}
