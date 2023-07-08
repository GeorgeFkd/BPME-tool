package com.example.bpme.metrics;


import com.example.bpme.metrics.CalculateMetricStatisticsService;
import com.example.bpme.metrics.CalculateMetricsService;
import com.example.bpme.metrics.records.AnalysisResults;
import com.example.bpme.metrics.records.MetricResultsOfFile;
import com.example.bpme.metrics.records.StatisticalResultsOfMetric;
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
    public ResponseEntity<AnalysisResults> analyzeFiles(@RequestParam("files") MultipartFile[] filesArray, @RequestParam("metrics") ArrayList<String> metricsToInclude) throws XmlFileException {
        List<Resource> resourceList = Arrays.stream(filesArray).map(MultipartFile::getResource).collect(Collectors.toList());
            List<MetricResultsOfFile> metricResults = this.calculateMetricsApi.calculateMetricsForFiles(resourceList,metricsToInclude);
            List<StatisticalResultsOfMetric> statisticalResultsOfMetrics = this.calculateMetricStatisticsApi.calculateStatisticsForMetricValues(metricResults,metricsToInclude);
            AnalysisResults analysisResults = new AnalysisResults(metricResults,statisticalResultsOfMetrics);
            return new ResponseEntity<>(analysisResults,HttpStatus.OK);
    }
    @ExceptionHandler({XmlFileException.class})
    public ResponseEntity<InformationForXmlFileException>handleXmlFileProblem(XmlFileException e){
        InformationForXmlFileException info = new InformationForXmlFileException(e.getFilename(),e.getExceptionMessage(),"This file you uploaded is not a valid xml file","Please upload a valid xml file");
        return new ResponseEntity<>(info,HttpStatus.BAD_REQUEST);
    }
    private record InformationForXmlFileException(String filename,String exceptionMessage,String forUserWhatHappenedMessage,String forUserHowToFixMessage){

    }



}
