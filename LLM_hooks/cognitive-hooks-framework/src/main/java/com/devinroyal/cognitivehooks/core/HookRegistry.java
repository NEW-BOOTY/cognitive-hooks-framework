/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe registry of hooks. Allows runtime registration and removal.
 */
public final class HookRegistry {

    private final ConcurrentMap<String, Hook> hooksById = new ConcurrentHashMap<>();

    public void register(Hook hook) {
        Objects.requireNonNull(hook, "hook must not be null");
        hooksById.put(hook.getId(), hook);
    }

    public void unregister(String hookId) {
        if (hookId == null) {
            return;
        }
        hooksById.remove(hookId);
    }

    public Hook get(String hookId) {
        if (hookId == null) {
            return null;
        }
        return hooksById.get(hookId);
    }

    public List<Hook> listAll() {
        return Collections.unmodifiableList(new ArrayList<>(hooksById.values()));
    }

    public List<Hook> listByType(HookType type) {
        List<Hook> result = new ArrayList<>();
        for (Hook hook : hooksById.values()) {
            if (hook.getType() == type) {
                result.add(hook);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<Hook> listByTag(String tag) {
        List<Hook> result = new ArrayList<>();
        for (Hook hook : hooksById.values()) {
            if (hook.getTags().contains(tag)) {
                result.add(hook);
            }
        }
        return Collections.unmodifiableList(result);
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
