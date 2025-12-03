/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wraps raw hook execution with extreme error handling:
 * retries, circuit-breakers, and metrics logging.
 */
public final class ResilientHookExecutor {

    private final int maxRetries;
    private final int failureThreshold;
    private final Duration openInterval;

    private final MetricsRecorder metricsRecorder;
    private final Map<String, CircuitState> circuitStates = new ConcurrentHashMap<>();

    public ResilientHookExecutor(int maxRetries,
                                 int failureThreshold,
                                 Duration openInterval,
                                 MetricsRecorder metricsRecorder) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be >= 0");
        }
        if (failureThreshold <= 0) {
            throw new IllegalArgumentException("failureThreshold must be > 0");
        }
        this.maxRetries = maxRetries;
        this.failureThreshold = failureThreshold;
        this.openInterval = openInterval == null ? Duration.ofSeconds(30) : openInterval;
        this.metricsRecorder = metricsRecorder;
    }

    public HookResult execute(Hook hook, HookRequest request, HookExecutionContext ctx) {
        String hookId = hook.getId();
        CircuitState state = circuitStates.computeIfAbsent(hookId, k -> new CircuitState());

        if (state.isOpen() && !state.canAttemptNow(openInterval)) {
            if (metricsRecorder != null) {
                metricsRecorder.recordCircuitOpen(hookId);
            }
            return HookResult.builder(hookId, hook.getType(), HookStatus.CIRCUIT_OPEN)
                    .message("Circuit open for hook; skipping execution")
                    .build();
        }

        int attempt = 0;
        Instant start = Instant.now();
        while (true) {
            attempt++;
            try {
                HookResult result = hook.execute(request, ctx);
                state.onSuccess();
                if (metricsRecorder != null) {
                    metricsRecorder.recordSuccess(hookId, Duration.between(start, Instant.now()));
                }
                return result;
            } catch (HookException ex) {
                state.onFailure();
                Duration latency = Duration.between(start, Instant.now());
                String reason = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                if (metricsRecorder != null) {
                    metricsRecorder.recordFailure(hookId, latency, reason);
                }

                if (attempt > maxRetries) {
                    return HookResult.builder(hookId, hook.getType(), HookStatus.FAILED)
                            .message("Hook failed after " + attempt + " attempts: " + reason)
                            .build();
                }

                if (state.getFailureCount() >= failureThreshold) {
                    state.open();
                    return HookResult.builder(hookId, hook.getType(), HookStatus.CIRCUIT_OPEN)
                            .message("Hook circuit opened after repeated failures: " + reason)
                            .build();
                }

                // brief blocking backoff to reduce hammering; in real systems this can be async
                try {
                    Thread.sleep(Math.min(200L * attempt, 1000L));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return HookResult.builder(hookId, hook.getType(), HookStatus.FAILED)
                            .message("Hook execution interrupted during backoff")
                            .build();
                }
            }
        }
    }

    private static final class CircuitState {
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private volatile boolean open = false;
        private volatile Instant openedAt;

        void onSuccess() {
            failureCount.set(0);
            open = false;
            openedAt = null;
        }

        void onFailure() {
            failureCount.incrementAndGet();
        }

        int getFailureCount() {
            return failureCount.get();
        }

        boolean isOpen() {
            return open;
        }

        void open() {
            open = true;
            openedAt = Instant.now();
        }

        boolean canAttemptNow(Duration openInterval) {
            if (!open || openedAt == null) {
                return true;
            }
            Instant now = Instant.now();
            return now.isAfter(openedAt.plus(openInterval));
        }
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
