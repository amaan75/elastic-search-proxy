package com.elasticsearchproxy.controller;

import com.elasticsearchproxy.model.IndexRequest;
import com.elasticsearchproxy.service.ElasticsearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ElasticsearchController.class)
@TestPropertySource(properties = {
    "spring.elasticsearch.uris=http://localhost:9200",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "elasticsearch-proxy.kafka.enable-publishing=false"
})
public class ElasticsearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElasticsearchService elasticsearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/elasticsearch/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Elasticsearch Proxy Service is running"));
    }

    @Test
    public void testIndexDocumentEndpoint() throws Exception {
        // Arrange
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.setIndex("test-index");
        indexRequest.setId("test-id");
        
        Map<String, Object> document = new HashMap<>();
        document.put("title", "Test Document");
        document.put("content", "This is a test document");
        indexRequest.setDocument(document);

        com.elasticsearchproxy.model.ElasticsearchResponse mockResponse = 
            com.elasticsearchproxy.model.ElasticsearchResponse.success(
                "test-index", "test-id", "CREATED", 1L, true);

        when(elasticsearchService.indexDocument(any(IndexRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act & Assert
        mockMvc.perform(post("/api/elasticsearch/index")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(indexRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.index").value("test-index"))
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.result").value("CREATED"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testIndexDocumentValidation() throws Exception {
        // Test with invalid request (missing index)
        IndexRequest invalidRequest = new IndexRequest();
        invalidRequest.setId("test-id");
        
        Map<String, Object> document = new HashMap<>();
        document.put("title", "Test Document");
        invalidRequest.setDocument(document);

        mockMvc.perform(post("/api/elasticsearch/index")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("VALIDATION_ERROR"));
    }
}