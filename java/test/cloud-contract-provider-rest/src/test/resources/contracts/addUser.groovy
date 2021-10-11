package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "预约签到契约测试"
    request {
        method POST()
        headers {contentType applicationJson()}
        url "/api/booking/checkin"
        // 服务提供方生成的测试用-1作为默认参数，消费方的任何数字请求都会处方Stub提供的默认结果
        body([bookingId: value(consumer(regex(number())), producer(-1))])
    }
    response {
        status OK()
        headers {contentType applicationJson()}
        // 提供给消费者Stub的默认返回
        body([success: true])
        // 服务端在测试过程中，body需要满足的规则
        bodyMatchers {
            jsonPath '$.success', byType()
        }
    }
}

