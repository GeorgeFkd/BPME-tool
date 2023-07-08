package com.example.bpme.metrics;

import com.example.bpme.common.BpmnParser;
import lombok.Getter;
import org.xml.sax.SAXException;

import java.io.IOException;

@Getter
public abstract class BPMetric {

    private final String name;
    private final String description;

    private final Number nullValue;
    private final Number errValue;
    private Number result;

    protected BpmnParser bpmnParser;

    public abstract Number calculateMetric();
    public BPMetric(String name, String description, Number nullValue, Number errValue){
        this.name = name;
        this.description = description;
        this.nullValue = nullValue;
        this.errValue = errValue;
    }

    public void setXmlFileUtils(BpmnParser bpmnParser){
        this.bpmnParser = bpmnParser;
    }


}
