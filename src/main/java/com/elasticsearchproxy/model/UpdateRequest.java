package com.elasticsearchproxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRequest {

    @NotBlank(message = "Index name is required")
    private String index;

    @NotBlank(message = "Document ID is required")
    private String id;

    @NotNull(message = "Document data is required")
    private Map<String, Object> document;

    private Boolean docAsUpsert;

    private String routing;

    private Long version;

    private String versionType;

    private LocalDateTime timestamp;

    public UpdateRequest() {
        this.timestamp = LocalDateTime.now();
    }

    public UpdateRequest(String index, String id, Map<String, Object> document) {
        this();
        this.index = index;
        this.id = id;
        this.document = document;
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

    public Map<String, Object> getDocument() {
        return document;
    }

    public void setDocument(Map<String, Object> document) {
        this.document = document;
    }

    public Boolean getDocAsUpsert() {
        return docAsUpsert;
    }

    public void setDocAsUpsert(Boolean docAsUpsert) {
        this.docAsUpsert = docAsUpsert;
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
        return "UpdateRequest{" +
                "index='" + index + '\'' +
                ", id='" + id + '\'' +
                ", document=" + document +
                ", docAsUpsert=" + docAsUpsert +
                ", routing='" + routing + '\'' +
                ", version=" + version +
                ", versionType='" + versionType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}