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

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Example of a "run simulation" capability. This one runs a simple Monte Carlo
 * style random-walk simulation to show structured external reasoning.
 */
public class SimulationHook implements Hook {

    private static final String ID = "simulation-hook";

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
        tags.add("simulation");
        tags.add("run-simulation");
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean supports(HookRequest request, HookExecutionContext ctx) {
        String prompt = request.getPrompt().toLowerCase();
        return prompt.contains("simulate") || prompt.contains("simulation");
    }

    @Override
    public HookResult execute(HookRequest request, HookExecutionContext ctx) throws HookException {
        Instant start = Instant.now();

        int steps = 20;
        Object stepsOverride = request.getPayload().get("simulationSteps");
        if (stepsOverride instanceof Number) {
            int val = ((Number) stepsOverride).intValue();
            if (val > 0 && val <= 1000) {
                steps = val;
            }
        }

        double position = 0.0;
        Random random = new Random();
        List<Double> positions = new ArrayList<>(steps);

        for (int i = 0; i < steps; i++) {
            position += random.nextBoolean() ? 1.0 : -1.0;
            positions.add(position);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("type", "random-walk");
        data.put("steps", steps);
        data.put("finalPosition", position);
        data.put("positions", positions);

        Duration latency = Duration.between(start, Instant.now());
        return HookResult.builder(getId(), getType(), HookStatus.SUCCESS)
                .message("Simulation completed")
                .data(data)
                .latency(latency)
                .build();
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
