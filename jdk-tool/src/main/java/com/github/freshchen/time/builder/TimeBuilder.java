package com.github.freshchen.time.builder;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 22:07 </p>
 **/
public class TimeBuilder {

    public static DateBuilder date(){
        return new DateBuilder();
    }

    private TimeBuilder() {
    }
}
