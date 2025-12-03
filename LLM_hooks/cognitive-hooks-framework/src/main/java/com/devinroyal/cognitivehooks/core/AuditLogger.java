/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.util.Map;

/**
 * Enterprise audit logging interface. Implementations can push to
 * SIEM, append-only logs, or secure storage for compliance.
 */
public interface AuditLogger {

    void logEvent(String eventType, Map<String, Object> details);
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
