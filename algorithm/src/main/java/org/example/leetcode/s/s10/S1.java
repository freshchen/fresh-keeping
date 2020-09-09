package org.example.leetcode.s.s10;

import org.example.annotation.Tree;
import org.example.common.TreeNode;

import java.util.LinkedList;
import java.util.Objects;


/**
 * @author darcy
 * @since 2020/08/23
 * 给定一个二叉树，检查它是否是镜像对称的。
 * <p>
 *  
 * <p>
 * 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
 * <p>
 * 1
 * / \
 * 2   2
 * / \ / \
 * 3  4 4  3
 *  
 * <p>
 * 但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的:
 * <p>
 * 1
 * / \
 * 2   2
 * \   \
 * 3    3
 *  
 * <p>
 * 进阶：
 * <p>
 * 你可以运用递归和迭代两种方法解决这个问题吗？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/symmetric-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 **/
@Tree("对称二叉树,给定一个二叉树，检查它是否是镜像对称的。")
public class S1 {

    /**
     * 递归
     *
     * @param root
     * @return
     */
    public boolean isSymmetric(TreeNode root) {
        if (Objects.isNull(root)) {
            return true;
        }
        return isSymmetric(root.left, root.right);
    }

    public boolean isSymmetric(TreeNode left, TreeNode right) {
        if (Objects.isNull(left) && Objects.isNull(right)) {
            return true;
        }
        if (Objects.isNull(left) || Objects.isNull(right)) {
            return false;
        }
        if (left.val == right.val) {
            return isSymmetric(left.left, right.right) && isSymmetric(left.right, right.left);
        }
        return false;
    }

    /**
     * 迭代
     *
     * @param root
     * @return
     */
    public boolean isSymmetric1(TreeNode root) {
        if (Objects.isNull(root)) {
            return true;
        }
        LinkedList<TreeNode> trees = new LinkedList<>();
        trees.add(root);
        trees.add(root);
        while (!trees.isEmpty()) {
            TreeNode left = trees.poll();
            TreeNode right = trees.poll();
            if (Objects.isNull(left) && Objects.isNull(right)) {
                continue;
            }
            if (Objects.isNull(left) || Objects.isNull(right)) {
                return false;
            }
            if (left.val != right.val) {
                return false;
            }
            trees.add(left.left);
            trees.add(right.right);
            trees.add(left.right);
            trees.add(right.left);
        }
        return true;
    }
}
