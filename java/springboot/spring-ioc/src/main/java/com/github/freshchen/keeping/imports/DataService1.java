package com.github.freshchen.keeping.imports;

/**
 * @author darcy
 * @since 2021/2/5
 **/
public class DataService1 implements DataService {

    @Override
    public void collect() {
        System.out.println("DataService1");
    }

    public DataService1() {
        System.out.println("构造函数 DataService1");
    }
}
