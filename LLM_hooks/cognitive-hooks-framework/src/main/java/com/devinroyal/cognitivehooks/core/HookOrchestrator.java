/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import com.devinroyal.cognitivehooks.policy.HookPolicyEngine;
import com.devinroyal.cognitivehooks.policy.PolicyDecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates which hooks run, in what order, and how their outputs combine.
 * This is the "cognitive OS" router that can apply compliance policies
 * and on-demand capabilities.
 */
public final class HookOrchestrator {

    private final HookRegistry registry;
    private final ResilientHookExecutor executor;
    private final HookPolicyEngine policyEngine;
    private final LlmClient llmClient;

    public HookOrchestrator(HookRegistry registry,
                            ResilientHookExecutor executor,
                            HookPolicyEngine policyEngine,
                            LlmClient llmClient) {
        this.registry = Objects.requireNonNull(registry, "registry must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
        this.policyEngine = Objects.requireNonNull(policyEngine, "policyEngine must not be null");
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
    }

    /**
     * Very simple orchestration pipeline:
     *
     * 1. Run SAFEGUARD hooks first (pre-processing).
     * 2. Run REASONING + ENTERPRISE + MULTIMODAL hooks as needed.
     * 3. Call LLM for final synthesis, including hook outputs as context.
     */
    public HookResponse handle(HookRequest request, HookExecutionContext ctx) {
        List<HookResult> allResults = new ArrayList<>();

        // 1. SAFEGUARDS
        for (Hook hook : registry.listByType(HookType.SAFEGUARD)) {
            PolicyDecision decision = policyEngine.evaluate(hook, request, ctx);
            if (!decision.allowed()) {
                allResults.add(deniedResult(hook, decision.reason()));
                continue;
            }
            if (!hook.supports(request, ctx)) {
                allResults.add(skippedResult(hook, "Hook does not support this request"));
                continue;
            }
            HookResult result = executor.execute(hook, request, ctx);
            allResults.add(result);
            if (result.getStatus() == HookStatus.FAILED) {
                // In a strict environment, we might abort here. For demo we continue.
            }
        }

        // 2. CORE CAPABILITIES
        for (HookType type : new HookType[]{HookType.REASONING, HookType.ENTERPRISE, HookType.MULTIMODAL}) {
            for (Hook hook : registry.listByType(type)) {
                PolicyDecision decision = policyEngine.evaluate(hook, request, ctx);
                if (!decision.allowed()) {
                    allResults.add(deniedResult(hook, decision.reason()));
                    continue;
                }
                if (!hook.supports(request, ctx)) {
                    allResults.add(skippedResult(hook, "Hook does not support this request"));
                    continue;
                }
                HookResult result = executor.execute(hook, request, ctx);
                allResults.add(result);
            }
        }

        // 3. Synthesize via LLM with hook results as context
        String finalContent;
        try {
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("You are a modular cognitive OS. Use the following hook outputs:\n");
            for (HookResult result : allResults) {
                contextBuilder.append("- Hook ")
                        .append(result.getHookId())
                        .append(" [")
                        .append(result.getStatus())
                        .append("]: ")
                        .append(result.getMessage())
                        .append("\n");
            }
            String combinedPrompt = request.getPrompt() + "\n\n" + contextBuilder;
            finalContent = llmClient.complete(combinedPrompt, null);
        } catch (HookException e) {
            finalContent = "Failed to synthesize final content via LLM: " + e.getMessage();
        }

        return new HookResponse(request.getRequestId(), finalContent, allResults);
    }

    private HookResult deniedResult(Hook hook, String reason) {
        return HookResult.builder(hook.getId(), hook.getType(), HookStatus.SKIPPED)
                .message("Denied by policy: " + reason)
                .build();
    }

    private HookResult skippedResult(Hook hook, String reason) {
        return HookResult.builder(hook.getId(), hook.getType(), HookStatus.SKIPPED)
                .message(reason)
                .build();
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
