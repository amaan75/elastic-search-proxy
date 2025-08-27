package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkResponse {

    private String status;
    private Integer totalRequests;
    private Integer successfulRequests;
    private Integer failedRequests;
    private List<ElasticsearchResponse> responses;
    private LocalDateTime timestamp;
    private String errorMessage;

    public BulkResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static BulkResponse success(Integer totalRequests, Integer successfulRequests, 
                                     Integer failedRequests, List<ElasticsearchResponse> responses) {
        BulkResponse bulkResponse = new BulkResponse();
        bulkResponse.status = "SUCCESS";
        bulkResponse.totalRequests = totalRequests;
        bulkResponse.successfulRequests = successfulRequests;
        bulkResponse.failedRequests = failedRequests;
        bulkResponse.responses = responses;
        return bulkResponse;
    }

    public static BulkResponse error(String errorMessage) {
        BulkResponse bulkResponse = new BulkResponse();
        bulkResponse.status = "ERROR";
        bulkResponse.errorMessage = errorMessage;
        return bulkResponse;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getSuccessfulRequests() {
        return successfulRequests;
    }

    public void setSuccessfulRequests(Integer successfulRequests) {
        this.successfulRequests = successfulRequests;
    }

    public Integer getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(Integer failedRequests) {
        this.failedRequests = failedRequests;
    }

    public List<ElasticsearchResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<ElasticsearchResponse> responses) {
        this.responses = responses;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "BulkResponse{" +
                "status='" + status + '\'' +
                ", totalRequests=" + totalRequests +
                ", successfulRequests=" + successfulRequests +
                ", failedRequests=" + failedRequests +
                ", timestamp=" + timestamp +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}