package com.example.bpme.records;

import java.util.Objects;

public record StatisticalResults(Number mean, Number median, Number standardDeviation, Number variance) {
    public StatisticalResults {
        Objects.requireNonNull(mean);
        Objects.requireNonNull(median);
        Objects.requireNonNull(standardDeviation);
        Objects.requireNonNull(variance);
    }
}

