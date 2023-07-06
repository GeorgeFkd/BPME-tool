package com.example.bpme.metrics;

import com.example.bpme.records.MetricResults;
import com.example.bpme.records.MetricResultsOfFile;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

public interface CalculateMetricsService {

    public List<MetricResultsOfFile> calculateMetricsForFiles(List<Resource> fileList,ArrayList<String> metricsChosen) throws InterruptedException;

}
