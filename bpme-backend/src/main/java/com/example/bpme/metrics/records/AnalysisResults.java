package com.example.bpme.metrics.records;


import java.util.List;
import java.util.Objects;

public record AnalysisResults(List<MetricResultsOfFile> metricResults, List<StatisticalResultsOfMetric> statisticalResults)
{
    public AnalysisResults {
        Objects.requireNonNull(metricResults);
        Objects.requireNonNull(statisticalResults);
    }
}
