package com.elasticsearchproxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.elasticsearch.uris=http://localhost:9200",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "elasticsearch-proxy.kafka.enable-publishing=false"
})
class ElasticSearchProxyApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // with virtual threads enabled
    }

}