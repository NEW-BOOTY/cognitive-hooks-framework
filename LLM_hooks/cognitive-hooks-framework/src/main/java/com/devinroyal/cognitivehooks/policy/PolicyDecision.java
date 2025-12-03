/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.policy;

/**
 * Simple decision object to express whether a hook is allowed for a request,
 * and why.
 */
public final class PolicyDecision {

    private final boolean allowed;
    private final String reason;

    private PolicyDecision(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static PolicyDecision allow() {
        return new PolicyDecision(true, "allowed");
    }

    public static PolicyDecision deny(String reason) {
        return new PolicyDecision(false, reason == null ? "denied" : reason);
    }

    public boolean allowed() {
        return allowed;
    }

    public String reason() {
        return reason;
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
