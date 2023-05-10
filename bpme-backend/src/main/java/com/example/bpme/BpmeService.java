package com.example.bpme;

import com.example.bpme.records.MetricResults;
import com.example.bpme.records.MetricResultsOfFile;
import com.example.bpme.records.StatisticalResults;
import com.example.bpme.records.StatisticalResultsOfMetric;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static java.util.List.*;

@Service
@Component("BpmeService")
public class BpmeService implements CalculateMetricsService, CalculateMetricStatisticsService {

    private MetricUtils metricUtils = new MetricUtils();

    public BpmeService() throws ParserConfigurationException {
    }


    public ArrayList<HashMap<String, Number>> ParseXmlFiles(List<Resource> fileList) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        //get file contents
        ArrayList<String> fileContents = new ArrayList<>();

        for (Resource r : fileList) {
            try {
                fileContents.add(r.getContentAsString(Charset.defaultCharset()));
            } catch (IOException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
        MetricUtils m = new MetricUtils();
        m.NSFG(fileContents.get(0), -10);
        ArrayList<HashMap<String, Number>> hashMaps = new ArrayList<>();
        for (String fileStr : fileContents) {
            HashMap<String, Number> metricResultsForFile = new HashMap<>();
            metricResultsForFile.put("NSFG", m.NSFG(fileStr, -1));//✔️
            metricResultsForFile.put("NOA", m.NOA(fileStr, -1));//✔️
            metricResultsForFile.put("NOAJS", m.NOAJS(fileStr, -1));//✔️
            metricResultsForFile.put("DENSITY", m.DENSITY(fileStr, -1.0));//✔️
            metricResultsForFile.put("CNC", m.CNC(fileStr, -1.0));//✔️
            metricResultsForFile.put("CFC", m.CFC(fileStr, -1));//✔️
            metricResultsForFile.put("GH", m.GH(fileStr, -1.0));//✔️
            metricResultsForFile.put("MGD", m.MGD(fileStr, -1.0));//✔️
            metricResultsForFile.put("GM", m.GM(fileStr, -1.0));//✔️
            metricResultsForFile.put("NMF", m.NMF(fileStr, -1));//✔️ !![with a catch for pools-> probably should be deactivated]
            metricResultsForFile.put("NSFA", m.NSFA(fileStr, -1));//✔️
            metricResultsForFile.put("NSFE", m.NSFE(fileStr, -1));//✔️
            metricResultsForFile.put("TNG", m.TNG(fileStr, -1));//✔️
            metricResultsForFile.put("CLA", m.CLA(fileStr, -1));//✔️
            metricResultsForFile.put("AGD", m.AGD(fileStr, -1));//✔️
            metricResultsForFile.put("SFs", m.sequenceFlowsInDiagram(fileStr, -1));//✔️
            hashMaps.add(metricResultsForFile);
        }


//        metricResults.put("TNG",allGatewaysNodeList.getLength());

        return hashMaps;


    }


    @Override
    public ArrayList<StatisticalResultsOfMetric> calculateStatisticsForMetricValues(List<MetricResultsOfFile> metricResults, ArrayList<String> metricsToInclude) {
        ArrayList<StatisticalResultsOfMetric> statisticalResultsOfMetrics = new ArrayList<>();
        List<MetricResults> mResults = metricResults.stream().map(MetricResultsOfFile::results).flatMap(Collection::stream).toList();
        for (String metricName : metricsToInclude) {
            //get metric values from MetricResultsOfFile
            List<Number> metricValues = mResults.stream().filter(m -> m.metricName().equals(metricName)).map(m -> m.result()).toList();
            System.out.println("FLATTENED IT: " + mResults);
            double mean = metricValues.stream().mapToDouble(Number::doubleValue).average().orElse(Double.NaN);
            double variance = metricValues.stream().mapToDouble(Number::doubleValue).map(d -> Math.pow(d - mean, 2)).average().orElse(Double.NaN);
            double standardDeviation = Math.sqrt(variance);
            double median = metricValues.stream().mapToDouble(Number::doubleValue).sorted().skip(metricValues.size() / 2).findFirst().orElse(Double.NaN);
            double min = metricValues.stream().mapToDouble(Number::doubleValue).min().orElse(Double.NaN);
            double max = metricValues.stream().mapToDouble(Number::doubleValue).max().orElse(Double.NaN);
            statisticalResultsOfMetrics.add(new StatisticalResultsOfMetric(metricName, new StatisticalResults(mean, variance, standardDeviation, median, min, max)));

        }

        return statisticalResultsOfMetrics;
    }

//    public ArrayList<MetricResults> calculateMetricsForFile(Resource fileResource){
//        ArrayList<MetricResults> metricResults = new ArrayList<>();
//        try {
//            String fileContent = fileResource.getContentAsString(Charset.defaultCharset());
//            for(BPMetric metric:this.metricUtils.getMetrics()){
//                metricResults.add(new MetricResults(metric.getName(),metric.calculateMetric(fileContent)));
//            }
//            return metricResults;
//        } catch (IOException e) {
//            return metricResults;
//        }
//
//    }


//    public ArrayList<MetricResultsOfFile> calculateAllMetricsForFiles(List<Resource> fileList) {
//        ArrayList<MetricResultsOfFile> metricResultsOfFiles = new ArrayList<>();
//        for (Resource file : fileList) {
//            ArrayList<MetricResults> metricResults = calculateMetricsForFile(file);
//            metricResultsOfFiles.add(new MetricResultsOfFile(file.getFilename(), metricResults));
//        }
//        return metricResultsOfFiles;
//    }

    @Override
    public List<MetricResultsOfFile> calculateMetricsForFiles(List<Resource> fileList, ArrayList<String> metricsChosen) {
        List<MetricResultsOfFile> resultsList = new ArrayList<>();
        List<BPMetric> metricsToCalculate = this.metricUtils.getMetrics().stream().filter(m -> metricsChosen.contains(m.getName())).toList();
        for (Resource f : fileList) {
            try {
                String fileContent = f.getContentAsString(Charset.defaultCharset());
                ArrayList<MetricResults> metricResultsOfCurrentFile = new ArrayList<>();
                for (BPMetric metric : metricsToCalculate) {
                    metricResultsOfCurrentFile.add(new MetricResults(metric.getName(), metric.calculateMetric(fileContent)));
                }

                resultsList.add(new MetricResultsOfFile(f.getFilename(), metricResultsOfCurrentFile));
            } catch (IOException e) {
                System.out.println("Exception " + e.getCause() + " with msg: " + e.getMessage());
                continue;
            }

        }
        return resultsList;
    }
}
