package com.github.freshchen.keeping;

import com.github.freshchen.keeping.model.JsonResult;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

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
