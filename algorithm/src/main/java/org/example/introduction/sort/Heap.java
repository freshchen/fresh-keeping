package org.example.introduction.sort;

/**
 * @author darcy
 * @since 2020/10/26
 **/
public class Heap {

    public static void sort(int[] nums) {
        int length = nums.length;
        buildMaxDump(nums);
        for (int i = length - 1; i > 0; i--) {
            swap(nums, 0, i);
            heapify(nums, 0, i);
        }
    }

    private static void buildMaxDump(int[] nums) {
        int length = nums.length;
        for (int i = length / 2 - 1; i >= 0; i--) {
            heapify(nums, i, length);
        }
    }

    private static void heapify(int[] nums, int curr, int len) {
        assert curr <= len - 1;
        int v = nums[curr];
        for (int i = left(curr); i < len; i = left(i)) {
            int left = nums[i];
            if (i + 1 < len) {
                int right = nums[i + 1];
                if (right > left) {
                    i++;
                }
            }
            int max = nums[i];
            if (v > max) {
                break;
            }
            nums[curr] = max;
            curr = i;
        }
        nums[curr] = v;
    }

    private static int left(int curr) {
        return curr * 2 + 1;
    }

    private static void swap(int[] s, int from, int to) {
        int temp = s[from];
        s[from] = s[to];
        s[to] = temp;
    }
}
