package com.github.freshchen.keeping.common.lib.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author darcy
 * @since 2020/06/30
 **/
public class StreamFunctions {

    /**
     * 找出重复元素
     *
     * @param <T>
     * @return 重复元素 List 集合
     */
    public static <T> Collector<T, Pair<List<T>, List<T>>, List<T>> toRepeatList() {
        Supplier<Pair<List<T>, List<T>>> supplier = () -> Pair.of(new ArrayList<T>(), new ArrayList<T>());
        BiConsumer<Pair<List<T>, List<T>>, T> accumulator = (pair, e) -> {
            List<T> all = pair.getLeft();
            if (all.contains(e)) {
                List<T> repeat = pair.getRight();
                if (!repeat.contains(e)) {
                    repeat.add(e);
                }
            } else {
                all.add(e);
            }
        };
        BinaryOperator<Pair<List<T>, List<T>>> combiner = (pair1, pair2) -> {
            throw new UnsupportedOperationException("不支持并行流");
        };
        return Collector.of(supplier, accumulator, combiner, Pair::getRight);
    }

    /**
     * 存到其他集合
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Consumer<T> intoList(List<T> list) {
        return list::add;
    }
}
