package com.example.bpme.records;


import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.records.StatisticalResultsOfMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record AnalysisResults(List<MetricResultsOfFile> metricResults, List<StatisticalResultsOfMetric> statisticalResults)
{
    public AnalysisResults {
        Objects.requireNonNull(metricResults);
        Objects.requireNonNull(statisticalResults);
    }
}
