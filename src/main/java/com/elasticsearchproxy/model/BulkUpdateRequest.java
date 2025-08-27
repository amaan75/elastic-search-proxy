package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkUpdateRequest {

    @NotEmpty(message = "Update requests list cannot be empty")
    @Valid
    private List<UpdateRequest> requests;

    private LocalDateTime timestamp;

    public BulkUpdateRequest() {
        this.timestamp = LocalDateTime.now();
    }

    public BulkUpdateRequest(List<UpdateRequest> requests) {
        this();
        this.requests = requests;
    }

    public List<UpdateRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<UpdateRequest> requests) {
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
        return "BulkUpdateRequest{" +
                "requests=" + (requests != null ? requests.size() + " requests" : "null") +
                ", timestamp=" + timestamp +
                '}';
    }
}