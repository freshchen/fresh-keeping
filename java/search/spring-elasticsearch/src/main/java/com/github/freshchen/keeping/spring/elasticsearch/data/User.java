package com.github.freshchen.keeping.spring.elasticsearch.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author darcy
 * @since 2022/05/21
 **/
@Data
@Document(indexName = "user", createIndex = true)
@Setting(replicas = 1)
public class User {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Date)
    private Long ts;

    @Field(type = FieldType.Integer)
    private Integer age;
}
