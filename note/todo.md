
## 未完成...

```dataviewjs
dv.table(["File", "Tags", "Rating", "Create", "Update"], dv.pages('"document"')
    .where(b => dv.equal(b.status, "ongoing"))
    .sort(b => b.mday, 'desc')
    .map(b => [b.file.link, dv.array(b.file.tags).join(" "), b.rating, b.file.cday, b.file.mday]))
```

## 待补充...

```dataviewjs
dv.table(["File", "Tags", "Rating", "Create", "Update"], dv.pages('"document"')
    .where(b => dv.equal(b.status, "half"))
    .sort(b => b.mday, 'desc')
    .map(b => [b.file.link, dv.array(b.file.tags).join(" "), b.rating, b.file.cday, b.file.mday]))
```

