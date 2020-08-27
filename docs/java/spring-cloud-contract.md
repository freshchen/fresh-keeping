# Spring Cloud Contract 微服务契约测试框架

## 简介

### 使用场景

主要用于在微服务架构下做CDC（消费者驱动契约）测试。下图展示了多个微服务的调用，如果我们更改了一个模块要如何进行测试呢？

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/contract-1.png)

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

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/contract-2.png)

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

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/contrct-3.png)

## 简单案例

### 服务提供者

模拟一个股票价格查询的服务

#### 项目地址

[springcloud-contract-provider-rest](https://github.com/freshchen/fresh-java-practice/tree/master/springcloud-contract-provider-rest)

#### 项目结构

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/contract-5.png)

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
      <version>2.2.1.RELEASE</version>
      <extensions>true</extensions>
      <configuration>
        <!--用于构建过程中插件自动生成测试用例的基类-->
        <baseClassForTests>
          com.example.springcloudcontractproviderrest.RestBaseCase
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

既然是消费者驱动契约，我么首先需要制定契约，这里为了方便假设查询贵州茅台的股价返回值是固定的999，也可以通过正则等方式去限制返回值

```groovy
Contract.make {
    description "query by id should return stock(id,price)"

    request {
        method GET()
        url value {
            // 消费者使用时请求任何 /stock/price/数字 都会被转为 /stock/price/600519
            consumer regex('/stock/price/\\d+')
            producer "/stock/price/600519"
        }
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        // 提供给消费者的默认返回
        body([
                id   : 600519,
                price: 999
        ])

        // 服务端在测试过程中，body需要满足的规则
        bodyMatchers {
            jsonPath '$.id', byRegex(number())
            jsonPath '$.price', byRegex(number())
        }
    }
}
```

#### 测试基类

主要是加载环境，然后由于不是真实环境模拟了数据库查询

```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class RestBaseCase {

    @Autowired
    private StockController stockController;

    @MockBean
    private StockRepository stockRepository;

    @Before
    public void setup() {
        init();
        RestAssuredMockMvc.standaloneSetup(stockController);
    }

    private void init() {
        Mockito.when(stockRepository.getStockById(600519)).thenReturn(new StockDTO(600519, "贵州茅台", 999L, "SH"));
    }

}
```

#### 实现服务并测试

实现我们的服务功能，具体代码逻辑可以在项目地址中查看，然后测试看是否符合契约

`mvn clean test`

可以在生成（target）目录中找到 generated-test-sources 这个目录，插件为我们自动生成并且运行的case就在其中

```java
public class StockTest extends RestBaseCase {

	@Test
	public void validate_shoudReturnStockIdAndPrice() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/stock/price/600519");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());

		// and:
			assertThat(parsedJson.read("$.id", String.class)).matches("-?(\\d*\\.\\d+|\\d+)");
			assertThat(parsedJson.read("$.price", String.class)).matches("-?(\\d*\\.\\d+|\\d+)");
	}

}
```

#### 发布

如果一切顺利就可以deploy了

### 服务消费者

模拟查询个人资产的服务，需要远程调用股票价格查询服务，计算总资产

#### 项目地址

[springcloud-contract-consumer-rest](https://github.com/freshchen/fresh-java-practice/tree/master/springcloud-contract-consumer-rest)

#### 项目结构

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/contract-6.png)

#### 验证服务

编写测试用例验证服务

```java
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(
        ids = {"com.example:springcloud-contract-provider-rest:+:stubs:8880"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class StockApiTest {

    @Autowired
    private StockApi stockApi;

    @Test
    public void testStockApi() throws IOException {
        StockPriceDTO stockPrice = stockApi.getStockPrice(600519).execute().body();
        BDDAssertions.then(stockPrice.getId()).isEqualTo(600519);
        BDDAssertions.then(stockPrice.getPrice()).isEqualTo(999);

    }
}
```

