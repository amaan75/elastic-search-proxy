package com.elasticsearchproxy.controller;

import com.elasticsearchproxy.model.*;
import com.elasticsearchproxy.service.ElasticsearchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/elasticsearch")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ElasticsearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchController.class);

    private final ElasticsearchService elasticsearchService;

    public ElasticsearchController(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @PostMapping("/index")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> indexDocument(
            @Valid @RequestBody IndexRequest indexRequest) {
        
        logger.info("Received index request for index: {}, id: {}", indexRequest.getIndex(), indexRequest.getId());

        return elasticsearchService.indexDocument(indexRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Unexpected error during index operation", throwable);
                    ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                            indexRequest.getIndex(), indexRequest.getId(), "Internal server error");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @PostMapping("/{index}/_doc")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> indexDocumentByPath(
            @PathVariable String index,
            @RequestBody java.util.Map<String, Object> document) {
        
        logger.info("Received index request for index: {} with auto-generated ID", index);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.setIndex(index);
        indexRequest.setDocument(document);

        return elasticsearchService.indexDocument(indexRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
    }

    @PostMapping("/{index}/_doc/{id}")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> indexDocumentWithId(
            @PathVariable String index,
            @PathVariable String id,
            @RequestBody java.util.Map<String, Object> document) {
        
        logger.info("Received index request for index: {}, id: {}", index, id);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.setIndex(index);
        indexRequest.setId(id);
        indexRequest.setDocument(document);

        return elasticsearchService.indexDocument(indexRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
    }

    @PostMapping("/update")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> updateDocument(
            @Valid @RequestBody UpdateRequest updateRequest) {
        
        logger.info("Received update request for index: {}, id: {}", updateRequest.getIndex(), updateRequest.getId());

        return elasticsearchService.updateDocument(updateRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Unexpected error during update operation", throwable);
                    ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                            updateRequest.getIndex(), updateRequest.getId(), "Internal server error");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @PostMapping("/{index}/_update/{id}")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> updateDocumentByPath(
            @PathVariable String index,
            @PathVariable String id,
            @RequestBody java.util.Map<String, Object> document) {
        
        logger.info("Received update request for index: {}, id: {}", index, id);

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setIndex(index);
        updateRequest.setId(id);
        updateRequest.setDocument(document);

        return elasticsearchService.updateDocument(updateRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.ok(response);
                });
    }

    @PostMapping("/delete")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> deleteDocument(
            @Valid @RequestBody DeleteRequest deleteRequest) {
        
        logger.info("Received delete request for index: {}, id: {}", deleteRequest.getIndex(), deleteRequest.getId());

        return elasticsearchService.deleteDocument(deleteRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Unexpected error during delete operation", throwable);
                    ElasticsearchResponse errorResponse = ElasticsearchResponse.error(
                            deleteRequest.getIndex(), deleteRequest.getId(), "Internal server error");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @DeleteMapping("/{index}/_doc/{id}")
    public CompletableFuture<ResponseEntity<ElasticsearchResponse>> deleteDocumentByPath(
            @PathVariable String index,
            @PathVariable String id) {
        
        logger.info("Received delete request for index: {}, id: {}", index, id);

        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIndex(index);
        deleteRequest.setId(id);

        return elasticsearchService.deleteDocument(deleteRequest)
                .thenApply(response -> {
                    if ("ERROR".equals(response.getStatus())) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.ok(response);
                });
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Elasticsearch Proxy Service is running");
    }
}