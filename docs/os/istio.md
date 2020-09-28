# 初识 Istio - 服务网格管理工具

## What is a service mesh（服务网格）?

微服务在国内流行已经多年了，大多数公司选择了基于容器化技术（ Docker ）以及容器编排管理平台 （ Kubernetes ）落地微服务 ，然而这仅仅是个开始，微服务的发展没有停滞。

相信在调研微服务开发框架的过程中，会发现成熟的框架如 Spring Cloud ，Dubbo，Microprofile，它们都提供了诸如服务发现、负载均衡、故障恢复、度量和监控等方面的解决方案，但是都不同程度的产生了很多业务无关的代码。运维层面，在 Kubernetes 平台上要实现一些常见需求很不容易，例如  A/B 测试、金丝雀发布、速率限制、访问控制和端到端认证等。

2016 年开发 Linkerd 的 Buoyant 公司提出，要在开发和运维中间增加一层基础设施，提供对网络流量的洞察和操作控制的能力，包括服务注册发现、负载均衡、故障恢复、监控、权限控制等等，而这层基础设施就称作服务网格。

## What is Istio?

Istio 是谷歌对于服务网格的实现，支持 Kubernetes，Consul，VMs等多种环境，并作为透明的一层接入到现有的微服务应用程序里，提供了如下功能：

- 为 HTTP、gRPC、WebSocket 和 TCP 流量自动负载均衡。
- 通过丰富的路由规则、重试、故障转移和故障注入对流量行为进行细粒度控制。
- 可插拔的策略层和配置 API，支持访问控制、速率限制和配额。
- 集群内（包括集群的入口和出口）所有流量的自动化度量、日志记录和追踪。
- 在具有强大的基于身份验证和授权的集群中实现安全的服务间通信。

## How istio work？

 Istio 的架构图如下，istio 服务网格，分为控制面和数据面，所有流量都从 istio 的 ingress 进，从 egress出，以 Kubernetes 为例，上图的两个 Service 对应 Kubernetes 中的 Pod，istio 使用 sidecar 模式，给每个 Pod 加了一层代理，实际请求通通路由到代理，满足条件才路由给 Pod，至于控制面很简单，就是把路由规则同步给各个代理，并且完成一些管理，安全，遥测工作。可以发现网格中 Kubernetes 的网络完全被 istio 接管了，每个 Pod 和其代理构成了一个个对外零信任的高内聚的小格子。

![图1](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/istio-arch.png)

插一句，Docker 提倡每个进程一个容器，Kubernetes 提倡每个容器一个 Pod，istio 又提倡给每个 Pod 一个格子，最小单元变的越来越大了，每个格子外面还会接着套么？

## 探秘 Istio 流量管理

流量管理无疑是 Istio 的核心，流量管理的核心又是 sidecar 代理，正是通过一个个 sidecar 代理，istio 能清晰的知道流量从哪来到哪去，从而很方便的实现，日志收集，遥测，追踪，监控，限流等功能。下面让我们一起近距离体验一下 Istio 的流量管理功能。

### 安装

参照 [官方教程](https://istio.io/latest/docs/setup/getting-started/) 安装 Isito demo profile，如下所示 Istio 相关的服务全装到了 istio-system 的 namespace。其中 istiod 是 istio 的核心服务，Pilot（服务发现），Galley（配置管理），Citadel（证书管理）等服务被统一成了 istiod，istiod 中 跑着 discovery 进程，用于监听 Kubernetes 的 ApiServer 并且实时把配置跟新到各个 sidecar 代理中。istio-ingressgateway 以及 istio-egressgateway 是 demo 模式下默认安装，运行中 Pilot 的客户端，接受配置实时跟新规则，envoy 是类似 ngnix 的轻量级代理工具。istio-ingressgateway 和Kubernetes 平台中的 nginx-ingress 组建起相同作用，作为平台外部请求进入网格的入口。其他 grafana，jaeger，kiali，prometheus为 istio 集成的可观察性相关的组件。

```bash
chenling@ChendeMacBook-Pro ~ % kubectl get pod -n istio-system
NAME                                    READY   STATUS    RESTARTS   AGE
grafana-767c5487d6-j5l92                1/1     Running   0          5d5h
istio-egressgateway-55856f9f8f-s78md    1/1     Running   0          5d21h
istio-ingressgateway-85fbcc77b8-8rsfk   1/1     Running   0          5d21h
istiod-6dc785c4b9-z8v9v                 1/1     Running   0          5d21h
jaeger-566c547fb9-zbhn7                 1/1     Running   0          5d5h
kiali-89fd7f87b-r64dw                   1/1     Running   0          5d21h
prometheus-788c945c9c-xn8mz             2/2     Running   0          5d5h
chenling@ChendeMacBook-Pro ~ % kubectl exec -it istiod-6dc785c4b9-z8v9v -n istio-system -- ps -ef              
UID        PID  PPID  C STIME TTY          TIME CMD
istio-p+     1     0  3 Sep27 ?        00:28:28 /usr/local/bin/pilot-discovery d
istio-p+    34     0  0 00:41 pts/0    00:00:00 sh
istio-p+   112     0  0 08:04 pts/1    00:00:00 ps -ef
chenling@ChendeMacBook-Pro ~ % kubectl exec -it istio-ingressgateway-85fbcc77b8-8rsfk -n istio-system -- ps -ef
UID        PID  PPID  C STIME TTY          TIME CMD
istio-p+     1     0  0 Sep27 ?        00:01:38 /usr/local/bin/pilot-agent proxy
istio-p+    14     1  1 Sep27 ?        00:08:48 /usr/local/bin/envoy -c etc/isti
istio-p+    65     0  0 08:04 pts/0    00:00:00 ps -ef
```

### 注入

通过给 default namespace 打如下标签，pod 创建时会自动注入 sidecar

```bash
$ kubectl label namespace default istio-injection=enabled
```

当我们通过 Kubernetes 的客户端工具给 ApiServer 发送指令时，平台的 Scheduler 调度服务，会监听创建请求，并找到合适的机器，把相关信息传给 ApiServer 并把原数据写入 etcd，Isito 的 Pilot 采用类似机制监听ApiServer ，当 namespace 被打上自动注入标签，就会修改创建 pod 的原数据，增加 sidecar 代理容器到 pod 中，并且监听到 Istio 自定义的资源变动，通知到相关的  sidecar。

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/istio-2.png)

当然也可以手动注入 sidecar，手动注入会启动新的 pod

```bash
istioctl kube-inject -f deployment.yaml -o deployment-injected.yaml
```

书写 deployment 资源文件，注入完成之后如下所示，为啥 Pod 中有 2 个容器？

```bash
chenling@ChendeMacBook-Pro ~ % kubectl get pod
NAME                         READY   STATUS    RESTARTS   AGE
client-6b495f748b-tgt97      2/2     Running   2          23h
hello-bc8bb7cd6-pvvkv        2/2     Running   0          24h
hello-new-5b7cbf7df4-ksxtg   2/2     Running   0          24h
```

默认容器是我们声明的，运行着一个 httpd 服务

```bash
chenling@ChendeMacBook-Pro ~ % kubectl exec -it hello-bc8bb7cd6-pvvkv -- ps -ef
Defaulting container name to hello.
Use 'kubectl describe pod/hello-bc8bb7cd6-pvvkv -n default' to see all of the containers in this pod.
PID   USER     TIME  COMMAND
    1 root      0:00 httpd -f -p 8080 -h /var/www
  435 root      0:00 ps -ef
```

查看 pod 中另外一个容器，可以发现和 istio-gateway 一样，其实运行着一个 envoy 代理服务，通过 pilot-agent 接受控制面信息。

```bash
chenling@ChendeMacBook-Pro ~ % kubectl exec -it hello-bc8bb7cd6-pvvkv -c istio-proxy -- ps -ef
UID        PID  PPID  C STIME TTY          TIME CMD
istio-p+     1     0  0 Sep27 ?        00:01:50 /usr/local/bin/pilot-agent proxy
istio-p+    15     1  1 Sep27 ?        00:05:24 /usr/local/bin/envoy -c etc/isti
istio-p+   140     0  0 08:13 pts/0    00:00:00 ps -ef
```

### 注入流程

下面我们看看注入过程，首先被注入的pod中增加了名为 istio-init 的 initContainer，如下日志显示初始化过程，通过在容器 iptables nat 表中增加规则，把所有非 Istio 的入站流量重定向到 15006 端口，所有非 Istio 的出站流量定向到 15001 端口

```bash
chenling@ChendeMacBook-Pro ~ % kubectl logs --tail=32 hello-bc8bb7cd6-pvvkv -c istio-init
iptables-save 
# Generated by iptables-save v1.6.1 on Sun Sep 27 07:20:03 2020
*nat
:PREROUTING ACCEPT [0:0]
:INPUT ACCEPT [0:0]
:OUTPUT ACCEPT [0:0]
:POSTROUTING ACCEPT [0:0]
:ISTIO_INBOUND - [0:0]
:ISTIO_IN_REDIRECT - [0:0]
:ISTIO_OUTPUT - [0:0]
:ISTIO_REDIRECT - [0:0]
-A PREROUTING -p tcp -j ISTIO_INBOUND
-A OUTPUT -p tcp -j ISTIO_OUTPUT
-A ISTIO_INBOUND -p tcp -m tcp --dport 15008 -j RETURN
-A ISTIO_INBOUND -p tcp -m tcp --dport 22 -j RETURN
-A ISTIO_INBOUND -p tcp -m tcp --dport 15090 -j RETURN
-A ISTIO_INBOUND -p tcp -m tcp --dport 15021 -j RETURN
-A ISTIO_INBOUND -p tcp -m tcp --dport 15020 -j RETURN
-A ISTIO_INBOUND -p tcp -j ISTIO_IN_REDIRECT
-A ISTIO_IN_REDIRECT -p tcp -j REDIRECT --to-ports 15006
-A ISTIO_OUTPUT -s 127.0.0.6/32 -o lo -j RETURN
-A ISTIO_OUTPUT ! -d 127.0.0.1/32 -o lo -m owner --uid-owner 1337 -j ISTIO_IN_REDIRECT
-A ISTIO_OUTPUT -o lo -m owner ! --uid-owner 1337 -j RETURN
-A ISTIO_OUTPUT -m owner --uid-owner 1337 -j RETURN
-A ISTIO_OUTPUT ! -d 127.0.0.1/32 -o lo -m owner --gid-owner 1337 -j ISTIO_IN_REDIRECT
-A ISTIO_OUTPUT -o lo -m owner ! --gid-owner 1337 -j RETURN
-A ISTIO_OUTPUT -m owner --gid-owner 1337 -j RETURN
-A ISTIO_OUTPUT -d 127.0.0.1/32 -j RETURN
-A ISTIO_OUTPUT -j ISTIO_REDIRECT
-A ISTIO_REDIRECT -p tcp -j REDIRECT --to-ports 15001
COMMIT
# Completed on Sun Sep 27 07:20:03 2020
```

如下所示 ，15006 和 15001 端口都是 envoy 提供

```bash
chenling@ChendeMacBook-Pro ~ % kubectl exec -it hello-bc8bb7cd6-pvvkv -c istio-proxy -- netstat -nltp
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name    
tcp        0      0 0.0.0.0:15021           0.0.0.0:*               LISTEN      15/envoy            
tcp        0      0 0.0.0.0:15090           0.0.0.0:*               LISTEN      15/envoy            
tcp        0      0 127.0.0.1:15000         0.0.0.0:*               LISTEN      15/envoy            
tcp        0      0 0.0.0.0:15001           0.0.0.0:*               LISTEN      15/envoy            
tcp        0      0 0.0.0.0:15006           0.0.0.0:*               LISTEN      15/envoy            
tcp6       0      0 :::15020                :::*                    LISTEN      1/pilot-agent       
tcp6       0      0 :::8080                 :::*                    LISTEN      -    
```

综上所述，istio流量管理的实现大致如下

- 通过自动注入，给 pod 增加 iptables，把所有进出流量指向 envoy
- 控制面的 pilot 监控 istio 自定义资源变化，把规则发送给各个 sidecar
- sidecar 中的 pilot-agent 接受 pilot 的信息，热更新 envoy 代理规则，无需重启 pod即可改变流量路径

### 配置

实践一下配置网格行为

- 配置名为 xingren-gateway 的 Istio Gateway 接受所有 host为 xingren.upup 的外部流量

```yaml
---
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: xingren-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
    - port:
        number: 80
        name: http
        protocol: HTTP
      hosts:
        - xingren.upup
```

- 配置 VirtualService 接受所有来自 ingren-gateway，host 为 xingren.upup 或者 hello 的流量
- 如果 http header 中 version 字段为 new，流量转到 new 子集，并且设置了 5 秒的超时时间，并且以 10% 的比例，注入一个10秒的请求延迟，已验证服务的容错能力
- 如果 http header 中 version 字段不存在，或者不是 new，则 70% 流量转到 latest 子集， 30% 流量转到 new 子集
- 配置可复用的 DestinationRule，定义了  latest 子集和 new 子集，按照 version 标签匹配到 Kubernetes hello 服务下的真实pod，同时设置了并发请求不能大于1的熔断规则

```yaml
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: hello-virtualservice
spec:
  hosts:
    - hello
    - xingren.upup
  gateways:
    - xingren-gateway
  http:
    - match:
        - headers:
            version:
              exact: new
      route:
        - destination:
            host: hello
            subset: new
      fault:
        delay:
          percentage:
            value: 10
          fixedDelay: 10s
      timeout: 5s
    - route:
        - destination:
            host: hello
            subset: latest
          weight: 70
        - destination:
            host: hello
            subset: new
          weight: 30

---
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: hello-destinationrule
spec:
  host: hello
  trafficPolicy:
    connectionPool:
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1
      tcp:
        maxConnections: 1
  subsets:
    - name: latest
      labels:
        version: latest
    - name: new
      labels:
        version: new
```

当然 istio 的功能远远不止这些，详见 [官网案例](https://istio.io/latest/docs/tasks/traffic-management/)

### 可视化

可以通过如下命令观察服务网格，当然也可以通过网关暴露出去

```bash
chenling@ChendeMacBook-Pro ~ % istioctl dashboard --help
Access to Istio web UIs

Usage:
  istioctl dashboard [flags]
  istioctl dashboard [command]

Aliases:
  dashboard, dash, d

Available Commands:
  controlz    Open ControlZ web UI
  envoy       Open Envoy admin web UI
  grafana     Open Grafana web UI
  jaeger      Open Jaeger web UI
  kiali       Open Kiali web UI
  prometheus  Open Prometheus web UI
  zipkin      Open Zipkin web UI
```

向服务中注入一些流量

```bash
chenling@ChendeMacBook-Pro ~ % for i in `seq 1000`; do wget -q -O - http://xingren.upup; sleep 0.2;done
 Hello World(new) 
 Hello World 
 Hello World 
 Hello World 
 Hello World 
 Hello World 
 Hello World(new) 
 Hello World 
 Hello World 
 Hello World 
 Hello World(new) 
 Hello World 
 Hello World 
 Hello World 
 Hello World(new) 
 Hello World 
 ...
```

观察 istio 的可视化界面 kiali，可以看到流量从外部通过 hello 虚拟服务进入 pod，并且权重 大致 7比3

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/istio-5.png)

## 总结

Istio 出色的完成了服务网格该有的功能，且有很强的可扩展性，可以方便的整合 prometheus，jaeger 等工具，随着迭代易用性也有所提高。虽然 Istio 还没有被大规模用于生产环境 ，并且有质疑其占用了过多的资源，总的来说利大于弊，经实验，没有被 Istio 注入的 pod 访问被注入的资源，不会受到任何影响，会直接透传给真实pod，所以还是可以小范围尝鲜 Istio 的。