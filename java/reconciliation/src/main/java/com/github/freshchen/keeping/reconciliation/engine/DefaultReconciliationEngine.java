package com.github.freshchen.keeping.reconciliation.engine;

import com.github.freshchen.keeping.reconciliation.accumulator.BillAccumulatorContext;
import com.github.freshchen.keeping.reconciliation.engine.model.ReconciliationConfig;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2022/8/8
 */
@Component
public class DefaultReconciliationEngine implements ReconciliationEngine {

    private BillAccumulatorContext billAccumulatorContext;

    @Override
    public void check(ReconciliationConfig config) {

    }
}
