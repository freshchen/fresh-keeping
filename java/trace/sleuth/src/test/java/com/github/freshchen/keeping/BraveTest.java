package com.github.freshchen.keeping;

import brave.sampler.CountingSampler;
import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import org.junit.jupiter.api.Test;

/**
 * @author darcy
 * @since 2022/03/13
 **/
public class BraveTest {

    @Test
    public void test1() {
        Sampler sampler = CountingSampler.create(0.1f);
        sampler.isSampled(1231231232L);
    }

    @Test
    public void test2() {
        Sampler sampler = RateLimitingSampler.create(5);
        sampler.isSampled(1231231232L);
    }
}
