/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Top-level response returned to the caller after orchestrating hooks.
 * Contains the final content plus trace of hook results for auditability.
 */
public final class HookResponse {

    private final String requestId;
    private final String finalContent;
    private final List<HookResult> hookResults;

    public HookResponse(String requestId,
                        String finalContent,
                        List<HookResult> hookResults) {
        this.requestId = Objects.requireNonNull(requestId, "requestId must not be null");
        this.finalContent = finalContent == null ? "" : finalContent;
        this.hookResults = hookResults == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(hookResults);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getFinalContent() {
        return finalContent;
    }

    public List<HookResult> getHookResults() {
        return hookResults;
    }

    @Override
    public String toString() {
        return "HookResponse{" +
                "requestId='" + requestId + '\'' +
                ", finalContent='" + finalContent + '\'' +
                ", hookResults=" + hookResults +
                '}';
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
