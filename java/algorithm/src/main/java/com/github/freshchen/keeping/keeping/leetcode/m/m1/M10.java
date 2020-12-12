package com.github.freshchen.keeping.keeping.leetcode.m.m1;

import com.github.freshchen.keeping.keeping.annotation.Array;
import com.github.freshchen.keeping.keeping.annotation.DP;

import java.util.Objects;

/**
 * @author darcy
 * @since 2020/9/10
 * <p>
 * 给定一个无序的整数数组，找到其中最长上升子序列的长度。
 * <p>
 * 示例:
 * <p>
 * 输入: [10,9,2,5,3,7,101,18]
 * 输出: 4
 * 解释: 最长的上升子序列是 [2,3,7,101]，它的长度是 4。
 * 说明:
 * <p>
 * 可能会有多种最长上升子序列的组合，你只需要输出对应的长度即可。
 * 你算法的时间复杂度应该为 O(n2) 。
 * 进阶: 你能将算法的时间复杂度降低到 O(n log n) 吗?
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-increasing-subsequence
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("最长上升子序列")
@DP("最长上升子序列")
public class M10 {

    public int lengthOfLIS(int[] nums) {
        if (Objects.isNull(nums)) {
            return 0;
        }
        int length = nums.length;
        if (length <= 1) {
            return length;
        }
        int[] dp = new int[length];
        dp[0] = 1;
        int max = 1;
        for (int i = 1; i < length; i++) {
            int maxIn = 0;
            int cur = nums[i];
            for (int j = 0; j < i; j++) {
                if (cur > nums[j]) {
                    maxIn = Math.max(maxIn, dp[j]);
                }
            }
            dp[i] = maxIn + 1;
            max = Math.max(max, dp[i]);
        }
        return max;
    }
}
