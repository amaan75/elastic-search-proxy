package com.elasticsearchproxy.service;

import com.elasticsearchproxy.config.ApplicationProperties;
import com.elasticsearchproxy.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ElasticsearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

    private final KafkaPublisherService kafkaPublisherService;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;

    public ElasticsearchService(KafkaPublisherService kafkaPublisherService,
                               ApplicationProperties applicationProperties,
                               ObjectMapper objectMapper) {
        this.kafkaPublisherService = kafkaPublisherService;
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<ElasticsearchResponse> indexDocument(IndexRequest indexRequest) {
        logger.info("Processing index request for index: {}, id: {}", indexRequest.getIndex(), indexRequest.getId());

        // Only publish to Kafka - no actual Elasticsearch operation
        CompletableFuture<Void> kafkaFuture = kafkaPublisherService.publishElasticsearchOperation(
                "INDEX", indexRequest.getIndex(), indexRequest.getId(), indexRequest);

        // Generate a synthetic ID if not provided
        String documentId = indexRequest.getId();
        if (documentId == null) {
            documentId = UUID.randomUUID().toString();
        }

        // Return a synthetic success response immediately
        ElasticsearchResponse result = ElasticsearchResponse.success(
                indexRequest.getIndex(),
                documentId,
                "created", // synthetic result
                1L, // synthetic version
                true // assuming creation
        );

        logger.info("Successfully processed index request (Kafka only): {}", result);
        return CompletableFuture.completedFuture(result);
    }

    public CompletableFuture<ElasticsearchResponse> updateDocument(UpdateRequest updateRequest) {
        logger.info("Processing update request for index: {}, id: {}", updateRequest.getIndex(), updateRequest.getId());

        // Only publish to Kafka - no actual Elasticsearch operation
        CompletableFuture<Void> kafkaFuture = kafkaPublisherService.publishElasticsearchOperation(
                "UPDATE", updateRequest.getIndex(), updateRequest.getId(), updateRequest);

        // Return a synthetic success response immediately
        ElasticsearchResponse result = ElasticsearchResponse.success(
                updateRequest.getIndex(),
                updateRequest.getId(),
                "updated", // synthetic result
                2L, // synthetic incremented version
                false // update never creates
        );

        logger.info("Successfully processed update request (Kafka only): {}", result);
        return CompletableFuture.completedFuture(result);
    }

    public CompletableFuture<ElasticsearchResponse> deleteDocument(DeleteRequest deleteRequest) {
        logger.info("Processing delete request for index: {}, id: {}", deleteRequest.getIndex(), deleteRequest.getId());

        // Only publish to Kafka - no actual Elasticsearch operation
        CompletableFuture<Void> kafkaFuture = kafkaPublisherService.publishElasticsearchOperation(
                "DELETE", deleteRequest.getIndex(), deleteRequest.getId(), deleteRequest);

        // Return a synthetic success response immediately
        ElasticsearchResponse result = ElasticsearchResponse.success(
                deleteRequest.getIndex(),
                deleteRequest.getId(),
                "deleted", // synthetic result
                1L, // synthetic version
                false // delete never creates
        );

        logger.info("Successfully processed delete request (Kafka only): {}", result);
        return CompletableFuture.completedFuture(result);
    }

    // Bulk operations methods
    public CompletableFuture<BulkResponse> bulkIndex(BulkIndexRequest bulkRequest) {
        logger.info("Processing bulk index request with {} documents", bulkRequest.getRequests().size());

        List<ElasticsearchResponse> responses = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (IndexRequest indexRequest : bulkRequest.getRequests()) {
            try {
                // Publish each request to Kafka
                kafkaPublisherService.publishElasticsearchOperation(
                        "BULK_INDEX", indexRequest.getIndex(), indexRequest.getId(), indexRequest);

                // Generate synthetic ID if needed
                String documentId = indexRequest.getId();
                if (documentId == null) {
                    documentId = UUID.randomUUID().toString();
                }

                // Create synthetic response
                ElasticsearchResponse response = ElasticsearchResponse.success(
                        indexRequest.getIndex(),
                        documentId,
                        "created",
                        1L,
                        true
                );
                responses.add(response);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to process index request in bulk: {}", indexRequest, e);
                ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                        indexRequest.getIndex(), indexRequest.getId(), e.getMessage());
                responses.add(errorResponse);
                failCount++;
            }
        }

        BulkResponse bulkResponse = BulkResponse.success(
                bulkRequest.getRequests().size(),
                successCount,
                failCount,
                responses
        );

        logger.info("Successfully processed bulk index request (Kafka only): {} total, {} success, {} failed", 
                bulkRequest.getRequests().size(), successCount, failCount);
        
        return CompletableFuture.completedFuture(bulkResponse);
    }

    public CompletableFuture<BulkResponse> bulkUpdate(BulkUpdateRequest bulkRequest) {
        logger.info("Processing bulk update request with {} documents", bulkRequest.getRequests().size());

        List<ElasticsearchResponse> responses = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (UpdateRequest updateRequest : bulkRequest.getRequests()) {
            try {
                // Publish each request to Kafka
                kafkaPublisherService.publishElasticsearchOperation(
                        "BULK_UPDATE", updateRequest.getIndex(), updateRequest.getId(), updateRequest);

                // Create synthetic response
                ElasticsearchResponse response = ElasticsearchResponse.success(
                        updateRequest.getIndex(),
                        updateRequest.getId(),
                        "updated",
                        2L,
                        false
                );
                responses.add(response);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to process update request in bulk: {}", updateRequest, e);
                ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                        updateRequest.getIndex(), updateRequest.getId(), e.getMessage());
                responses.add(errorResponse);
                failCount++;
            }
        }

        BulkResponse bulkResponse = BulkResponse.success(
                bulkRequest.getRequests().size(),
                successCount,
                failCount,
                responses
        );

        logger.info("Successfully processed bulk update request (Kafka only): {} total, {} success, {} failed", 
                bulkRequest.getRequests().size(), successCount, failCount);
        
        return CompletableFuture.completedFuture(bulkResponse);
    }

    public CompletableFuture<BulkResponse> bulkDelete(BulkDeleteRequest bulkRequest) {
        logger.info("Processing bulk delete request with {} documents", bulkRequest.getRequests().size());

        List<ElasticsearchResponse> responses = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (DeleteRequest deleteRequest : bulkRequest.getRequests()) {
            try {
                // Publish each request to Kafka
                kafkaPublisherService.publishElasticsearchOperation(
                        "BULK_DELETE", deleteRequest.getIndex(), deleteRequest.getId(), deleteRequest);

                // Create synthetic response
                ElasticsearchResponse response = ElasticsearchResponse.success(
                        deleteRequest.getIndex(),
                        deleteRequest.getId(),
                        "deleted",
                        1L,
                        false
                );
                responses.add(response);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to process delete request in bulk: {}", deleteRequest, e);
                ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                        deleteRequest.getIndex(), deleteRequest.getId(), e.getMessage());
                responses.add(errorResponse);
                failCount++;
            }
        }

        BulkResponse bulkResponse = BulkResponse.success(
                bulkRequest.getRequests().size(),
                successCount,
                failCount,
                responses
        );

        logger.info("Successfully processed bulk delete request (Kafka only): {} total, {} success, {} failed", 
                bulkRequest.getRequests().size(), successCount, failCount);
        
        return CompletableFuture.completedFuture(bulkResponse);
    }
}