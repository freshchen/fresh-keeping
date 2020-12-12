package com.github.freshchen.keeping.keeping.leetcode.s.s11;

import com.github.freshchen.keeping.keeping.annotation.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * @author darcy
 * @since 2020/10/2
 * <p>
 * 给定一个整数数组，判断是否存在重复元素。
 * <p>
 * 如果任意一值在数组中出现至少两次，函数返回 true 。如果数组中每个元素都不相同，则返回 false 。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,3,1]
 * 输出: true
 * 示例 2:
 * <p>
 * 输入: [1,2,3,4]
 * 输出: false
 * 示例 3:
 * <p>
 * 输入: [1,1,1,3,3,4,3,2,4,2]
 * 输出: true
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/contains-duplicate
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("存在重复元素")
public class S19 {

    public boolean containsDuplicate(int[] nums) {
        int len = nums.length;
        Map<Integer, Integer> map = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            int num = nums[i];
            Integer orDefault = map.getOrDefault(num, -1);
            if (1 == orDefault) {
                return true;
            } else {
                map.put(num, 1);
            }
        }
        return false;
    }
}
