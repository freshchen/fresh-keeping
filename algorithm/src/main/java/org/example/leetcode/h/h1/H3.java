package org.example.leetcode.h.h1;

import org.example.annotation.Array;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author darcy
 * @since 2020/9/16
 * <p>
 * 给定一个整数数组 nums，按要求返回一个新数组 counts。数组 counts 有该性质： counts[i] 的值是  nums[i] 右侧小于 nums[i] 的元素的数量。
 * <p>
 *  
 * <p>
 * 示例：
 * <p>
 * 输入：nums = [5,2,6,1]
 * 输出：[2,1,1,0]
 * 解释：
 * 5 的右侧有 2 个更小的元素 (2 和 1)
 * 2 的右侧仅有 1 个更小的元素 (1)
 * 6 的右侧有 1 个更小的元素 (1)
 * 1 的右侧有 0 个更小的元素
 *  
 * <p>
 * 提示：
 * <p>
 * 0 <= nums.length <= 10^5
 * -10^4 <= nums[i] <= 10^4
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/count-of-smaller-numbers-after-self
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("计算右侧小于当前元素的个数")
public class H3 {

    public List<Integer> countSmaller(int[] nums) {
        // TODO
        int[] clone = nums.clone();
        Arrays.sort(clone);
        int length = nums.length;
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            int cur = nums[i];
            int first = getFirst(clone, cur);
            list.add(first);
            clone = delete(clone, first);
        }
        return list;
    }

    private int[] delete(int[] nums, int index) {
        int[] clone = new int[nums.length - 1];
        int ci = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i == index) {
                continue;
            }
            clone[ci++] = nums[i];
        }
        return clone;
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
            return 0;
        }
        return nums[left] == target ? left : 0;
    }
}
