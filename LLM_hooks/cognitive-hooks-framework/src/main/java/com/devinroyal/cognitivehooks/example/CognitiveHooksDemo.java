/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.example;

import com.devinroyal.cognitivehooks.core.*;
import com.devinroyal.cognitivehooks.hooks.enterprise.DatabaseQueryHook;
import com.devinroyal.cognitivehooks.hooks.multimodal.VisionAnalysisHook;
import com.devinroyal.cognitivehooks.hooks.reasoning.FactCheckHook;
import com.devinroyal.cognitivehooks.hooks.reasoning.SimulationHook;
import com.devinroyal.cognitivehooks.policy.HookPolicyEngine;
import com.devinroyal.cognitivehooks.safeguards.BiasFilterHook;

import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Simple command-line demo showing how to wire up the framework into
 * a cognitive OS style pipeline.
 */
public class CognitiveHooksDemo {

    public static void main(String[] args) {
        // 1. Build core services
        HookRegistry registry = new HookRegistry();
        MetricsRecorder metricsRecorder = new ConsoleMetricsRecorder();
        ResilientHookExecutor executor = new ResilientHookExecutor(
                2, // max retries
                3, // failure threshold
                Duration.ofSeconds(10),
                metricsRecorder
        );
        HookPolicyEngine policyEngine = new HookPolicyEngine();
        LlmClient llmClient = new EchoLlmClient();

        // 2. Register hooks
        registry.register(new BiasFilterHook());
        registry.register(new FactCheckHook());
        registry.register(new SimulationHook());
        registry.register(new DatabaseQueryHook());
        registry.register(new VisionAnalysisHook());

        // 3. Minimal security context: user has enterprise and simulation scopes
        Set<String> roles = Set.of("user");
        Set<String> scopes = Set.of("enterprise:hooks");
        SecurityContext securityContext = new SecurityContext("devin.royal", roles, scopes);

        // 4. Execution context: we do not configure a real DB for demo
        HookExecutionContext ctx = new HookExecutionContext(
                llmClient,
                securityContext,
                metricsRecorder,
                new ConsoleAuditLogger(),
                null, // databaseConnection
                Map.of("environment", "demo")
        );

        HookOrchestrator orchestrator = new HookOrchestrator(registry, executor, policyEngine, llmClient);

        // 5. Build a sample request
        Map<String, Object> payload = new HashMap<>();
        payload.put("simulationSteps", 100);
        payload.put("imageUri", "file:///Users/devin/Desktop/myimage.png");

        HookRequest request = new HookRequest(
                UUID.randomUUID().toString(),
                securityContext.getUserId(),
                "\"Fact-check this and run a 100-step simulation, then analyze image safety.\"\n",
                payload,
                Instant.now()
        );

        // 6. Run orchestration
        HookResponse response = orchestrator.handle(request, ctx);

        System.out.println("=== FINAL RESPONSE ===");
        System.out.println(response.getFinalContent());
        System.out.println();
        System.out.println("=== HOOK TRACE ===");
        for (HookResult result : response.getHookResults()) {
            System.out.println(result);
        }
    }

    /**
     * Simple console metrics sink.
     */
    private static class ConsoleMetricsRecorder implements MetricsRecorder {

        @Override
        public void recordSuccess(String hookId, Duration latency) {
            System.out.printf("METRICS: hook=%s status=SUCCESS latencyMs=%d%n",
                    hookId, latency.toMillis());
        }

        @Override
        public void recordFailure(String hookId, Duration latency, String reason) {
            System.out.printf("METRICS: hook=%s status=FAIL latencyMs=%d reason=%s%n",
                    hookId, latency.toMillis(), reason);
        }

        @Override
        public void recordCircuitOpen(String hookId) {
            System.out.printf("METRICS: hook=%s status=CIRCUIT_OPEN%n", hookId);
        }
    }

    /**
     * Minimal audit logger that writes to stdout; real systems would log to
     * secure append-only storage or SIEM.
     */
    private static class ConsoleAuditLogger implements AuditLogger {
        @Override
        public void logEvent(String eventType, Map<String, Object> details) {
            System.out.printf("AUDIT: type=%s details=%s%n", eventType, details);
        }
    }

    /**
     * Extremely simple LLM client for demo: echoes the prompt plus metadata.
     * Replace with OpenAI/Azure/local model integration in real deployments.
     */
    private static class EchoLlmClient implements LlmClient {
        @Override
        public String complete(String prompt, Map<String, Object> context) throws HookException {
            StringBuilder sb = new StringBuilder();
            sb.append("ECHO-LLM OUTPUT:\n");
            sb.append("Prompt:\n").append(prompt).append("\n");
            if (context != null && !context.isEmpty()) {
                sb.append("Context:\n").append(context).append("\n");
            }
            return sb.toString();
        }
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
