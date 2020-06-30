package util;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class StreamFunctionsTest {

    @Test
    void toRepeatList() {
        List<Integer> collect = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 3, 5).collect(StreamFunctions.toRepeatList());
        collect.sort(Comparator.naturalOrder());
        assertArrayEquals(new Integer[]{1, 3, 5}, collect.toArray());
    }

    @Test
    void toRepeatList1() {
        List<String> collect = Stream.of("a", "b", "c", "d", "a", "c").collect(StreamFunctions.toRepeatList());
        collect.sort(Comparator.naturalOrder());
        assertArrayEquals(new String[]{"a", "c"}, collect.toArray());
    }

    @Test
    void intoList() {
        List<Integer> integers = Lists.newArrayList(1, 2, 3);
        Stream.of(4, 5, 6).forEach(StreamFunctions.intoList(integers));
        integers.sort(Comparator.naturalOrder());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, integers.toArray());
    }
}