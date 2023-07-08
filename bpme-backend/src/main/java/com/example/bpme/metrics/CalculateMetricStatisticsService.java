package com.example.bpme.metrics;

import com.example.bpme.metrics.records.MetricResultsOfFile;
import com.example.bpme.metrics.records.StatisticalResultsOfMetric;

import java.util.ArrayList;
import java.util.List;

public interface CalculateMetricStatisticsService {


    public List<StatisticalResultsOfMetric> calculateStatisticsForMetricValues(List<MetricResultsOfFile> metricResults, ArrayList<String> metricsChosen);
}
