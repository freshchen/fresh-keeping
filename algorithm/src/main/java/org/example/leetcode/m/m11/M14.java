package org.example.leetcode.m.m11;

import org.example.annotation.Link;
import org.example.common.ListNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author darcy
 * @since 2020/9/16
 * <p>
 * 给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。
 * <p>
 * 示例：
 * <p>
 * 给定一个链表: 1->2->3->4->5, 和 n = 2.
 * <p>
 * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
 * 说明：
 * <p>
 * 给定的 n 保证是有效的。
 * <p>
 * 进阶：
 * <p>
 * 你能尝试使用一趟扫描实现吗？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Link("删除链表的倒数第N个节点")
public class M14 {

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode temp = head;
        Map<Integer, ListNode> m = new HashMap<>();
        int index = 1;
        while (temp != null) {
            m.put(index++, temp);
            temp = temp.next;
        }
        int size = m.size();
        if (size == 1)
            return null;
        if (n == 1) {
            m.get(size - 1).next = null;
        } else if (n == size) {
            head = head.next;
        } else {
            m.get(size - n).next = m.get(size - n + 2);
        }
        return head;
    }
}
