package com.github.freshchen.keeping.keeping.leetcode.m.m11;

import com.github.freshchen.keeping.keeping.annotation.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * @author darcy
 * @since 2020/9/10
 * <p>
 * 给定一个 n x n 矩阵，其中每行和每列元素均按升序排序，找到矩阵中第 k 小的元素。
 * 请注意，它是排序后的第 k 小元素，而不是第 k 个不同的元素。
 * <p>
 *  
 * <p>
 * 示例：
 * <p>
 * matrix = [
 * [ 1,  5,  9],
 * [10, 11, 13],
 * [12, 13, 15]
 * ],
 * k = 8,
 * <p>
 * 返回 13。
 *  
 * <p>
 * 提示：
 * 你可以假设 k 的值永远是有效的，1 ≤ k ≤ n2 。
 * <p>
 * <p>
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/kth-smallest-element-in-a-sorted-matrix
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Matrix("有序矩阵中第K小的元素")
public class M11 {

    public int kthSmallest(int[][] matrix, int k) {
        int length = matrix.length;
        List<Integer> integers = new ArrayList<>(length * length);
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                integers.add(matrix[i][j]);
            }
        }
        int[] ints = integers.stream()
                .mapToInt(Integer::intValue)
                .sorted()
                .toArray();
        return ints[k - 1];

    }
}
