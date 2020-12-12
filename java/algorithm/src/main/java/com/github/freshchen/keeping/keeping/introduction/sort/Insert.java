package com.github.freshchen.keeping.keeping.introduction.sort;

import com.github.freshchen.keeping.keeping.annotation.Sort;

/**
 * @author darcy
 * @since 2020/10/13
 **/
@Sort("插入排序")
public class Insert {

    public static void sort(int[] nums) {
        int length = nums.length;
        for (int i = 1; i < length; i++) {
            int cur = nums[i];
            int index = i - 1;
            boolean needInsert = false;
            while (index >= 0) {
                int pre = nums[index];
                if (cur > pre) {
                    break;
                }
                needInsert = true;
                nums[index + 1] = pre;
                index--;
            }
            if (needInsert) {
                nums[index + 1] = cur;
            }
        }
    }

}
