/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.util.Set;

/**
 * Primary contract for all hooks. Hooks are modular, attachable capabilities
 * that can be discovered and orchestrated at runtime.
 */
public interface Hook {

    String getId();

    HookType getType();

    /**
     * Arbitrary tags (e.g., "fact-check", "db-query", "vision", "compliance").
     */
    Set<String> getTags();

    /**
     * Lightweight check to see whether this hook wants to handle the given request.
     */
    boolean supports(HookRequest request, HookExecutionContext ctx);

    /**
     * Execute hook logic. Must never throw unhandled runtime errors;
     * instead, wrap failures in HookException to allow resilience layer
     * to apply retries, circuit breakers, etc.
     */
    HookResult execute(HookRequest request, HookExecutionContext ctx) throws HookException;
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
