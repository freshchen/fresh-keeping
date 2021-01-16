package com.github.freshchen.keeping.simplefactory;

/**
 * @program: fresh-design-pattern
 * @Date: 2019/5/16 0:09
 * @Author: Ling Chen
 * @Description:
 */
public class OptAdd extends Operation {

    public double getResult() {
        return super.getNumberOne() + super.getNumberTwo();
    }
}
