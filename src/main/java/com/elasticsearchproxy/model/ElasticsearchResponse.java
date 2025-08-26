package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElasticsearchResponse {

    private String index;
    private String id;
    private String result;
    private Long version;
    private Boolean created;
    private String status;
    private String error;
    private LocalDateTime timestamp;

    public ElasticsearchResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ElasticsearchResponse(String index, String id, String result) {
        this();
        this.index = index;
        this.id = id;
        this.result = result;
    }

    // Static factory methods for different response types
    public static ElasticsearchResponse success(String index, String id, String result, Long version, Boolean created) {
        ElasticsearchResponse response = new ElasticsearchResponse(index, id, result);
        response.setVersion(version);
        response.setCreated(created);
        response.setStatus("SUCCESS");
        return response;
    }

    public static ElasticsearchResponse error(String index, String id, String error) {
        ElasticsearchResponse response = new ElasticsearchResponse(index, id, null);
        response.setError(error);
        response.setStatus("ERROR");
        return response;
    }

    // Getters and Setters
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ElasticsearchResponse{" +
                "index='" + index + '\'' +
                ", id='" + id + '\'' +
                ", result='" + result + '\'' +
                ", version=" + version +
                ", created=" + created +
                ", status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}