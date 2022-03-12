package com.github.freshchen.keeping;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.propagation.TraceIdContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author darcy
 * @since 2022/01/30
 **/
@RestController
@Slf4j
public class TraceController {

    @Autowired
    private HttpClientBuilder httpClientBuilder;

    @Autowired
    private Tracer tracer;

    @GetMapping("/test1")
    public String test1() throws IOException {
        CloseableHttpClient client = httpClientBuilder.build();
        CloseableHttpResponse response = client.execute(new HttpGet("http://localhost:8080/test2"));

        TraceIdContext traceIdContext = TraceIdContext.newBuilder().traceId(1L).sampled(true).build();
        TraceContextOrSamplingFlags traceContextOrSamplingFlags = TraceContextOrSamplingFlags.create(traceIdContext);
        Span span = tracer.nextSpan(traceContextOrSamplingFlags);
        log.info("test1");
        return "test1";
    }

    @GetMapping("test2")
    public String test2() {
        return "test2";
    }

}
