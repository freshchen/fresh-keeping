---
begin: 2021-11-04
status: half
rating: 1
---

# k8s命令

```bash
      
# 指定命名空间安装
kubectl apply -f <>.yaml --namespace=<>

# 查看POD 详情 可以看到 POD的 IP 以及实际部署的 Node

kubectl get pod -o wide

# 查看POD label
kubectl get pod --show-labels

# 指定label查找

kubectl get pod -l app=order

```


## 参考链接


##### 标签
#k8s #tools 