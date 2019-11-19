package com.github.freshchen.time.builder.impl;

import com.github.freshchen.time.builder.IBuilder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 23:18 </p>
 **/
public enum DateBuilder implements IBuilder {
    INSTANCE;

    @Override
    public LocalDate now() {
        return LocalDate.now();
    }

    @Override
    public Custom custom() {
        return Custom.INSTANCE;
    }

    @Override
    public Parse parse() {
        return Parse.INSTANCE;
    }

    private DateBuilder() {
    }

    public enum Custom {
        INSTANCE;

        private int year;
        private short month;
        private short day;

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

        private void initialize() {
            this.year = 0;
            this.month = 0;
            this.day = 0;
        }

        public LocalDate build() {
            LocalDate date = LocalDate.of(year, month, day);
            initialize();
            return date;
        }

        private Custom() {
        }
    }

    public enum Parse {
        INSTANCE;

        private DateTimeFormatter formatter;
        private String dateText;

        public Parse formatter(String formatter) {
            this.formatter = DateTimeFormatter.ofPattern(formatter);
            return this;
        }

        public Parse dateText(String dateText) {
            this.dateText = dateText;
            return this;
        }

        public LocalDate build() {
            LocalDate date = formatter == null ? LocalDate.parse(dateText) : LocalDate.parse(dateText, formatter);
            initialize();
            return date;
        }

        private void initialize() {
            this.formatter = null;
            this.dateText = null;
        }

        private Parse() {
        }
    }

}
