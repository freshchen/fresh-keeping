package com.github.freshchen.keeping;


import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class RestBaseCase {

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        MockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.standaloneSetup(builder);
    }

}
