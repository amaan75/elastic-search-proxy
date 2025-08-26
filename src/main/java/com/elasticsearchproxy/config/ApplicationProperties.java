package com.elasticsearchproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch-proxy")
public class ApplicationProperties {

    private final Kafka kafka = new Kafka();
    private final Elasticsearch elasticsearch = new Elasticsearch();

    public Kafka getKafka() {
        return kafka;
    }

    public Elasticsearch getElasticsearch() {
        return elasticsearch;
    }

    public static class Kafka {
        private String topic = "elasticsearch-write-operations";
        private boolean enablePublishing = true;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public boolean isEnablePublishing() {
            return enablePublishing;
        }

        public void setEnablePublishing(boolean enablePublishing) {
            this.enablePublishing = enablePublishing;
        }
    }

    public static class Elasticsearch {
        private String defaultIndexPrefix = "app";
        private int maxRetryAttempts = 3;
        private long retryDelayMs = 1000;

        public String getDefaultIndexPrefix() {
            return defaultIndexPrefix;
        }

        public void setDefaultIndexPrefix(String defaultIndexPrefix) {
            this.defaultIndexPrefix = defaultIndexPrefix;
        }

        public int getMaxRetryAttempts() {
            return maxRetryAttempts;
        }

        public void setMaxRetryAttempts(int maxRetryAttempts) {
            this.maxRetryAttempts = maxRetryAttempts;
        }

        public long getRetryDelayMs() {
            return retryDelayMs;
        }

        public void setRetryDelayMs(long retryDelayMs) {
            this.retryDelayMs = retryDelayMs;
        }
    }
}