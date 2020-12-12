package com.github.freshchen.keeping.keeping.leetcode.s.s11;

import com.github.freshchen.keeping.keeping.annotation.DC;
import com.github.freshchen.keeping.keeping.annotation.DP;

import java.util.Objects;

/**
 * @author darcy
 * @since 2020/10/12
 * <p>
 * 给定一个整数数组，找出总和最大的连续数列，并返回总和。
 * <p>
 * 示例：
 * <p>
 * 输入： [-2,1,-3,4,-1,2,1,-5,4]
 * 输出： 6
 * 解释： 连续子数组 [4,-1,2,1] 的和最大，为 6。
 * 进阶：
 * <p>
 * 如果你已经实现复杂度为 O(n) 的解法，尝试使用更为精妙的分治法求解。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/contiguous-sequence-lcci
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@DC("连续数列")
@DP("连续数列")
public class S20 {

    public int maxSubArray(int[] nums) {
        Objects.requireNonNull(nums);
        int length = nums.length;
        if (length == 0) {
            return Integer.MIN_VALUE;
        }
        int max = nums[0];
        for (int i = 1; i < length; i++) {
            int cur = nums[i];
            int pre = nums[i - 1];
            if (pre >= 0) {
                nums[i] = cur + pre;
            }
            max = Math.max(max, nums[i]);
        }
        return max;
    }
}
