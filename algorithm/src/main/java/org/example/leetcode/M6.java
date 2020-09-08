package org.example.leetcode;

import org.example.annotation.Array;

/**
 * @author darcy
 * @since 2020/9/8
 * <p>
 * 给你一个整数数组 nums ，请你找出数组中乘积最大的连续子数组（该子数组中至少包含一个数字），并返回该子数组所对应的乘积。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 输入: [2,3,-2,4]
 * 输出: 6
 * 解释: 子数组 [2,3] 有最大乘积 6。
 * 示例 2:
 * <p>
 * 输入: [-2,0,-1]
 * 输出: 0
 * 解释: 结果不能为 2, 因为 [-2,-1] 不是子数组。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/maximum-product-subarray
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("乘积最大子数组")
public class M6 {

    public int maxProduct(int[] nums) {
        // TODO
        int max = Math.max(nums[0], nums[nums.length - 1]);
        int curr = nums[0];
        for (int i = 0; i < nums.length - 1; i++) {
            int n = nums[i];
            int n1 = nums[i + 1];
            int m = n * n1;
            if (m < curr) {

            }
        }
        return 1;
    }
}
