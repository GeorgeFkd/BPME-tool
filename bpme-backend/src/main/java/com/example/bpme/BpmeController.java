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
    public BpmeController(@Qualifier("MockCalculateMetrics") CalculateMetricsService calculateMetricsApi, @Qualifier("MockCalculateMetricStatistics") CalculateMetricStatisticsService calculateMetricStatisticsApi){
        this.calculateMetricStatisticsApi = calculateMetricStatisticsApi;
        this.calculateMetricsApi = calculateMetricsApi;
    }


    @PostMapping(path ="/files")
    public ResponseEntity<AnalysisResults> submitMockFiles(@RequestParam("files") MultipartFile[] filesArray) {
        List<Resource> resourceList = Arrays.stream(filesArray).map((f) -> f.getResource()).collect(Collectors.toList());
        ArrayList<MetricResultsOfFile> metricResults = this.calculateMetricsApi.calculateMetricsForFiles(resourceList);
        ArrayList<StatisticalResultsOfMetric> statisticalResultsOfMetrics = this.calculateMetricStatisticsApi.calculateStatisticsForMetricValues(metricResults);
        AnalysisResults analysisResults = new AnalysisResults(metricResults,statisticalResultsOfMetrics);

        return new ResponseEntity<>(analysisResults,HttpStatus.OK);
    }

//    @PostMapping(path ="/files")
//    public ResponseEntity<ArrayList<HashMap<String,Number>>> submitFiles(@RequestParam("files") MultipartFile[] filesArray){
//        List<Resource> resourceList = Arrays.stream(filesArray).map((f)-> f.getResource()).collect(Collectors.toList());
////        System.out.println(resourceList);
//        System.out.println("HELLO DOCKER");
//        System.out.println(resourceList.get(0).getFilename());
//        try {
//            ArrayList<HashMap<String,Number>> result = this.bpmeService.ParseXmlFiles(resourceList);
//            return new ResponseEntity<>(result,HttpStatus.OK);
//
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        //return new ResponseEntity<>(filesArray.toString(), HttpStatus.OK);
//    }
}
