/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Structured result produced by a single hook execution.
 */
public final class HookResult {

    private final String hookId;
    private final HookType hookType;
    private final HookStatus status;
    private final String message;
    private final Object data;
    private final Duration latency;
    private final Map<String, Object> diagnostics;

    private HookResult(Builder builder) {
        this.hookId = Objects.requireNonNull(builder.hookId, "hookId must not be null");
        this.hookType = Objects.requireNonNull(builder.hookType, "hookType must not be null");
        this.status = Objects.requireNonNull(builder.status, "status must not be null");
        this.message = builder.message;
        this.data = builder.data;
        this.latency = builder.latency == null ? Duration.ZERO : builder.latency;
        this.diagnostics = builder.diagnostics == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(builder.diagnostics);
    }

    public String getHookId() {
        return hookId;
    }

    public HookType getHookType() {
        return hookType;
    }

    public HookStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Duration getLatency() {
        return latency;
    }

    public Map<String, Object> getDiagnostics() {
        return diagnostics;
    }

    @Override
    public String toString() {
        return "HookResult{" +
                "hookId='" + hookId + '\'' +
                ", hookType=" + hookType +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", latency=" + latency +
                ", diagnostics=" + diagnostics +
                '}';
    }

    public static Builder builder(String hookId, HookType hookType, HookStatus status) {
        return new Builder(hookId, hookType, status);
    }

    public static final class Builder {
        private final String hookId;
        private final HookType hookType;
        private final HookStatus status;
        private String message;
        private Object data;
        private Duration latency;
        private Map<String, Object> diagnostics;

        private Builder(String hookId, HookType hookType, HookStatus status) {
            this.hookId = hookId;
            this.hookType = hookType;
            this.status = status;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder latency(Duration latency) {
            this.latency = latency;
            return this;
        }

        public Builder diagnostics(Map<String, Object> diagnostics) {
            this.diagnostics = diagnostics;
            return this;
        }

        public HookResult build() {
            return new HookResult(this);
        }
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
