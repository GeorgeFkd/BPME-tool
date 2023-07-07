package com.example.bpme.records;

import java.util.ArrayList;
import java.util.Objects;

public record MetricResultsOfFile(String filename, ArrayList<MetricResults> results) {
    public MetricResultsOfFile {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(results);
    }
}
