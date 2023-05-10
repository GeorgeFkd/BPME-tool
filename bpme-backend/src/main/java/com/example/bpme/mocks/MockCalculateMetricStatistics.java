package com.example.bpme.mocks;

import com.example.bpme.CalculateMetricStatisticsService;
import com.example.bpme.MetricUtils;
import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.records.StatisticalResults;
import com.example.bpme.records.StatisticalResultsOfMetric;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("MockCalculateMetricStatistics")
public class MockCalculateMetricStatistics implements CalculateMetricStatisticsService {

    public ArrayList<StatisticalResultsOfMetric> calculateStatisticsForMetricValues(ArrayList<MetricResultsOfFile> metricResults) {
        ArrayList<StatisticalResultsOfMetric> statisticalResults = new ArrayList<>();

        for(String metric: MetricUtils.supportedMetrics){

            System.out.println(metric);
            statisticalResults.add(new StatisticalResultsOfMetric(metric,new StatisticalResults( (int) 1 + Math.random() * 15,(int) 1 + Math.random() * 15,(int) 1 + Math.random() * 15,(int) 1 + Math.random() * 15 )));
        }


        return statisticalResults;
    }
}

