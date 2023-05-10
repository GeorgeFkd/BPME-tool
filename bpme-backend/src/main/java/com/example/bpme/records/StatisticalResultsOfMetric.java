package com.example.bpme.records;

import com.example.bpme.records.StatisticalResults;

import java.util.Objects;

public record StatisticalResultsOfMetric(String metricName, StatisticalResults statistics) {
    public StatisticalResultsOfMetric {
        Objects.requireNonNull(metricName);
    }
}
