package com.example.bpme;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class BPMetric {

    private String name;
    private String description;

    private Number nullValue;
    private Number errValue;
    private Number result;

    public abstract Number calculateMetric(String xmlString);
    public BPMetric(String name, String description, Number nullValue, Number errValue){
        this.name = name;
        this.description = description;
        this.nullValue = nullValue;
        this.errValue = errValue;
    }


}
