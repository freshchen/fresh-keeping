# 总览

```ActivityHistory
/
```

```commits-recents
project=/
divWidth=50
divHeight=300
divAligh=left
```

```commits-type
project=/
divWidth=50
divHeight=300
divAligh=right
```



## 标签

```dataviewjs
// 生成所有的标签且以 | 分割，修改时只需要修改 join(" | ") 里面的内容。
dv.paragraph(
  dv.pages("").file.tags.distinct().map(t => {return `[${t}](${t})`}).array().join(" | ")
)
```


## 面试题...

```dataviewjs
dv.table(["File", "Tags", "Rating", "Create", "Update"], dv.pages('#interview')
    .sort(b => b.rating, 'desc')
    .map(b => [b.file.link, dv.array(b.file.tags).join(" "), b.rating, b.file.cday, b.file.mday]))
```


## 未完成...

```dataviewjs
dv.table(["File", "Tags", "Rating", "Create", "Update"], dv.pages('"document"')
    .where(b => dv.equal(b.status, "ongoing"))
    .sort(b => b.mday, 'desc')
    .map(b => [b.file.link, dv.array(b.file.tags).join(" "), b.rating, b.file.cday, b.file.mday]))
```


## 已完成...

```dataviewjs
dv.table(["File", "Tags", "Rating", "Create", "Update"], dv.pages('"document"')
    .where(b => dv.equal(b.status, "done"))
    .sort(b => b.mday, 'desc')
    .map(b => [b.file.link, dv.array(b.file.tags).join(" "), b.rating, b.file.cday, b.file.mday]))
```