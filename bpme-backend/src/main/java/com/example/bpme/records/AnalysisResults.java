package com.example.bpme.records;


import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.records.StatisticalResultsOfMetric;

import java.util.ArrayList;
import java.util.Objects;

public record AnalysisResults(ArrayList<MetricResultsOfFile> metricResults, ArrayList<StatisticalResultsOfMetric> statisticalResults)
{
    public AnalysisResults {
        Objects.requireNonNull(metricResults);
        Objects.requireNonNull(statisticalResults);
    }
}
