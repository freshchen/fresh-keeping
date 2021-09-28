package com.github.freshchen.keeping.imports;

/**
 * @author darcy
 * @since 2021/2/5
 **/
public class DataService2 implements DataService {

    @Override
    public void collect() {
        System.out.println("DataService2");
    }

    public DataService2() {
        System.out.println("构造函数 DataService2");
    }
}
