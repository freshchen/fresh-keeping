package org.example.leetcode.m.m1;

import org.example.annotation.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2020/9/8
 * <p>
 * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组。
 * <p>
 * 注意：答案中不可以包含重复的三元组。
 * <p>
 *  
 * <p>
 * 示例：
 * <p>
 * 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 * <p>
 * 满足要求的三元组集合为：
 * [
 * [-1, 0, 1],
 * [-1, -1, 2]
 * ]
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/3sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("三数之和")
public class M7 {

    public List<List<Integer>> threeSum(int[] nums) {
        // TODO
        List<List<Integer>> lists = new ArrayList<>();
        int[] ints = IntStream.of(nums)
                .sorted()
                .toArray();
        for (int i = 0; i < ints.length - 2; i++) {
            int anInt = ints[i];
            int anInt1 = ints[i + 1];
            int anInt2 = ints[i + 2];
            if (anInt + anInt1 + anInt2 == 0) {
                List<Integer> a = new ArrayList<>();
                a.add(anInt);
                a.add(anInt1);
                a.add(anInt2);
                if (!lists.contains(a)) {
                    lists.add(a);
                }
            }
        }
        return lists;
    }
}
