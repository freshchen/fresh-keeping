package com.github.freshchen.time;

import com.github.freshchen.time.builder.impl.DateTimeBuilder;
import com.github.freshchen.time.builder.impl.DateBuilder;
import com.github.freshchen.time.builder.impl.TimeBuilder;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 22:07 </p>
 **/
public class TimeFactory {

    public static DateBuilder date(){
        return DateBuilder.INSTANCE;
    }

    public static TimeBuilder time(){
        return TimeBuilder.INSTANCE;
    }

    public static DateTimeBuilder dateTime(){
        return DateTimeBuilder.INSTANCE;
    }

    private TimeFactory() {
    }
}
