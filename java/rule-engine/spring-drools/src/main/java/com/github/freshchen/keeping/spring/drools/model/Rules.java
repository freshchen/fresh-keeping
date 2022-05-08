package com.github.freshchen.keeping.spring.drools.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author darcy
 * @since 2022/05/01
 **/
@Getter
@Setter
@Entity
public class Rules {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(columnDefinition = "text")
    private String drl;

}
