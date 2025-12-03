/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */

package com.devinroyal.cognitivehooks.hooks.enterprise;

import com.devinroyal.cognitivehooks.core.Hook;
import com.devinroyal.cognitivehooks.core.HookExecutionContext;
import com.devinroyal.cognitivehooks.core.HookException;
import com.devinroyal.cognitivehooks.core.HookRequest;
import com.devinroyal.cognitivehooks.core.HookResult;
import com.devinroyal.cognitivehooks.core.HookStatus;
import com.devinroyal.cognitivehooks.core.HookType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Enterprise hook that allows controlled, parameterized database queries.
 * This demonstrates "query database" capabilities without coupling to any one vendor.
 */
public class DatabaseQueryHook implements Hook {

    private static final String ID = "database-query-hook";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public HookType getType() {
        return HookType.ENTERPRISE;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("db-query");
        tags.add("enterprise");
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean supports(HookRequest request, HookExecutionContext ctx) {
        return request.getPayload().containsKey("sqlQuery");
    }

    @Override
    public HookResult execute(HookRequest request, HookExecutionContext ctx) throws HookException {
        Instant start = Instant.now();
        Connection connection = ctx.getDatabaseConnection();
        if (connection == null) {
            throw new HookException("No database connection available for DatabaseQueryHook");
        }

        Object sqlObj = request.getPayload().get("sqlQuery");
        if (!(sqlObj instanceof String sql)) {
            throw new HookException("Payload 'sqlQuery' must be a String");
        }

        if (!sql.trim().toLowerCase().startsWith("select")) {
            throw new HookException("Only SELECT statements are allowed");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String colName = rs.getMetaData().getColumnLabel(i);
                        row.put(colName, rs.getObject(i));
                    }
                    rows.add(row);
                }
                Duration latency = Duration.between(start, Instant.now());
                return HookResult.builder(getId(), getType(), HookStatus.SUCCESS)
                        .message("Successfully executed database query")
                        .data(rows)
                        .latency(latency)
                        .build();
            }
        } catch (SQLException e) {
            throw new HookException("Database query failed: " + e.getMessage(), e);
        }
    }
}

/*
 * Copyright © 2025 Devin B. Royal.
 * All Rights Reserved.
 */
