# 总览

## 标签

```dataviewjs
// 生成所有的标签且以 | 分割，修改时只需要修改 join(" | ") 里面的内容。
dv.paragraph(
  dv.pages("").file.tags.distinct().map(t => {return `[${t}](${t})`}).array().join(" | ")
)
```


## 面试题...

```dataview
table tags, rating,status, file.inlinks,file.outlinks
from #interview
sort rating desc
```


## 未完成...

```dataview
table tags, rating, file.inlinks,file.outlinks
from "document"
where status = "ongoing"
sort rating desc
```


## 已完成...

```dataview
table tags, rating, file.inlinks,file.outlinks
from "document"
where status = "done"
sort rating desc
```

