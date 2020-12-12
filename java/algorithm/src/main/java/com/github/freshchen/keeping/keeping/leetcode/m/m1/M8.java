package com.github.freshchen.keeping.keeping.leetcode.m.m1;

import com.github.freshchen.keeping.keeping.annotation.Array;

/**
 * @author darcy
 * @since 2020/9/9
 * 给定一个包含红色、白色和蓝色，一共 n 个元素的数组，原地对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。
 * <p>
 * 此题中，我们使用整数 0、 1 和 2 分别表示红色、白色和蓝色。
 * <p>
 * 注意:
 * 不能使用代码库中的排序函数来解决这道题。
 * <p>
 * 示例:
 * <p>
 * 输入: [2,0,2,1,1,0]
 * 输出: [0,0,1,1,2,2]
 * 进阶：
 * <p>
 * 一个直观的解决方案是使用计数排序的两趟扫描算法。
 * 首先，迭代计算出0、1 和 2 元素的个数，然后按照0、1、2的排序，重写当前数组。
 * 你能想出一个仅使用常数空间的一趟扫描算法吗？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sort-colors
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("颜色分类")
public class M8 {

    public void sortColors(int[] nums) {
        int length = nums.length;
        int partitionNum = 1;
        int less = -1;
        int more = length;
        int index = 0;
        while (index < more) {
            int num = nums[index];
            if (num < partitionNum) {
                swap(nums, ++less, index++);
            } else if (num > partitionNum) {
                swap(nums, index, --more);
            } else {
                index++;
            }
        }
    }

    private void swap(int[] nums, int from, int to) {
        int t = nums[from];
        nums[from] = nums[to];
        nums[to] = t;
    }
}
