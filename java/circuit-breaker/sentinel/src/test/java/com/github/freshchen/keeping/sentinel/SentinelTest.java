package com.github.freshchen.keeping.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author darcy
 * @since 2022/8/23
 */
class SentinelTest {

    @Test
    public void test() {
        String resource = "user-add";
        FlowRule flowRule = new FlowRule();
        flowRule.setCount(5);
        flowRule.setResource(resource);
        FlowRuleManager.loadRules(Lists.newArrayList(flowRule));

        for (int i = 0; i < 10; i++) {
            try (Entry entry = SphU.entry(resource)) {
                // 被保护的业务逻辑
                // do something here...
                System.out.println(i);
            } catch (BlockException ex) {
                // 资源访问阻止，被限流或被降级
                // 在此处进行相应的处理操作
                System.out.println(i + ex.toString());
            }
        }
    }

}