package com.github.freshchen;

import com.github.freshchen.time.builder.TimeFactory;

/**
 * @author : freshchen
 * <P>Created on 2019-11-17 22:52 </p>
 **/
public class Test {

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(1).month(1).day(1).build());
        });
        Thread thread2 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(2).month(2).day(2).build());
        });
        Thread thread3 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(3).month(3).day(3).build());
        });
        Thread thread4 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(4).month(4).day(4).build());
        });
        Thread thread5 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(5).month(5).day(5).build());
        });
        Thread thread6 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(6).month(6).day(6).build());
        });
        Thread thread7 = new Thread(() -> {
            System.out.println(TimeFactory.date().custom().year(7).month(7).day(7).build());
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
    }
}
