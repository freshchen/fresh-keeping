package org.example.leetcode;

import org.example.annotation.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * @author darcy
 * @since 2020/9/7
 * <p>
 * 给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数大于 ⌊ n/2 ⌋ 的元素。
 * <p>
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 * <p>
 *  
 * <p>
 * 示例 1:
 * <p>
 * 输入: [3,2,3]
 * 输出: 3
 * 示例 2:
 * <p>
 * 输入: [2,2,1,1,1,2,2]
 * 输出: 2
 * <p>
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/majority-element
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("多数元素,给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数大于 ⌊ n/2 ⌋ 的元素。")
public class S6 {

    public int majorityElement(int[] nums) {
        int length = nums.length;
        Map<Integer, Integer> map = new HashMap<>(length);
        int result = -1;
        for (int i = 0; i < length; i++) {
            int v = nums[i];
            int o = map.getOrDefault(v, 0);
            int curr = o + 1;
            if (curr > length / 2) {
                result = v;
                break;
            }
            map.put(v, curr);
        }
        return result;
    }
}
