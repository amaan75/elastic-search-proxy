package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteRequest {

    @NotBlank(message = "Index name is required")
    private String index;

    @NotBlank(message = "Document ID is required")
    private String id;

    private String routing;

    private Long version;

    private String versionType;

    private LocalDateTime timestamp;

    public DeleteRequest() {
        this.timestamp = LocalDateTime.now();
    }

    public DeleteRequest(String index, String id) {
        this();
        this.index = index;
        this.id = id;
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

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DeleteRequest{" +
                "index='" + index + '\'' +
                ", id='" + id + '\'' +
                ", routing='" + routing + '\'' +
                ", version=" + version +
                ", versionType='" + versionType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}