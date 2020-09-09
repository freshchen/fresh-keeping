package org.example.leetcode.s.s10;

import org.example.annotation.Array;

/**
 * @author darcy
 * @since 2020/08/30
 * <p>
 * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
 * <p>
 *  
 * <p>
 * 示例:
 * <p>
 * 给定 nums = [2, 7, 11, 15], target = 9
 * <p>
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/two-sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("两数之和")
public class S4 {

    public int[] twoSum(int[] nums, int target) {
        int[] clone = nums.clone();
        for (int i = 0; i < clone.length - 1; i++) {
            int v1 = clone[i];
            for (int j = i + 1; j < clone.length; j++) {
                int v2 = clone[j];
                if (v1 + v2 == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
