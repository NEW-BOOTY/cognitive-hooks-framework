/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable request passed into a hook.
 * Encapsulates the LLM prompt, user identity, metadata, and arbitrary payload.
 */
public final class HookRequest {

    private final String requestId;
    private final String userId;
    private final String prompt;
    private final Map<String, Object> payload;
    private final Instant timestamp;

    public HookRequest(String requestId,
                       String userId,
                       String prompt,
                       Map<String, Object> payload,
                       Instant timestamp) {
        this.requestId = Objects.requireNonNull(requestId, "requestId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.prompt = Objects.requireNonNull(prompt, "prompt must not be null");
        this.payload = payload == null ? Collections.emptyMap() : Collections.unmodifiableMap(payload);
        this.timestamp = timestamp == null ? Instant.now() : timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPrompt() {
        return prompt;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "HookRequest{" +
                "requestId='" + requestId + '\'' +
                ", userId='" + userId + '\'' +
                ", prompt='" + prompt + '\'' +
                ", payload=" + payload +
                ", timestamp=" + timestamp +
                '}';
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
