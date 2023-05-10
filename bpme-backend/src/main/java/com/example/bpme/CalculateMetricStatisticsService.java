package com.example.bpme;

import com.example.bpme.records.MetricResults;
import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.records.StatisticalResultsOfMetric;

import java.util.ArrayList;

public interface CalculateMetricStatisticsService {


    public ArrayList<StatisticalResultsOfMetric> calculateStatisticsForMetricValues(ArrayList<MetricResultsOfFile> metricResults);
}
