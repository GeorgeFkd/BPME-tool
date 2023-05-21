package com.example.bpme;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class BPMetric {

    private final String name;
    private final String description;

    private final Number nullValue;
    private final Number errValue;
    private Number result;


    public abstract Number calculateMetric(String xmlString) throws Exception;
    public BPMetric(String name, String description, Number nullValue, Number errValue){
        this.name = name;
        this.description = description;
        this.nullValue = nullValue;
        this.errValue = errValue;
    }


}
