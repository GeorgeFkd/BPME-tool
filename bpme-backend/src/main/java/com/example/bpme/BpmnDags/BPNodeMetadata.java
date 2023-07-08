package com.example.bpme.BpmnDags;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BPNodeMetadata {
    private double selectivity;
    private double cost;
    private String id;


}
