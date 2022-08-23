package com.github.freshchen.keeping.reconciliation.engine.model;

import com.github.freshchen.keeping.reconciliation.accumulator.model.BillAccumulatorType;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2022/8/8
 */
@Builder
public class ReconciliationConfig {

    BillAccumulatorType source;

    BillAccumulatorType target;

    LocalDateTime from;

    LocalDateTime to;

}
