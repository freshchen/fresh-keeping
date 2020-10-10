package org.example.leetcode.m.m11;

import org.example.annotation.Link;
import org.example.common.ListNode;

/**
 * @author darcy
 * @since 2020/10/2
 * <p>
 * 在 O(n log n) 时间复杂度和常数级空间复杂度下，对链表进行排序。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 4->2->1->3
 * 输出: 1->2->3->4
 * 示例 2:
 * <p>
 * 输入: -1->5->3->4->0
 * 输出: -1->0->3->4->5
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sort-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Link("排序链表")
public class M15 {

    public ListNode sortList(ListNode head) {
        ListNode curHead = head;
        curHead.next = null;
        while (head != null) {
            ListNode next = head.next;
            while (head!=null){
                if (next.val < head.val){
                    ListNode tmp = next;

                }
            }
            head = next;
        }
    }
}
