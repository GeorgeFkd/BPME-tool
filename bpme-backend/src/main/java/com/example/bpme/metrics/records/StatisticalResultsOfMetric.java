package com.example.bpme.metrics.records;

import com.example.bpme.metrics.records.StatisticalResults;

import java.util.Objects;

public record StatisticalResultsOfMetric(String metricName, StatisticalResults statistics) {
    public StatisticalResultsOfMetric {
        Objects.requireNonNull(metricName);
    }
}
