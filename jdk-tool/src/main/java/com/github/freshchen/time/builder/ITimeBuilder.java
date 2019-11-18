package com.github.freshchen.time.builder;

import java.time.temporal.Temporal;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 23:58 </p>
 **/
public interface ITimeBuilder {

    Temporal now();

    Temporal build();

}
