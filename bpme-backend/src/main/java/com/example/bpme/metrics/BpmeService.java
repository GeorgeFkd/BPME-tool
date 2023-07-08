package com.example.bpme.metrics;

import com.example.bpme.metrics.records.MetricResults;
import com.example.bpme.metrics.records.MetricResultsOfFile;
import com.example.bpme.metrics.records.StatisticalResults;
import com.example.bpme.metrics.records.StatisticalResultsOfMetric;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.ArrayList;

@Service
@Component("BpmeService")
public class BpmeService implements CalculateMetricsService, CalculateMetricStatisticsService {



    public BpmeService() throws ParserConfigurationException {
    }



    @Override
    public ArrayList<StatisticalResultsOfMetric> calculateStatisticsForMetricValues(List<MetricResultsOfFile> metricResults, ArrayList<String> metricsToInclude) {
        ArrayList<StatisticalResultsOfMetric> statisticalResultsOfMetrics = new ArrayList<>();
        List<MetricResults> mResults = metricResults.stream().map(MetricResultsOfFile::results).flatMap(Collection::stream).toList();
        for (String theMetric : metricsToInclude) {
            //get metric values from MetricResultsOfFile, have to filter NaN values to produce results
            List<Number> metricValues = mResults.stream().filter(m -> m.metricName().equals(theMetric)).map(m -> m.result()).toList();
            double mean = metricValues.stream().mapToDouble(Number::doubleValue).filter(n->!Double.isNaN(n)).average().orElse(Double.NaN);
            double variance = metricValues.stream().mapToDouble(Number::doubleValue).filter(n->!Double.isNaN(n)).map(d -> Math.pow(d - mean, 2)).average().orElse(Double.NaN);
            double standardDeviation = Math.sqrt(variance);
            double median = metricValues.stream().mapToDouble(Number::doubleValue).filter(n->!Double.isNaN(n)).sorted().skip(metricValues.size() / 2).findFirst().orElse(Double.NaN);
            double min = metricValues.stream().mapToDouble(Number::doubleValue).filter(n->!Double.isNaN(n)).min().orElse(Double.NaN);
            double max = metricValues.stream().mapToDouble(Number::doubleValue).filter(n->!Double.isNaN(n)).max().orElse(Double.NaN);
            statisticalResultsOfMetrics.add(new StatisticalResultsOfMetric(theMetric, new StatisticalResults(mean, variance, standardDeviation, median, min, max)));

        }

        return statisticalResultsOfMetrics;
    }



    public ArrayList<MetricResults> calculateMetricsForFile(String fileContent,String filename,List<String> metricsToCalculate) throws NotImplementedException, XmlFileException {
        MetricsCalculator mc = new MetricsCalculator(fileContent,filename);
        return mc.calculateMetricsForFile(metricsToCalculate);
    }

    @Override
    public List<MetricResultsOfFile> calculateMetricsForFiles(List<Resource> fileList, ArrayList<String> metricsChosen) throws XmlFileException {
        List<MetricResultsOfFile> resultsList = new ArrayList<>();
        for (Resource f : fileList) {
            String fileContent;
            try {
                System.out.println("==================");
                System.out.println("FILENAME: " + f.getFilename());
                fileContent = f.getContentAsString(Charset.defaultCharset());
            } catch (IOException e) {
                System.out.println("Exception " + e.getCause() + " with msg: " + e.getMessage());
                continue;
            }
            ArrayList<MetricResults> metricResultsOfCurrentFile = calculateMetricsForFile(fileContent,f.getFilename(),metricsChosen);
            resultsList.add(new MetricResultsOfFile(f.getFilename(), metricResultsOfCurrentFile));

        }
        return resultsList;
    }
}
