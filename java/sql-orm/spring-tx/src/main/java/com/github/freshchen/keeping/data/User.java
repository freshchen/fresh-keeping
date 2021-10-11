package com.github.freshchen.keeping.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author darcy
 * @since 2021/1/18
 **/
@Data
@Entity
public class User {

    @Id
    @GeneratedValue
    private Integer id;
    @Column
    private String name;
    @Column
    private String realName;
    @Column
    private String idCard;

}
