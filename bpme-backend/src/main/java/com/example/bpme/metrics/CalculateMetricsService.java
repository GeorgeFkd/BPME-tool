package com.example.bpme.metrics;

import com.example.bpme.metrics.records.MetricResultsOfFile;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface CalculateMetricsService {

    public List<MetricResultsOfFile> calculateMetricsForFiles(List<Resource> fileList,ArrayList<String> metricsChosen) throws
            NotImplementedException, XmlFileException;

}
