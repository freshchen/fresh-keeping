package com.github.freshchen.keeping.reconciliation.accumulator;

import com.github.freshchen.keeping.reconciliation.accumulator.model.BillAccumulatorType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author darcy
 * @since 2022/8/8
 */
@Component
public class BillAccumulatorContext implements ApplicationContextAware {

    private Map<BillAccumulatorType, BillAccumulator> billAccumulatorMap;

    /**
     * 根据时间区间加载账单
     */
    public BillAccumulator matchBillAccumulator(BillAccumulatorType type) {
        return billAccumulatorMap.get(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
