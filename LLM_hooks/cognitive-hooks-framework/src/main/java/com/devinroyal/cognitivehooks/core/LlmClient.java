/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.util.Map;

/**
 * Abstract client wrapper for any LLM provider. This keeps the framework
 * decoupled from specific vendors (OpenAI, Azure, local models, etc.).
 */
public interface LlmClient {

    /**
     * Sends a prompt plus optional context to an LLM and returns the response text.
     * Implementations should perform their own error handling and throw
     * a HookException when unrecoverable.
     */
    String complete(String prompt, Map<String, Object> context) throws HookException;
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
