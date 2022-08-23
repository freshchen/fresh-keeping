package com.github.freshchen.keeping.reconciliation.engine;

import com.github.freshchen.keeping.reconciliation.accumulator.BillAccumulator;
import com.github.freshchen.keeping.reconciliation.engine.model.ReconciliationConfig;

/**
 * @author darcy
 * @since 2022/8/8
 */
public interface ReconciliationEngine {

    void check(ReconciliationConfig config);
}
