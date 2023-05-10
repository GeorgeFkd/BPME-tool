package com.example.bpme;


import com.example.bpme.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1/bpme")
public class BpmeController {

    private final CalculateMetricsService calculateMetricsApi;
    private final CalculateMetricStatisticsService calculateMetricStatisticsApi;

    @Autowired
    public BpmeController(@Qualifier("BpmeService") CalculateMetricsService calculateMetricsApi, @Qualifier("BpmeService") CalculateMetricStatisticsService calculateMetricStatisticsApi){
        this.calculateMetricStatisticsApi = calculateMetricStatisticsApi;
        this.calculateMetricsApi = calculateMetricsApi;
    }


    @PostMapping(path ="/files")
    public ResponseEntity<AnalysisResults> analyzeFiles(@RequestParam("files") MultipartFile[] filesArray) {
        List<Resource> resourceList = Arrays.stream(filesArray).map((f) -> f.getResource()).collect(Collectors.toList());
        ArrayList<MetricResultsOfFile> metricResults = this.calculateMetricsApi.calculateMetricsForFiles(resourceList);

        ArrayList<StatisticalResultsOfMetric> statisticalResultsOfMetrics = this.calculateMetricStatisticsApi.calculateStatisticsForMetricValues(metricResults);
        AnalysisResults analysisResults = new AnalysisResults(metricResults,statisticalResultsOfMetrics);

        return new ResponseEntity<>(analysisResults,HttpStatus.OK);
    }

}
