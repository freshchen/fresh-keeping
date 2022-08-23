package com.github.freshchen.keeping.reconciliation.accumulator;

import com.github.freshchen.keeping.reconciliation.accumulator.dao.DefaultSaveBillDAO;
import com.github.freshchen.keeping.reconciliation.accumulator.model.Bill;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author darcy
 * @since 2022/8/8
 */
public abstract class OuterBillAccumulator<OuterBill>
        implements BillAccumulator, ApplicationContextAware {

    private DefaultSaveBillDAO defaultSaveBillDAO;

    @Override
    public List<Bill> load(LocalDateTime begin, LocalDateTime end) {
        OuterBill outerBill = loadOuter(begin, end);
        if (outerBill == null) {
            throw new IllegalArgumentException("未能获取外部账单");
        }
        saveOuterBills(outerBill);
        List<Bill> bills = standardizing(outerBill);
        saveBills(bills);
        return bills;
    }

    abstract OuterBill loadOuter(LocalDateTime begin, LocalDateTime end);

    abstract void saveOuterBills(OuterBill outerBill);

    abstract List<Bill> standardizing(OuterBill outerBill);

    protected void saveBills(List<Bill> bills) {
        if (defaultSaveBillDAO == null) {
            throw new IllegalArgumentException("外部账单存储处理器不存在");
        }
        defaultSaveBillDAO.batchSave(bills);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
