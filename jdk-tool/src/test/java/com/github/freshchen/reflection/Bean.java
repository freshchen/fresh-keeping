package com.github.freshchen.reflection;

import java.util.List;

/**
 * @author : freshchen
 * <P>Created on 2019-11-23 22:24 </p>
 **/
public class Bean {

    private String name;
    private int age;
    private List<String> hobbies;
    private String[] schools;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAge(int age, String name) {
        this.age = age;
    }

    public List <String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List <String> hobbies) {
        this.hobbies = hobbies;
    }

    public String[] getSchools() {
        return schools;
    }

    public void setSchools(String[] schools) {
        this.schools = schools;
    }
}
