package com.github.freshchen.keeping.keeping.leetcode.s.s11;

import com.github.freshchen.keeping.keeping.annotation.Array;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2020/9/10
 * <p>
 * 给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。
 * <p>
 * 说明：
 * <p>
 * 你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？
 * <p>
 * 示例 1:
 * <p>
 * 输入: [2,2,1]
 * 输出: 1
 * 示例 2:
 * <p>
 * 输入: [4,1,2,1,2]
 * 输出: 4
 * <p>
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/single-number
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("只出现一次的数字")
public class S11 {

    public int singleNumber(int[] nums) {
        return Optional.ofNullable(nums)
                .map(IntStream::of)
                .orElse(IntStream.empty())
                .boxed()
                .collect(Collectors.toMap(Function.identity(), v -> 1, (o, n) -> o + 1))
                .entrySet().stream()
                .filter(e -> e.getValue().equals(1))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }
}
