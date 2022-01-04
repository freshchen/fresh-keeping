---
begin: 2021-12-29
status: ongoing
rating: 1
---

# Sleuth自动配置

[Spring Cloud Sleuth Reference Documentation](https://docs.spring.io/spring-cloud-sleuth/docs/3.0.4/reference/htmlsingle/#common-application-properties)
[brave/brave at master · openzipkin/brave · GitHub](https://github.com/openzipkin/brave/tree/master/brave)
[brave/brave at master · openzipkin/brave · GitHub](https://github.com/openzipkin/brave/tree/master/brave#baggage)
[brave/instrumentation/http at master · openzipkin/brave · GitHub](https://github.com/openzipkin/brave/tree/master/instrumentation/http)


### TraceWebServletConfiguration
spring.sleuth.web.enabled=`true` 开启 web 服务的拦截
spring.sleuth.web.servlet.enabled=`true`  开启 servlet 的拦截，也就是拦截所有 http 请求

#### TraceWebAspect

 TraceWebAspect AOP 拦截 @Controller 以及 @RestController 下的异步执行方法进行包装，一般执行不到

#### LazyTracingFilter
LazyTracingFilter 是 servlet 拦截器，拦截器顺序 spring.sleuth.web.filter-order。默认是 Ordered.HIGHEST_PRECEDENCE + 5
代理模式，LazyTracingFilter 所有操作通过 this.tracingFilter =TracingFilter.create(this.beanFactory.getBean(CurrentTraceContext.class),  
 this.beanFactory.getBean(HttpServerHandler.class));
	- 默认 BraveCurrentTraceContext，BraveHttpServerHandler

先执行 BraveHttpServerHandler# handleReceive
- 委托给 brave.http.HttpServerHandler 执行，先后进行以下包装
- brave.http.HttpServerHandler 先 extract 提取出 B3 头
	- b3 以及X-B3-Sampled 是否采样，1 采 0 不采
	- X-B3-Flags = 1 就是 debug
	- 如果有 X-B3-TraceId 就复原出上下文，包括 X-B3-TraceId，X-B3-SpanId，X-B3-ParentSpanId
- brave.http.HttpServerHandler 执行 nextSpan
	- 此时 Span 信息全部生产好了
- brave.http.HttpHandler#handleStart 主要解析请求
	- brave.http.HttpRequestParser.Default#parse 指定 trace 的名称
![](image/Pasted%20image%2020211229181647.png)



执行业务请求

后执行 BraveHttpServerHandler# handleSend

if (statusCode < 200 || statusCode > 299) { // not success code  
 STATUS_CODE.tag(response, context, span);  
}
if (httpStatus < 100 || httpStatus > 399) {  
 span.tag(Tags.ERROR.key(), statusCodeString(httpStatus));  
}

SpanFilter 可以控制是不是要上报，默认有个根据 名字过滤
spring.sleuth.span-filter.enabled
SpanIgnoringSpanFilter，配置如下spring.sleuth.span-filter

org.springframework.cloud.sleuth.zipkin2.RestTemplateSender#sendSpans 通过 deamon 都线程后台发送

[{"traceId":"ee2037edf4679df9","id":"ee2037edf4679df9","kind":"SERVER","name":"get /trace3","timestamp":1640783010184175,"duration":7924950,"localEndpoint":{"serviceName":"test","ipv4":"172.18.6.142"},"remoteEndpoint":{"ipv6":"::1","port":60781},"tags":{"http.method":"GET","http.path":"/trace3","mvc.controller.class":"Trace","mvc.controller.method":"trace3"}}]



## BraveAutoConfiguration
注册关键 bean Tracer

`@Value("${spring.zipkin.service.name:${spring.application.name:default}}")`

BraveBridgeConfiguration


id 生成 brave.propagation.TraceContext#traceIdString

## ZipkinAutoConfiguration
配置 Reporter


采样率是全局的，因此需要对接口指定采样

### BraveRedisAutoConfiguration


# 扩展发送到

```java
@Builder
public class ZipkinRpcSender extends Sender {

    final int messageMaxBytes;
    final BytesMessageEncoder messageEncoder;
    volatile boolean closeCalled;

    @Override
    public Encoding encoding() {
        return Encoding.JSON;
    }

    @Override
    public int messageMaxBytes() {
        return messageMaxBytes;
    }

    @Override
    public int messageSizeInBytes(List<byte[]> encodedSpans) {
        return encoding().listSizeInBytes(encodedSpans);
    }

    @Override
    public Call<Void> sendSpans(List<byte[]> encodedSpans) {
        if (closeCalled) {
            throw new ClosedSenderException();
        }
        byte[] encode = messageEncoder.encode(encodedSpans);
        return new BrpcCall(encode);
    }

    @Override
    public void close() throws IOException {
        this.closeCalled = true;
    }

    @AllArgsConstructor
    @Slf4j
    static class BrpcCall extends Call.Base<Void> {

        private final byte[] message;

        @Override
        protected Void doExecute() throws IOException {
            System.out.println(new String(message, StandardCharsets.UTF_8));
            return null;
        }

        @Override
        protected void doEnqueue(Callback<Void> callback) {
            try {
                System.out.println(new String(message, StandardCharsets.UTF_8));
                callback.onSuccess(null);
            } catch (RuntimeException | Error e) {
                callback.onError(e);
            }
        }

        @Override
        public Call<Void> clone() {
            return new ZipkinRpcSender.BrpcCall(this.message);
        }

    }
}
```

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(name = ZipkinAutoConfiguration.SENDER_BEAN_NAME)
@ConditionalOnProperty(value = "spring.zipkin.sender.type", havingValue = "rpc", matchIfMissing = false)
@AutoConfigureAfter(rpc的自动化配置)
public class ZipkinBrpcSenderConfiguration {

    @Configuration(proxyBeanMethods = false)
//    @ConditionalOnBean(ActiveMQConnectionFactory.class)
    static class ZipkinRpcSenderBeanConfiguration {

        /**
         * 默认超过 1M 的 Span 丢弃
         */
        @Value("${spring.zipkin.rpc.message-max-bytes:1048576}")
        private int messageMaxBytes;

        @Bean(ZipkinAutoConfiguration.SENDER_BEAN_NAME)
        Sender rpcSender() {
            return ZipkinRpcSender.builder()
                .messageMaxBytes(messageMaxBytes)
                .messageEncoder(BytesMessageEncoder.JSON)
                .build();
        }

    }

}

```


## 参考链接

[spring-cloud-sleuth源码学习二 - 云+社区 - 腾讯云](https://cloud.tencent.com/developer/article/1884429?from=10680)
[spring-cloud-sleuth源码学习三 - 云+社区 - 腾讯云](https://cloud.tencent.com/developer/article/1886833?from=10680)

##### 标签
