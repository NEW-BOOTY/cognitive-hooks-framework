/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.safeguards;

import com.devinroyal.cognitivehooks.core.Hook;
import com.devinroyal.cognitivehooks.core.HookExecutionContext;
import com.devinroyal.cognitivehooks.core.HookException;
import com.devinroyal.cognitivehooks.core.HookRequest;
import com.devinroyal.cognitivehooks.core.HookResult;
import com.devinroyal.cognitivehooks.core.HookStatus;
import com.devinroyal.cognitivehooks.core.HookType;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Ethical safeguard hook that scans user prompts for disallowed patterns
 * or sensitive content and annotates the request.
 */
public class BiasFilterHook implements Hook {

    private static final String ID = "bias-filter-hook";

    private final List<Pattern> bannedPatterns;

    public BiasFilterHook() {
        // Minimal illustrative patterns; production systems would load from
        // configuration or centralized policy.
        this.bannedPatterns = new ArrayList<>();
        bannedPatterns.add(Pattern.compile("\\b(?i)kill\\b"));
        bannedPatterns.add(Pattern.compile("\\b(?i)genocide\\b"));
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public HookType getType() {
        return HookType.SAFEGUARD;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("bias-filter");
        tags.add("safety");
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean supports(HookRequest request, HookExecutionContext ctx) {
        return request.getPrompt() != null;
    }

    @Override
    public HookResult execute(HookRequest request, HookExecutionContext ctx) throws HookException {
        Instant start = Instant.now();
        String prompt = request.getPrompt();
        List<String> matches = new ArrayList<>();

        for (Pattern pattern : bannedPatterns) {
            if (pattern.matcher(prompt).find()) {
                matches.add(pattern.pattern());
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("flaggedPatterns", matches);
        data.put("safe", matches.isEmpty());

        Duration latency = Duration.between(start, Instant.now());
        HookStatus status = HookStatus.SUCCESS;
        String message = matches.isEmpty()
                ? "No banned patterns detected"
                : "Potentially unsafe content detected";

        return HookResult.builder(getId(), getType(), status)
                .message(message)
                .data(data)
                .latency(latency)
                .build();
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
