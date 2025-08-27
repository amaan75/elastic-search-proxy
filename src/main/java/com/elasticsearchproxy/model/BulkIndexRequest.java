package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkIndexRequest {

    @NotEmpty(message = "Index requests list cannot be empty")
    @Valid
    private List<IndexRequest> requests;

    private LocalDateTime timestamp;

    public BulkIndexRequest() {
        this.timestamp = LocalDateTime.now();
    }

    public BulkIndexRequest(List<IndexRequest> requests) {
        this();
        this.requests = requests;
    }

    public List<IndexRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<IndexRequest> requests) {
        this.requests = requests;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BulkIndexRequest{" +
                "requests=" + (requests != null ? requests.size() + " requests" : "null") +
                ", timestamp=" + timestamp +
                '}';
    }
}