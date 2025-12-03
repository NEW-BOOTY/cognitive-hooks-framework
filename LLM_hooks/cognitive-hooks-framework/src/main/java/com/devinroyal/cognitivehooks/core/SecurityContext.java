/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the security posture of the current request:
 * identity, roles, and scopes. Hooks use this to enforce authorization.
 */
public final class SecurityContext {

    private final String userId;
    private final Set<String> roles;
    private final Set<String> scopes;

    public SecurityContext(String userId, Set<String> roles, Set<String> scopes) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(roles));
        this.scopes = scopes == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(scopes));
    }

    public String getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }

    @Override
    public String toString() {
        return "SecurityContext{" +
                "userId='" + userId + '\'' +
                ", roles=" + roles +
                ", scopes=" + scopes +
                '}';
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
