package com.example.bpme.records;

import java.util.Objects;

public record MetricResults(String metricName, Number result)
{
    public MetricResults {
        Objects.requireNonNull(metricName);
//        Objects.requireNonNull(fileName);
        Objects.requireNonNull(result);
    }
}
