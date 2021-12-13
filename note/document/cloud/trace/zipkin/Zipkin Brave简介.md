---
begin: 2021-12-08
status: ongoing
rating: 1
---

# Brave简介

[GitHub - openzipkin/brave: Java distributed tracing implementation compatible with Zipkin backend services.](https://github.com/openzipkin/brave)

## 拦截支持

Here's a brief overview of what's packaged here:

-   [dubbo](https://github.com/openzipkin/brave/blob/master/instrumentation/dubbo/README.md) - Tracing filter for RPC providers and consumers in [Apache Dubbo](http://dubbo.apache.org/en-us/)
-   [dubbo-rpc](https://github.com/openzipkin/brave/blob/master/instrumentation/dubbo-rpc/README.md) - Tracing filter for RPC providers and consumers in [Alibaba Dubbo](http://dubbo.io/books/dubbo-user-book-en/)
-   [grpc](https://github.com/openzipkin/brave/blob/master/instrumentation/grpc/README.md) - Tracing client and server interceptors for [grpc](https://github.com/openzipkin/brave/blob/master/instrumentation/github.com/grpc/grpc-java)
-   [httpasyncclient](https://github.com/openzipkin/brave/blob/master/instrumentation/httpasyncclient/README.md) - Tracing decorator for [Apache HttpClient](https://hc.apache.org/httpcomponents-asyncclient-dev/) 4.0+
-   [httpclient](https://github.com/openzipkin/brave/blob/master/instrumentation/httpclient/README.md) - Tracing decorator for [Apache HttpClient](http://hc.apache.org/httpcomponents-client-4.4.x/index.html) 4.3+
-   [jaxrs2](https://github.com/openzipkin/brave/blob/master/instrumentation/jaxrs2/README.md) - Client tracing filter and span customizing resource filter for JAX-RS 2.x
-   [jersey-server](https://github.com/openzipkin/brave/blob/master/instrumentation/jersey-server/README.md) - Tracing and span customizing application event listeners for [Jersey Server](https://jersey.github.io/documentation/latest/monitoring_tracing.html#d0e16007).
-   [jms](https://github.com/openzipkin/brave/blob/master/instrumentation/jms/README.md) - Tracing decorators for JMS 1.1-2.01 producers, consumers and listeners.
-   [kafka-clients](https://github.com/openzipkin/brave/blob/master/instrumentation/kafka-clients/README.md) - Tracing decorators for Kafka 0.11+ producers and consumers.
-   [kafka-streams](https://github.com/openzipkin/brave/blob/master/instrumentation/kafka-streams/README.md) - Tracing decorator for Kafka Streams 2.0+ clients.
-   [mongodb](https://github.com/openzipkin/brave/blob/master/instrumentation/mongodb/README.md) - Tracing MongoDB command listener
-   [mysql](https://github.com/openzipkin/brave/blob/master/instrumentation/mysql/README.md) - Tracing MySQL statement interceptor
-   [mysql6](https://github.com/openzipkin/brave/blob/master/instrumentation/mysql6/README.md) - Tracing MySQL v6 statement interceptor
-   [mysql8](https://github.com/openzipkin/brave/blob/master/instrumentation/mysql8/README.md) - Tracing MySQL v8 statement interceptor
-   [netty-codec-http](https://github.com/openzipkin/brave/blob/master/instrumentation/netty-codec-http/README.md) - Tracing handler for [Netty](http://netty.io/) 4.x http servers
-   [okhttp3](https://github.com/openzipkin/brave/blob/master/instrumentation/okhttp3/README.md) - Tracing decorators for [OkHttp](https://github.com/square/okhttp) 3.x
-   [p6spy](https://github.com/openzipkin/brave/blob/master/instrumentation/p6spy/README.md) - Tracing event listener for [P6Spy](https://github.com/p6spy/p6spy) (a proxy for calls to your JDBC driver)
-   [servlet](https://github.com/openzipkin/brave/blob/master/instrumentation/servlet/README.md) - Tracing filter for Servlet 2.5+ (including Async)
-   [sparkjava](https://github.com/openzipkin/brave/blob/master/instrumentation/sparkjava/README.md) - Tracing filters and exception handlers for [SparkJava](http://sparkjava.com/)
-   [spring-rabbit](https://github.com/openzipkin/brave/blob/master/instrumentation/spring-rabbit/README.md) - Tracing MessagePostProcessor and ListenerAdvice for [Spring Rabbit](https://spring.io/guides/gs/messaging-rabbitmq/)
-   [spring-web](https://github.com/openzipkin/brave/blob/master/instrumentation/spring-web/README.md) - Tracing interceptor for [Spring RestTemplate](https://spring.io/guides/gs/consuming-rest/)
-   [spring-webmvc](https://github.com/openzipkin/brave/blob/master/instrumentation/spring-webmvc/README.md) - Tracing filter and span customizing interceptors for [Spring WebMVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)
-   [vertx-web](https://github.com/openzipkin/brave/blob/master/instrumentation/vertx-web/README.md) - Tracing routing context handler for [Vert.x Web](http://vertx.io/docs/vertx-web/js/)


## 参考链接


##### 标签
#trace #distributed 
