package org.example.leetcode.m.m10;

import org.example.annotation.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author darcy
 * @since 2020/08/24
 * <p>
 * 打乱一个没有重复元素的数组。
 * <p>
 *  
 * <p>
 * 示例:
 * <p>
 * // 以数字集合 1, 2 和 3 初始化数组。
 * int[] nums = {1,2,3};
 * Solution solution = new Solution(nums);
 * <p>
 * // 打乱数组 [1,2,3] 并返回结果。任何 [1,2,3]的排列返回的概率应该相同。
 * solution.shuffle();
 * <p>
 * // 重设数组到它的初始状态[1,2,3]。
 * solution.reset();
 * <p>
 * // 随机返回数组[1,2,3]打乱后的结果。
 * solution.shuffle();
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/shuffle-an-array
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("打乱数组,打乱一个没有重复元素的数组。")
public class M2 {

    private int[] nums;
    private Random random = new Random();

    public M2(int[] nums) {
        this.nums = nums;
    }

    /**
     * Resets the array to its original configuration and return it.
     */
    public int[] reset() {
        return this.nums;
    }

    /**
     * Returns a random shuffling of the array.
     */
    public int[] shuffle() {
        int length = nums.length;
        List<Integer> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(nums[i]);
        }
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(list.size());
            result[i] = list.get(index);
            list.remove(index);
        }
        return result;
    }
}
