## 解题技巧

### Fisher-Yates 洗牌算法

- 在每次迭代中，生成一个范围在当前下标到数组末尾元素下标之间的随机整数。接下来，将当前元素和随机选出的下标所指的元素互相交换
- 主要用于随机打乱数组



### Knuth-Morris-Pratt算法（简称KMP）

- [link](http://www.ruanyifeng.com/blog/2013/05/Knuth–Morris–Pratt_algorithm.html)
- [link2](https://blog.csdn.net/daaikuaichuan/article/details/80719203)

- 主要用于串匹配
- “真前缀”指**除了自身以外**，一个字符串的全部头部组合；”真后缀”指**除了自身以外**，一个字符串的全部尾部组合。
- 假设主串A ”BBC ABCDAB ABCDABCDABDE”，需要找出 A 中是否有字符串 B ”ABCDABD”，循环对比 a 和 b 对应位置的值，算出真前缀和真后缀的最大共有元素长度，例如匹配到最后一位 ”D“ 失败了，计算出 ”ABCDAB” 的最大共有元素长度为2 也就是 首尾都有一个 “AB”，直接从 ”ABCDABD” 的 第二位C继续匹配，也就是跳过了重复的 ”AB“