package com.example.bpme;


import com.example.bpme.metrics.CalculateMetricStatisticsService;
import com.example.bpme.metrics.CalculateMetricsService;
import com.example.bpme.records.*;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
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
    public ResponseEntity<AnalysisResults> analyzeFiles(@RequestParam("files") MultipartFile[] filesArray,@RequestParam("metrics") ArrayList<String> metricsToInclude)  {
        List<Resource> resourceList = Arrays.stream(filesArray).map(MultipartFile::getResource).collect(Collectors.toList());

        try{
            List<MetricResultsOfFile> metricResults = this.calculateMetricsApi.calculateMetricsForFiles(resourceList,metricsToInclude);
            List<StatisticalResultsOfMetric> statisticalResultsOfMetrics = this.calculateMetricStatisticsApi.calculateStatisticsForMetricValues(metricResults,metricsToInclude);
            AnalysisResults analysisResults = new AnalysisResults(metricResults,statisticalResultsOfMetrics);
            return new ResponseEntity<>(analysisResults,HttpStatus.OK);
        }catch (ParserConfigurationException | IOException | SAXException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (NotImplementedException e) {
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }


    }

}
