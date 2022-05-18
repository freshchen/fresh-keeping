package com.github.freshchen.keeping.flink.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2022/05/08
 **/
@Component
@Slf4j
public class SequenceJob extends BaseJob {
    @Override
    protected void config(StreamExecutionEnvironment env) {
        DataStreamSource<Long> source = env.fromSequence(1, 10);
        source.print();
    }

}
