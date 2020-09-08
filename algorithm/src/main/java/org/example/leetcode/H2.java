package org.example.leetcode;

import org.example.annotation.Array;

import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2020/9/8
 * <p>
 * 给你一个未排序的整数数组，请你找出其中没有出现的最小的正整数。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,0]
 * 输出: 3
 * 示例 2:
 * <p>
 * 输入: [3,4,-1,1]
 * 输出: 2
 * 示例 3:
 * <p>
 * 输入: [7,8,9,11,12]
 * 输出: 1
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/first-missing-positive
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("缺失的第一个正数,给你一个未排序的整数数组，请你找出其中没有出现的最小的正整数。")
public class H2 {

    public int firstMissingPositive(int[] nums) {
        int[] ints = IntStream.of(nums).boxed()
                .filter(i -> i > 0)
                .sorted()
                .distinct()
                .mapToInt(Integer::intValue)
                .toArray();
        if (ints == null || ints.length == 0 || ints[0] > 1) {
            return 1;
        }
        for (int i = 0; i < ints.length - 1; i++) {
            int i1 = ints[i] + 1;
            if (ints[i + 1] != i1) {
                return i1;
            }
        }
        return ints[ints.length - 1] + 1;
    }
}
