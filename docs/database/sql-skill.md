## SQL技巧

###  or 还是 union

- 如果where和or涉及不同列，索引会失效
- or和union的区别，具体链接放在末尾。大致意思是: 对于单列来说，用or是没有任何问题的，但是or涉及到多个列的时候，每次select只能选取一个index，如果选择了area，population就需要进行table-scan，即全部扫描一遍，但是使用union就可以解决这个问题，分别使用area和population上面的index进行查询。 但是这里还会有一个问题就是，UNION会对结果进行排序去重，可能会降低一些performance(这有可能是方法一比方法二快的原因），所以最佳的选择应该是两种方法都进行尝试比较。 （stackoverflow链接: https://stackoverflow.com/questions/13750475/sql-performance-union-vs-or）