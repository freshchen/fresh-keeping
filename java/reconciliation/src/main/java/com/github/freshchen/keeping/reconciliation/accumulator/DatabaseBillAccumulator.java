package com.github.freshchen.keeping.reconciliation.accumulator;

import com.github.freshchen.keeping.reconciliation.accumulator.model.Bill;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author darcy
 * @since 2022/8/8
 */
public abstract class DatabaseBillAccumulator<DatabaseBill> implements BillAccumulator {

    @Override
    public List<Bill> load(LocalDateTime begin, LocalDateTime end) {
        List<DatabaseBill> databaseBills = batchFind(begin, end);
        return standardizing(databaseBills);
    }

    abstract List<DatabaseBill> batchFind(LocalDateTime begin, LocalDateTime end);

    abstract List<Bill> standardizing(List<DatabaseBill> databaseBills);


}
