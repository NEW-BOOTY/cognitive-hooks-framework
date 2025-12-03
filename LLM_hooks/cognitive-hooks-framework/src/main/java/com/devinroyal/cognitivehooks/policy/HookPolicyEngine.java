/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.policy;

import com.devinroyal.cognitivehooks.core.Hook;
import com.devinroyal.cognitivehooks.core.HookExecutionContext;
import com.devinroyal.cognitivehooks.core.HookRequest;
import com.devinroyal.cognitivehooks.core.HookType;
import com.devinroyal.cognitivehooks.core.SecurityContext;

/**
 * Policy layer to enforce enterprise compliance, monetization rules, and
 * ethical safeguards.
 *
 * For demo:
 * - Blocks ENTERPRISE hooks when user lacks "enterprise:hooks" scope.
 * - Blocks SAFEGUARD hooks only if explicitly restricted.
 */
public class HookPolicyEngine {

    public PolicyDecision evaluate(Hook hook, HookRequest request, HookExecutionContext ctx) {
        SecurityContext sc = ctx.getSecurityContext();

        if (hook.getType() == HookType.ENTERPRISE && !sc.hasScope("enterprise:hooks")) {
            return PolicyDecision.deny("Missing scope 'enterprise:hooks'");
        }

        if (hook.getType() == HookType.SAFEGUARD && sc.hasScope("skip:safeguards")) {
            return PolicyDecision.deny("User scope skip:safeguards prevents safeguard execution");
        }

        // Future: monetize by tenant, throttle by org, route across clouds, etc.
        return PolicyDecision.allow();
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
