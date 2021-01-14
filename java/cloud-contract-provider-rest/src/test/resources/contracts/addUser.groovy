package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "add user"

    request {
        method POST()
        headers {
            contentType applicationJson()
        }
        url "/user"
        body([
                age: value(
                        consumer(regex(number())),
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

        // 服务端在测试过程中，body需要满足的规则
        bodyMatchers {
            jsonPath '$.success', byType()
        }
    }
}

