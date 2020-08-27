# Spring Cloud Stream微服务消息框架

## 简介

随着近些年微服务在国内的盛行，消息驱动被提到的越来越多。主要原因是系统被拆分成多个模块后，一个业务往往需要在多个服务间相互调用，不管是采用HTTP还是RPC都是同步的，不可避免快等慢的情况发生，系统性能上很容易遇到瓶颈。在这样的背景下，将业务中实时性要求不是特别高且非主干的部分放到消息队列中是很好的选择，达到了异步解耦的效果。

目前消息队列有很多优秀的中间件，目前使用较多的主要有 RabbitMQ，Kafka，RocketMQ 等，这些中间件各有优势，有的对 AMQP（应用层标准高级消息队列协议）支持完善，有的提供了更高的可靠性，有的对大数据支持良好，同时各种消息中间件概念不统一，使得选择和使用一款合适的消息中间件成为难题。Spring跳出来给出了解决方案：Spring Cloud Stream，使用它可以很方便高效的操作消息中间件，程序员只要关心业务代码即可，目前官方支持 RabbitMQ，Kafka两大主流MQ，RocketMQ 则自己提供了相应支持。

首先看一下Spring Cloud Stream做了什么，如下图所示，框架目前官方把消息中间件抽象成了 Binder，业务代码通过进出管道连接 Binder，各消息中间件的差异性统一交给了框架处理，程序员只需要了解框架的抽象出来的一些统一概念即可

- Binder（绑定器）：RabbitMQ，Kafka等中间件服务的封装
- Channel（管道）：也就是图中的 inputs 和 outputs 所指区域，是应用程序和 Binder 的桥梁
- Gourp（消费组）：由于微服务会部署多实例，为了保证只被服务的一个实例消费，可以通过配置，把实例都绑到同一个消费组
- Partitioning （消息分区）：如果某一类消息只想指定给服务的固定实例消费，可以使用分区实现

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/spring-cloud-stream-1.png)

Spring Cloud Stream将业务代码和消息中间件解耦，带来的好处可以从下图很直观的感受到，很简洁的代码，我们便能从RabbitMQ中接受消息然后经过业务处理再向Kafka发送一条消息，只需要更改相关配置就能快速改变系统行为。

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/spring-cloud-stream-2.png)

细心的读者可能会好奇，上图的代码只是注入了一个简单的 Function 而已，实际上，Spring Cloud Stream3.0后集成了Spring Cloud Function框架 ，提倡函数式的风格，弃用先前版本基于注解的开发方式。Spring Cloud Function是 Serverless 和 Faas 的产物，强调面向函数编程，一份代码各云平台运行，和Spring Cloud Stream一样也是解决了基础设施的差异性问题，通过强大的自动装配机制，可以根据配置自动暴露 HTTP 服务或者消息服务，并且同时支持命令式和响应式编程模式，可以说是很强大了。下面通过一个简单的例子来理解下上图的代码和框架的使用把。

## 简单案例

模拟一个简单的下单，收到订单之后处理完，返回成功，然后发送消息给库存模块，库存模块再发送消息给报表模块

#### 项目地址

[springcloud-stream](https://github.com/freshchen/fresh-java-practice/tree/master/springcloud-stream)

#### 项目结构

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/spring-cloud-stream-3.png)

#### 项目依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

#### 表单

```java
@Data
public class OrderForm {
    private String productName;
}
```

#### 消息管道注册

```java
@Configuration
@Slf4j
public class MessageQueueConfig {

    @Bean
    public Function<OrderForm, OrderForm> inventory() {
        return orderForm -> {
            log.info("Inventory Received Message: " + orderForm);
            return orderForm;
        };
    }

    @Bean
    public Consumer<OrderForm> report() {
        return orderForm -> {
            log.info("Report Received Message: " + orderForm);
        };
    }
}
```

#### Controller

```java
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private BeanFactoryChannelResolver resolver;

    @PostMapping("order")
    public String order(@RequestBody OrderForm orderForm) {
        log.info("Received Request " + orderForm);
        resolver.resolveDestination("inventory-in-0").send(new GenericMessage<>(orderForm));
        return "success";
    }
}
```

#### 配置

框架会按照中间件默认端口去连接，这里自定义了一个名为myLocalRabbit的类型是RabbitMQ的Binder配置，bindings下面 inventory-in-0 是通道名，接受inventory主题（对应RabbitMQ的ExChange）的消息，然后处理完通过 inventory-out-0 通道发送消息到 report 主题， report-in-0通道负责接受report主题的消息。

注：通道名=注册的 function 方法名 + in或者out + 参数位置（详见注释）

```yaml
spring:
  cloud:
    stream:
#     配置消息中间件信息
      binders:
        myLocalRabbit:
          type: rabbit
          environment:
            spring:
              rabbitmq:
                host: localhost
                port: 31003
                username: guest
                password: guest
                virtual-host: /
#     重点，如何绑定通道，这里有个约定，开头是函数名，in表示消费消息，out表示生产消息，最后的数字是函数接受的参数的位置，destination后面为订阅的主题
#     比如Function<Tuple2<Flux<String>, Flux<Integer>>, Flux<String>> gather()
#     gather函数接受的第一个String参数对应 gather-in-0，第二个Integer参数对应 gather-in-1，输出对应 gather-out-0
      bindings:
        inventory-in-0:
          destination: inventory
        inventory-out-0:
          destination: report
        report-in-0:
          destination: report
#     注册声明的三个函数
      function:
        definition: inventory;report
```

#### 测试

```http
POST http://localhost:8080/order
Content-Type: application/json

{
  "productName": "999"
}
```

#### 结果

```http
POST http://localhost:8080/order

HTTP/1.1 200 
Content-Type: text/plain;charset=UTF-8
Content-Length: 7
Date: Sat, 30 May 2020 15:27:56 GMT
Keep-Alive: timeout=60
Connection: keep-alive

success

Response code: 200; Time: 56ms; Content length: 7 bytes
```

#### 后台日志

可以看到消息成功发送到了库存和报表服务

```verilog
2020-05-30 23:27:56.956  INFO 8760 --- [nio-8080-exec-1] c.e.springcloudstream.OrderController    : Received Request OrderForm(productName=999)
2020-05-30 23:27:56.956  INFO 8760 --- [nio-8080-exec-1] o.s.i.h.s.MessagingMethodInvokerHelper   : Overriding default instance of MessageHandlerMethodFactory with provided one.
2020-05-30 23:27:56.957  INFO 8760 --- [nio-8080-exec-1] c.e.s.MessageQueueConfig                 : Inventory Received Message: OrderForm(productName=999)
2020-05-30 23:27:56.958  INFO 8760 --- [nio-8080-exec-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:31003]
2020-05-30 23:27:56.964  INFO 8760 --- [nio-8080-exec-1] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory.publisher#6131841e:0/SimpleConnection@192fe472 [delegate=amqp://guest@127.0.0.1:31003/, localPort= 2672]
2020-05-30 23:27:56.965  INFO 8760 --- [nio-8080-exec-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable, auto-delete, or exclusive Queue (inventory.anonymous.wtaFwHlNRkql5IUh2JCNAA) durable:false, auto-delete:true, exclusive:true. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.
2020-05-30 23:27:56.965  INFO 8760 --- [nio-8080-exec-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable, auto-delete, or exclusive Queue (report.anonymous.SJgpJKiJQf2tudszgf623w) durable:false, auto-delete:true, exclusive:true. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.
2020-05-30 23:27:56.979  INFO 8760 --- [f2tudszgf623w-1] o.s.i.h.s.MessagingMethodInvokerHelper   : Overriding default instance of MessageHandlerMethodFactory with provided one.
2020-05-30 23:27:56.980  INFO 8760 --- [f2tudszgf623w-1] c.e.s.MessageQueueConfig                 : Report Received Message: OrderForm(productName=999)
```

