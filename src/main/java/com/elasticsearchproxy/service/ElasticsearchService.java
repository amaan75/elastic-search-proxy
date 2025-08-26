package com.elasticsearchproxy.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.elasticsearchproxy.config.ApplicationProperties;
import com.elasticsearchproxy.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

@Service
public class ElasticsearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

    private final ElasticsearchClient elasticsearchClient;
    private final KafkaPublisherService kafkaPublisherService;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;

    public ElasticsearchService(ElasticsearchClient elasticsearchClient,
                               KafkaPublisherService kafkaPublisherService,
                               ApplicationProperties applicationProperties,
                               ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.kafkaPublisherService = kafkaPublisherService;
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<ElasticsearchResponse> indexDocument(IndexRequest indexRequest) {
        logger.info("Processing index request for index: {}, id: {}", indexRequest.getIndex(), indexRequest.getId());

        // Publish to Kafka first (async)
        CompletableFuture<Void> kafkaFuture = kafkaPublisherService.publishElasticsearchOperation(
                "INDEX", indexRequest.getIndex(), indexRequest.getId(), indexRequest);

        try {
            // Build Elasticsearch index request
            co.elastic.clients.elasticsearch.core.IndexRequest.Builder<Object> requestBuilder = 
                    new co.elastic.clients.elasticsearch.core.IndexRequest.Builder<>()
                            .index(indexRequest.getIndex())
                            .document(indexRequest.getDocument());
            
            if (indexRequest.getId() != null) {
                requestBuilder.id(indexRequest.getId());
            }
            
            if (indexRequest.getRouting() != null) {
                requestBuilder.routing(indexRequest.getRouting());
            }
            
            if (indexRequest.getVersion() != null) {
                requestBuilder.version(indexRequest.getVersion());
            }

            // Execute the request
            IndexResponse response = elasticsearchClient.index(requestBuilder.build());
            
            ElasticsearchResponse result = ElasticsearchResponse.success(
                    response.index(),
                    response.id(),
                    response.result().jsonValue(),
                    response.version(),
                    response.result() == Result.Created
            );

            logger.info("Successfully indexed document: {}", result);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            logger.error("Failed to index document for index: {}, id: {}", 
                    indexRequest.getIndex(), indexRequest.getId(), e);
            
            ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                    indexRequest.getIndex(), indexRequest.getId(), e.getMessage());
            
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<ElasticsearchResponse> updateDocument(UpdateRequest updateRequest) {
        logger.info("Processing update request for index: {}, id: {}", updateRequest.getIndex(), updateRequest.getId());

        // Publish to Kafka first (async)
        CompletableFuture<Void> kafkaFuture = kafkaPublisherService.publishElasticsearchOperation(
                "UPDATE", updateRequest.getIndex(), updateRequest.getId(), updateRequest);

        try {
            // Build Elasticsearch update request
            co.elastic.clients.elasticsearch.core.UpdateRequest.Builder<Object, Object> requestBuilder = 
                    new co.elastic.clients.elasticsearch.core.UpdateRequest.Builder<>()
                            .index(updateRequest.getIndex())
                            .id(updateRequest.getId())
                            .doc(updateRequest.getDocument());
            
            if (updateRequest.getDocAsUpsert() != null && updateRequest.getDocAsUpsert()) {
                requestBuilder.docAsUpsert(true);
            }
            
            if (updateRequest.getRouting() != null) {
                requestBuilder.routing(updateRequest.getRouting());
            }
            
            // Note: Version handling removed as new client API doesn't support it directly in update requests

            // Execute the request
            UpdateResponse<Object> response = elasticsearchClient.update(requestBuilder.build(), Object.class);
            
            ElasticsearchResponse result = ElasticsearchResponse.success(
                    response.index(),
                    response.id(),
                    response.result().jsonValue(),
                    response.version(),
                    response.result() == Result.Created
            );

            logger.info("Successfully updated document: {}", result);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            logger.error("Failed to update document for index: {}, id: {}", 
                    updateRequest.getIndex(), updateRequest.getId(), e);
            
            ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                    updateRequest.getIndex(), updateRequest.getId(), e.getMessage());
            
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<ElasticsearchResponse> deleteDocument(DeleteRequest deleteRequest) {
        logger.info("Processing delete request for index: {}, id: {}", deleteRequest.getIndex(), deleteRequest.getId());

        // Publish to Kafka first (async)
        CompletableFuture<Void> kafkaFuture = kafkaPublisherService.publishElasticsearchOperation(
                "DELETE", deleteRequest.getIndex(), deleteRequest.getId(), deleteRequest);

        try {
            // Build Elasticsearch delete request
            co.elastic.clients.elasticsearch.core.DeleteRequest.Builder requestBuilder = 
                    new co.elastic.clients.elasticsearch.core.DeleteRequest.Builder()
                            .index(deleteRequest.getIndex())
                            .id(deleteRequest.getId());
            
            if (deleteRequest.getRouting() != null) {
                requestBuilder.routing(deleteRequest.getRouting());
            }
            
            if (deleteRequest.getVersion() != null) {
                requestBuilder.version(deleteRequest.getVersion());
            }

            // Execute the request
            DeleteResponse response = elasticsearchClient.delete(requestBuilder.build());
            
            ElasticsearchResponse result = ElasticsearchResponse.success(
                    response.index(),
                    response.id(),
                    response.result().jsonValue(),
                    response.version(),
                    false // delete never creates
            );

            logger.info("Successfully deleted document: {}", result);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            logger.error("Failed to delete document for index: {}, id: {}", 
                    deleteRequest.getIndex(), deleteRequest.getId(), e);
            
            ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                    deleteRequest.getIndex(), deleteRequest.getId(), e.getMessage());
            
            return CompletableFuture.completedFuture(errorResponse);
        }
    }
}