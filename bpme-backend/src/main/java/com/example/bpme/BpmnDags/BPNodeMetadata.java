package com.example.bpme.BpmnDags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BPNodeMetadata {
    private double selectivity;
    private double cost;
    private String idOfXmlElement;
    private Boolean isDummy;
    private String id;



}
