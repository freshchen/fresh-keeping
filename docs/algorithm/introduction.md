# 算法导论笔记

## 算法度量

- T(n) 算法的最长执行时间

- θ (f(n)) 算法复杂度界限，通常取最高阶项，忽略常系数和低阶项

- O(f(n)) 算法复杂度的渐进上界，也就是最坏情况复杂度

- Ω(fn) 算法的渐进下界，也就是算法的最好情况复杂度

- 对任意两个函数 f(n) 和 g(n) ,有 f(n) = θ (g(n))，当且仅当 f(n) =O (g(n)) 且 f(n) = Ω (g(n))

- o(f(n)) 对应 O，是一个不包含上界

- w(f(n)) 对应Ω，是一个不包含下界

- f(n) = O(g(n)) 类似于 a <= b

- f(n) = Ω(g(n)) 类似于 a >= b

- f(n) = θ (g(n)) 类似于 a = b

- f(n) = o(g(n)) 类似于 a < b

- f(n) = w(g(n)) 类似于 a > b

- 分治法递归求解复杂度分析

- 主定理：如果有一个问题规模为 n，递推的子问题数量为 a，每个子问题的规模为n/b（假设每个子问题的规模基本一样），递推以外进行的计算工作为 f(n)（比如归并排序，需要合并序列，则 f(n)就是合并序列需要的运算量），那么对于这个问题有如下递推关系式：

  ![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/main1.jpe)

  然后就可以套公式估算递归的时间复杂度

  ![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/main2.jpe)

  

## 排序

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/alg-1.png)