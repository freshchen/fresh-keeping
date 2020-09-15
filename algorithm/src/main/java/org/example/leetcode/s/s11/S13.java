package org.example.leetcode.s.s11;

import org.example.annotation.Array;

/**
 * @author darcy
 * @since 2020/9/15
 * <p>
 * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,3,12]
 * 输出: [1,3,12,0,0]
 * 说明:
 * <p>
 * 必须在原数组上操作，不能拷贝额外的数组。
 * 尽量减少操作次数。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/move-zeroes
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("移动零")
public class S13 {

    public void moveZeroes(int[] nums) {
        int index = 0;
        int notZeroBegin = 0;
        while (index < nums.length - 1) {
            int cur = nums[index++];
            if (cur == 0) {
                if (nums[index] != 0) {
                    swap(nums, index, notZeroBegin);
                    notZeroBegin++;
                }
            } else {
                notZeroBegin++;
            }
        }
        if (nums[nums.length - 1] != 0) {
            swap(nums, nums.length - 1, notZeroBegin);
        }
    }

    private void swap(int[] s, int from, int to) {
        int temp = s[from];
        s[from] = s[to];
        s[to] = temp;
    }
}
