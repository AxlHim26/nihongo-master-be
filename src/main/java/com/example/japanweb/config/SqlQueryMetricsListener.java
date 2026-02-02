package com.example.japanweb.config;

import com.example.japanweb.config.properties.SqlObservabilityProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlQueryMetricsListener implements QueryExecutionListener {

    private static final String QUERY_TIMER = "app.db.query.execution";
    private static final String SLOW_QUERY_COUNTER = "app.db.query.slow.count";
    private static final String QUERY_BATCH_SIZE = "app.db.query.batch.size";

    private final MeterRegistry meterRegistry;
    private final SqlObservabilityProperties properties;

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // no-op
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        if (!properties.isEnabled()) {
            return;
        }

        long elapsedMs = Math.max(execInfo.getElapsedTime(), 0L);
        boolean success = execInfo.getThrowable() == null;
        boolean batch = execInfo.isBatch();
        int batchSize = Math.max(queryInfoList.size(), 1);
        String operation = resolveOperation(queryInfoList, batch);

        Timer timer = meterRegistry.timer(
                QUERY_TIMER,
                "operation", operation,
                "success", Boolean.toString(success),
                "batch", Boolean.toString(batch)
        );
        timer.record(elapsedMs, TimeUnit.MILLISECONDS);

        DistributionSummary summary = meterRegistry.summary(
                QUERY_BATCH_SIZE,
                "operation", operation,
                "batch", Boolean.toString(batch)
        );
        summary.record(batchSize);

        if (elapsedMs < properties.getSlowQueryThresholdMs()) {
            return;
        }

        Counter counter = meterRegistry.counter(
                SLOW_QUERY_COUNTER,
                "operation", operation,
                "success", Boolean.toString(success)
        );
        counter.increment();

        if (properties.isSlowQueryLogEnabled()) {
            log.warn(
                    "Slow SQL detected ({} ms, op={}, batch={}, size={}, success={}): {}",
                    elapsedMs,
                    operation,
                    batch,
                    batchSize,
                    success,
                    buildSqlPreview(queryInfoList)
            );
        }
    }

    private String resolveOperation(List<QueryInfo> queryInfoList, boolean batch) {
        if (batch || queryInfoList.size() > 1) {
            return "BATCH";
        }
        if (queryInfoList.isEmpty()) {
            return "UNKNOWN";
        }

        String query = normalize(queryInfoList.getFirst().getQuery()).toUpperCase(Locale.ROOT);
        if (query.startsWith("SELECT")) {
            return "SELECT";
        }
        if (query.startsWith("INSERT")) {
            return "INSERT";
        }
        if (query.startsWith("UPDATE")) {
            return "UPDATE";
        }
        if (query.startsWith("DELETE")) {
            return "DELETE";
        }
        if (query.startsWith("MERGE")) {
            return "MERGE";
        }
        if (query.startsWith("WITH")) {
            return "CTE";
        }
        return "OTHER";
    }

    private String buildSqlPreview(List<QueryInfo> queryInfoList) {
        if (queryInfoList.isEmpty()) {
            return "<empty>";
        }

        String joinedSql = queryInfoList.stream()
                .map(QueryInfo::getQuery)
                .map(this::normalize)
                .reduce((left, right) -> left + " || " + right)
                .orElse("<empty>");

        int maxLength = properties.getMaxSqlLength();
        if (joinedSql.length() <= maxLength) {
            return joinedSql;
        }
        return joinedSql.substring(0, maxLength) + "... [truncated]";
    }

    private String normalize(String rawSql) {
        if (rawSql == null) {
            return "";
        }
        return rawSql.replaceAll("\\s+", " ").trim();
    }
}
