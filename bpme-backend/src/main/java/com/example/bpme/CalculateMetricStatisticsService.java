package com.example.bpme;

import com.example.bpme.records.MetricResults;
import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.records.StatisticalResultsOfMetric;

import java.util.ArrayList;
import java.util.List;

public interface CalculateMetricStatisticsService {


    public List<StatisticalResultsOfMetric> calculateStatisticsForMetricValues(List<MetricResultsOfFile> metricResults, ArrayList<String> metricsChosen);
}
