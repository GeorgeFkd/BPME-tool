package com.example.bpme.metrics.records;

import com.example.bpme.metrics.records.MetricResults;

import java.util.ArrayList;
import java.util.Objects;

public record MetricResultsOfFile(String filename, ArrayList<MetricResults> results) {
    public MetricResultsOfFile {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(results);
    }
}
