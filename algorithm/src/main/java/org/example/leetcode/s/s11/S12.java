package org.example.leetcode.s.s11;

import org.example.annotation.Str;

/**
 * @author darcy
 * @since 2020/9/10
 * <p>
 * 实现 strStr() 函数。
 * <p>
 * 给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。
 * <p>
 * 示例 1:
 * <p>
 * 输入: haystack = "hello", needle = "ll"
 * 输出: 2
 * 示例 2:
 * <p>
 * 输入: haystack = "aaaaa", needle = "bba"
 * 输出: -1
 * 说明:
 * <p>
 * 当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。
 * <p>
 * 对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与C语言的 strstr() 以及 Java的 indexOf() 定义相符。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/implement-strstr
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Str("实现 strStr()")
public class S12 {

    public int strStr(String haystack, String needle) {
        // TODO
        if (needle == null || needle.isEmpty()) {
            return 0;
        }
        int hIndex = 0;
        int nIndex = 0;
        char[] h = haystack.toCharArray();
        char[] n = needle.toCharArray();
        for (int i = 0; i < h.length; i++) {

        }
        return 1;
    }

    public int[] getNext(char[] s) {
        int length = s.length;
        int[] ints = new int[length];
        ints[0] = 0;
        for (int i = 1; i < length; i++) {
//            while ()
        }
        return null;
    }
}
