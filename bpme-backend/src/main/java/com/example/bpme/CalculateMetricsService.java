package com.example.bpme;

import com.example.bpme.records.MetricResultsOfFile;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

public interface CalculateMetricsService {

    public ArrayList<MetricResultsOfFile> calculateMetricsForFiles(List<Resource> fileList);

}
