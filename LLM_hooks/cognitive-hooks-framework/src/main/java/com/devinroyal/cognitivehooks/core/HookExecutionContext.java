/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

/**
 * Runtime container for resources a hook might need: LLM client, DB connection,
 * security context, metrics, audit logging, configuration, etc.
 */
public final class HookExecutionContext {

    private final LlmClient llmClient;
    private final SecurityContext securityContext;
    private final MetricsRecorder metricsRecorder;
    private final AuditLogger auditLogger;
    private final Connection databaseConnection;
    private final Map<String, Object> config;

    public HookExecutionContext(LlmClient llmClient,
                                SecurityContext securityContext,
                                MetricsRecorder metricsRecorder,
                                AuditLogger auditLogger,
                                Connection databaseConnection,
                                Map<String, Object> config) {
        this.llmClient = llmClient;
        this.securityContext = Objects.requireNonNull(securityContext, "securityContext must not be null");
        this.metricsRecorder = metricsRecorder;
        this.auditLogger = auditLogger;
        this.databaseConnection = databaseConnection;
        this.config = config;
    }

    public LlmClient getLlmClient() {
        return llmClient;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public MetricsRecorder getMetricsRecorder() {
        return metricsRecorder;
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String key, Class<T> type) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Config key '" + key + "' is not of type " + type.getName());
        }
        return (T) value;
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
