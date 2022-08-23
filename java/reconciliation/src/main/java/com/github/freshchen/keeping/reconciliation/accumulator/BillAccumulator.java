package com.github.freshchen.keeping.reconciliation.accumulator;

import com.github.freshchen.keeping.reconciliation.accumulator.model.Bill;
import com.github.freshchen.keeping.reconciliation.accumulator.model.BillAccumulatorType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author darcy
 * @since 2022/8/8
 */
public interface BillAccumulator {

    BillAccumulatorType type();

    /**
     * 根据时间区间加载账单
     */
    List<Bill> load(LocalDateTime begin, LocalDateTime end);
}
