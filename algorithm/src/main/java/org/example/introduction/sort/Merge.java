package org.example.introduction.sort;

/**
 * @author darcy
 * @since 2020/10/13
 **/
public class Merge {

    public static void sort(int[] nums) {
        sort(nums, 0, nums.length - 1);
    }

    private static void sort(int[] nums, int left, int right) {
        if (left == right) {
            return;
        }
        int mid = left + ((right - left) >> 1);
        sort(nums, left, mid);
        sort(nums, mid + 1, right);
        merge(nums, left, mid, right);
    }

    private static void merge(int[] nums, int left, int mid, int right) {
        int[] help = new int[right - left + 1];
        int helpIndex = 0;
        int leftIndex = left;
        int rightIndex = mid + 1;
        while (leftIndex <= mid && rightIndex <= right) {
            help[helpIndex++] = nums[leftIndex] < nums[rightIndex] ? nums[leftIndex++] : nums[rightIndex++];
        }
        while (leftIndex <= mid) {
            help[helpIndex++] = nums[leftIndex++];
        }
        while (rightIndex <= right) {
            help[helpIndex++] = nums[rightIndex++];
        }
        for (int i = 0; i < help.length; i++) {
            nums[left + i] = help[i];
        }
    }
}
