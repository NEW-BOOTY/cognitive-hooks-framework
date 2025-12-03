/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.time.Duration;

/**
 * Pluggable metrics sink for latency, error counts, etc.
 * Implementations can forward to Prometheus, OpenTelemetry, CloudWatch, etc.
 */
public interface MetricsRecorder {

    void recordSuccess(String hookId, Duration latency);

    void recordFailure(String hookId, Duration latency, String reason);

    void recordCircuitOpen(String hookId);
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
