/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.hooks.reasoning;

import com.devinroyal.cognitivehooks.core.Hook;
import com.devinroyal.cognitivehooks.core.HookExecutionContext;
import com.devinroyal.cognitivehooks.core.HookException;
import com.devinroyal.cognitivehooks.core.HookRequest;
import com.devinroyal.cognitivehooks.core.HookResult;
import com.devinroyal.cognitivehooks.core.HookStatus;
import com.devinroyal.cognitivehooks.core.HookType;
import com.devinroyal.cognitivehooks.core.LlmClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Augmented reasoning hook: asks the LLM to verify its own statements
 * against an independent "fact-check" style prompt.
 */
public class FactCheckHook implements Hook {

    private static final String ID = "fact-check-hook";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public HookType getType() {
        return HookType.REASONING;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("fact-check");
        tags.add("augmented-reasoning");
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean supports(HookRequest request, HookExecutionContext ctx) {
        return request.getPrompt() != null && !request.getPrompt().isBlank();
    }

    @Override
    public HookResult execute(HookRequest request, HookExecutionContext ctx) throws HookException {
        Instant start = Instant.now();
        LlmClient llm = ctx.getLlmClient();
        if (llm == null) {
            throw new HookException("No LlmClient configured for FactCheckHook");
        }

        String factCheckPrompt = "You are a strict fact-checking agent. " +
                "Given the following user query, identify claims that are likely incorrect " +
                "or require citations. Respond in JSON with fields 'risky_claims' and 'notes'.\n\n" +
                "USER QUERY:\n" + request.getPrompt();

        String response = llm.complete(factCheckPrompt, null);

        Duration latency = Duration.between(start, Instant.now());
        return HookResult.builder(getId(), getType(), HookStatus.SUCCESS)
                .message("Fact check completed")
                .data(response)
                .latency(latency)
                .build();
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
