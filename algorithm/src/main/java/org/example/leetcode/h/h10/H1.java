package org.example.leetcode.h.h10;

import org.example.annotation.Array;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2020/9/7
 * <p>
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 * <p>
 * <p>
 * <p>
 * 上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。 感谢 Marcos 贡献此图。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出: 6
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/trapping-rain-water
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("接雨水,给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。")
public class H1 {

    public int trap(int[] height) {
        // TODO
        int length = height.length;
        Map<Integer, Integer> iMap = new HashMap<>(length);
        Map<Integer, List<Integer>> vMap = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            int v = height[i];
            iMap.put(i, v);
            List<Integer> indexes = vMap.get(v);
            if (Objects.isNull(indexes)) {
                indexes = new ArrayList<>(length);
            }
            indexes.add(i);
            indexes.sort(Comparator.naturalOrder());
            vMap.put(v, indexes);
        }
        int sum = vMap.entrySet().stream().mapToInt((entry) -> {
            int v = entry.getKey();
            int[] indexes = entry.getValue().stream().mapToInt(Integer::intValue).toArray();
            int min = -1;
            for (int i = 0; i < indexes.length; i++) {
                int index = indexes[i];
                if (index == 0) {
                    continue;
                }
                boolean b = IntStream.range(0, index).anyMatch(ii -> iMap.get(ii) > v);
                if (b) {
                    min = i;
                    break;
                }
            }
            int max = -1;
            for (int i = indexes.length - 1; i >= 0; i--) {
                int index = indexes[i];
                if (index == length - 1) {
                    continue;
                }
                boolean b = IntStream.range(index, length).anyMatch(ii -> iMap.get(ii) > v);
                if (b) {
                    max = i;
                    break;
                }
            }
            if (min == -1 || max == -1 || min > max) {
                return 0;
            }
            int indexMax = indexes[max];
            int indexMin = indexes[min];
            if (indexMax - indexMin <= 1) {
                return indexMax - indexMin + 1;
            }
            int count = (int) IntStream.range(indexMin + 1, indexMax).filter(i -> iMap.get(i) > v).count();
            return indexMax - indexMin + 1 - count;
        }).sum();
        return sum;
    }
}
