package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkDeleteRequest {

    @NotEmpty(message = "Delete requests list cannot be empty")
    @Valid
    private List<DeleteRequest> requests;

    private LocalDateTime timestamp;

    public BulkDeleteRequest() {
        this.timestamp = LocalDateTime.now();
    }

    public BulkDeleteRequest(List<DeleteRequest> requests) {
        this();
        this.requests = requests;
    }

    public List<DeleteRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<DeleteRequest> requests) {
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
        return "BulkDeleteRequest{" +
                "requests=" + (requests != null ? requests.size() + " requests" : "null") +
                ", timestamp=" + timestamp +
                '}';
    }
}