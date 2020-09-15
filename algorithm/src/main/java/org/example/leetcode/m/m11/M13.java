package org.example.leetcode.m.m11;

import org.example.annotation.Array;

/**
 * @author darcy
 * @since 2020/9/15
 * <p>
 * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。
 * <p>
 * 你的算法时间复杂度必须是 O(log n) 级别。
 * <p>
 * 如果数组中不存在目标值，返回 [-1, -1]。
 * <p>
 * 示例 1:
 * <p>
 * 输入: nums = [5,7,7,8,8,10], target = 8
 * 输出: [3,4]
 * 示例 2:
 * <p>
 * 输入: nums = [5,7,7,8,8,10], target = 6
 * 输出: [-1,-1]
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/find-first-and-last-position-of-element-in-sorted-array
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("在排序数组中查找元素的第一个和最后一个位置")
public class M13 {

    public int[] searchRange(int[] nums, int target) {
        return new int[]{getFirst(nums, target), getLast(nums, target)};
    }

    private int getFirst(int[] nums, int target) {
        int left = 0;
        int right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            int cur = nums[mid];
            if (cur == target) {
                right = mid;
            } else if (cur < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        if (left == nums.length) {
            return -1;
        }
        return nums[left] == target ? left : -1;
    }

    private int getLast(int[] nums, int target) {
        int left = 0;
        int right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            int cur = nums[mid];
            if (cur == target) {
                left = mid + 1;
            } else if (cur < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        if (left == 0) {
            return -1;
        }
        return nums[left - 1] == target ? (left - 1) : -1;
    }
}
