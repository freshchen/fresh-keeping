package com.github.freshchen.keeping.keeping.leetcode.s.s11;

import com.github.freshchen.keeping.keeping.annotation.Str;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author darcy
 * @since 2020/9/16
 * <p>
 * 给定一个字符串，找到它的第一个不重复的字符，并返回它的索引。如果不存在，则返回 -1。
 * <p>
 *  
 * <p>
 * 示例：
 * <p>
 * s = "leetcode"
 * 返回 0
 * <p>
 * s = "loveleetcode"
 * 返回 2
 *  
 * <p>
 * 提示：你可以假定该字符串只包含小写字母。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/first-unique-character-in-a-string
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Str("字符串中的第一个唯一字符")
public class S17 {

    public int firstUniqChar(String s) {
        Objects.requireNonNull(s);
        int length = s.length();
        Map<Character, Integer> map = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            Integer count = map.get(c);
            if (Objects.isNull(count)) {
                map.put(c, 1);
            } else {
                map.put(c, count + 1);
            }
        }
        int index = -1;
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (map.get(c) == 1) {
                index = i;
                break;
            }
        }
        return index;
    }
}
