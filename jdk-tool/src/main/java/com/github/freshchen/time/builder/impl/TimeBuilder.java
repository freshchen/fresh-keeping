package com.github.freshchen.time.builder.impl;

import com.github.freshchen.time.builder.IBuilder;

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
        return new Custom();
    }

    @Override
    public Parse parse() {
        return new Parse();
    }

    private TimeBuilder() {
    }

    public class Custom {

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

        public LocalTime build() {
            return LocalTime.of(hour, minute, second);
        }

    }

    public class Parse {

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
            return formatter == null ? LocalTime.parse(timeText) : LocalTime.parse(timeText, formatter);
        }

    }

}
