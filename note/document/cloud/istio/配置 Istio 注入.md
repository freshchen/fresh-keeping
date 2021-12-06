---
begin: 2021-11-30
status: ongoing
rating: 1
---

# 配置 Istio 注入

## Namespace 全局配置

通过给 Namespace 打 istio-injection lable 实现

`kubectl get ns -L istio-injection`

## Yaml 中配置

```yaml
  template:
    metadata:
      annotations:
        sidecar.istio.io/inject: "false"
```


## 参考链接


##### 标签
#cloud-native