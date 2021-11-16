---
begin: 2021-11-16
status: done
rating: 1
---

# Spring Cloud Contract 微服务契约测试

## 简介

### 使用场景

主要用于在微服务架构下做CDC（消费者驱动契约）测试。下图展示了多个微服务的调用，如果我们更改了一个模块要如何进行测试呢？

![](image/Pasted%20image%2020211116222409.png)

- 传统的两种测试思路
  - 模拟生产环境部署所有的微服务，然后进行测试
    - 优点
      - 测试结果可信度高
    - 缺点
      - 测试成本太大，装一整套环境耗时，耗力，耗机器
  - Mock其他微服务做端到端的测试
    - 优点
      - 不用装整套产品了，测的也方便快捷
    - 缺点
      - 需要写很多服务的Mock，要维护一大堆不同版本用途的simulate（模拟器），同样耗时耗力

- Spring Cloud Contrct解决思路
  - 每个服务都生产可被验证的 Stub Runner，通过WireMock调用，服务双方签订契约，一方变化就更新自己的Stub，并且测对方的Stub。Stub其实只提供了数据，也就是契约，可以很轻量的模拟服务的请求返回。而Mock可在Stub的基础上增加验证

![](image/Pasted%20image%2020211116222435.png)

### 契约测试流程

- 服务提供者
  - 编写契约，可以用Groovy DSL 脚本也可以用 YAML文件
  - 编写测试基类用于构建过程中插件自动生成测试用例
  - 生成的测试用例会自动运行，这时如果我么提供的服务不能满足契约中的规则就会失败
  - 提供者不断完善功能直到服务满足契约要求
  - 发布Jar包，同时将Stub后缀的jar一同发布
- 服务消费者
  - 对需要依赖外部服务的接口编写测试用例
  - 通过注解指定需要依赖服务的Stub jar包
  - 验证外部服务没有问题

![](image/Pasted%20image%2020211116222447.png)

## 简单案例

### 服务提供者

模拟一个用户服务

#### 项目地址

[cloud-contract-provider-rest](https://github.com/freshchen/fresh-keeping/tree/master/java/test/cloud-contract-provider-rest)

#### 项目依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-contract-verifier</artifactId>
  <scope>test</scope>
</dependency>

<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-contract-maven-plugin</artifactId>
      <extensions>true</extensions>
      <configuration>
        <!--用于构建过程中插件自动生成测试用例的基类-->
        <baseClassForTests>
          com.github.freshchen.keeping.RestBaseCase
        </baseClassForTests>
      </configuration>
    </plugin>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
  </plugins>
</build>
```

#### 编写契约

既然是消费者驱动契约，首先需要制定契约, 告诉消费方能提供哪些 stub，并且生成单元测试验证提供方能不能满足约定的能力

```groovy
Contract.make {
    description "add user"

    request {
        method POST()
        url "/user"
        body([
                age: value(
                        // 消费方想创建任何年龄的用户，都会得到下面的response.body的返回 "success: true"
                        consumer(regex(number())),
                        // 提供方生成的测试会调用接口创建一个年龄20岁的用户
                        producer(20)
                )

        ])
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        // 提供给消费者的默认返回
        body([
                success: true
        ])

        // 提供方在测试过程中，body需要满足的规则
        bodyMatchers {
            // 自定义的模型中有 success 字段，byEquality 可以验证服务端返回json中的 success 是不是 true
            jsonPath '$.success', byEquality()
            // 当然我们也可以自定义校验, 可以在基类中实现 assertIsTrue 方法
            jsonPath '$.success', byCommand('assertIsTrue($it)')
        }
    }
}
```

#### 测试基类

```java
@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class RestBaseCase {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        MockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.standaloneSetup(builder);
    }
    
    protected void assertIsTrue(Object object) {
        Map map = (Map) object;
        assertThat(map.get("success")).isEqualTo(true);
    }

}
```

#### 实现功能

```java
@Data
@ApiModel
public class JsonResult<T> {
    @NonNull
    @ApiModelProperty("是否成功")
    private boolean success;
    
    @ApiModelProperty("响应结果")
    private Optional<T> data = Optional.empty();

    @ApiModelProperty("错误码")
    private Optional<Integer> errCode = Optional.empty();

    @ApiModelProperty("错误消息")
    private Optional<String> errMessage = Optional.empty();

}
```

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public JsonResult create(@RequestBody User user) {
        return JsonResult.ok();
    }

}
```

```java
server.port=8880
```

#### 测试

实现我们的服务功能，具体代码逻辑可以在项目地址中查看，然后测试看是否符合契约

`mvn clean test`

可以在生成（target）目录中找到 generated-test-sources 这个目录，插件为我们自动生成并且运行的case就在其中

```java
public class ContractVerifierTest extends RestBaseCase {

	@Test
	public void validate_addUser() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.body("{\"age\":20}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/user");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());

		// and:
			assertThat(parsedJson.read("$.success", Boolean.class)).isEqualTo(true);
			assertIsTrue(parsedJson.read("$.success"));
	}

}
```

#### 发布

如果一切顺利就可以deploy了，解压发布的 stubs 可以看到定义给消费者的 json

```json
{
  "id" : "737fc339-a9c5-41f4-909a-a783dbc0855f",
  "request" : {
    "url" : "/user",
    "method" : "POST",
    "bodyPatterns" : [ {
      "matchesJsonPath" : "$[?(@.['age'] =~ /-?(\\d*\\.\\d+|\\d+)/)]"
    } ]
  },
  "response" : {
    "status" : 200,
    "body" : "{\"success\":true}",
    "headers" : {
      "Content-Type" : "application/json"
    },
    "transformers" : [ "response-template" ]
  },
  "uuid" : "737fc339-a9c5-41f4-909a-a783dbc0855f"
}
```



### 服务消费者

预约服务，会调用户服务接口

#### 项目地址

[cloud-contract-consumer-rest](https://github.com/freshchen/fresh-keeping/tree/master/java/test/cloud-contract-consumer-rest)

#### 服务调用

服务调用方会去调用 8880 端口，也就是上文的用户服务

```java
public interface UserApi {

    @POST("/user")
    Call<JsonResult> create(@Body User user);
}

public class UserClient {

    public static JsonResult createUser(User user) throws IOException {
        UserApi userApi = new Retrofit.Builder().baseUrl("http://127.0.0.1:8880")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApi.class);
        return userApi.create(user).execute().body();
    }
}
```

#### 验证服务

即使用户服务没有开发完成，得到了 stubs 后，预约即使依赖用户服务接口也可以并行开发并完成测试不被阻塞

```java
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(
        ids = {"com.github.freshchen.keeping:cloud-contract-provider-rest:+:stubs:8880"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class UserClientTest {

    @Test
    public void createUser() throws IOException {
        User user = new User();
        user.setAge(123);
        JsonResult user1 = UserClient.createUser(user);
        BDDAssertions.then(user1.getSuccess()).isEqualTo(true);

    }
}
```

关注一下日志，确认是 stubs  生效了, 可以看到 stub id 和上文 json中 uuid 吻合

```java
2020-12-12 16:09:08.070  INFO 18224 --- [p1001114349-254] WireMock                                 : Request received:
127.0.0.1 - POST /user

Connection: [keep-alive]
User-Agent: [okhttp/3.14.8]
Host: [127.0.0.1:8880]
Accept-Encoding: [gzip]
Content-Length: [11]
Content-Type: [application/json; charset=UTF-8]
{"age":123}


Matched response definition:
{
  "status" : 200,
  "body" : "{\"success\":true}",
  "headers" : {
    "Content-Type" : "application/json"
  },
  "transformers" : [ "response-template" ]
}

Response:
HTTP/1.1 200
Content-Type: [application/json]
Matched-Stub-Id: [737fc339-a9c5-41f4-909a-a783dbc0855f]
```



## 参考链接


##### 标签
#test 
