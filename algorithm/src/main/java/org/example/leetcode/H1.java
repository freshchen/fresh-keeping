package org.example.leetcode;

import org.example.annotation.Array;

/**
 * @author darcy
 * @since 2020/9/7
 * <p>
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 * <p>
 * <p>
 * <p>
 * 上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。 感谢 Marcos 贡献此图。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出: 6
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/trapping-rain-water
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Array("接雨水,给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。")
public class H1 {

    public int trap(int[] height) {
        // TODO
        int result = 0;
        int max = 0;
        int total = 0;
        for (int i = 0; i < height.length - 1; i++) {
            int gap = height[i + 1] - height[i];
            int cur = max + gap;
            if (cur <= 0) {
                continue;
            }
            int curTotal = total + cur;
        }
        return result;
    }
}
