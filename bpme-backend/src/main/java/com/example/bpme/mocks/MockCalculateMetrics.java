package com.example.bpme.mocks;

import com.example.bpme.CalculateMetricsService;
import com.example.bpme.records.MetricResults;
import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.MetricUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component("MockCalculateMetrics")
public class MockCalculateMetrics implements CalculateMetricsService {

    public ArrayList<MetricResultsOfFile> calculateMetricsForFiles(List<Resource> fileList,ArrayList<String> metricsChosen) {
        ArrayList<MetricResultsOfFile> totalResults = new ArrayList<>();
        for(Resource file : fileList){
            ArrayList<MetricResults> metricResults = new ArrayList<>();
            String filename = file.getFilename();
            for(String metric: MetricUtils.supportedMetrics){
                metricResults.add(new MetricResults(metric, (int) 1 + Math.random() * 15 ));
            }
            totalResults.add(new MetricResultsOfFile(filename,metricResults));
        }


        return totalResults;
    }


}
