package com.example.bpme.BpmnDags;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class BPGraphNode {
    BPNodeMetadata metadata;
    ArrayList<BPGraphNode> previousElements;
    ArrayList<BPGraphNode> nextElements;
}
