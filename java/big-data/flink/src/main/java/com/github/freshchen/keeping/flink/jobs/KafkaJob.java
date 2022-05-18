package com.github.freshchen.keeping.flink.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2022/05/08
 **/
@Component
@Slf4j
public class KafkaJob extends BaseJob {
    @Override
    protected void config(StreamExecutionEnvironment env) {
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("kafka-single:9092")
                .setTopics("test")
                .setGroupId("flink-job-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> kafkaSource = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        kafkaSource.print();
    }

}
