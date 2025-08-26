package com.elasticsearchproxy.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Arrays;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String[] elasticsearchUris;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Bean
    public RestClient restClient() {
        HttpHost[] httpHosts = Arrays.stream(elasticsearchUris)
                .map(this::createHttpHost)
                .toArray(HttpHost[]::new);

        return RestClient.builder(httpHosts)
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(10000)
                                .setSocketTimeout(30000))
                .build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport() {
        return new RestClientTransport(restClient(), new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(elasticsearchTransport());
    }

    private HttpHost createHttpHost(String uri) {
        URI parsedUri = URI.create(uri);
        return new HttpHost(
                parsedUri.getHost(),
                parsedUri.getPort(),
                parsedUri.getScheme()
        );
    }
}