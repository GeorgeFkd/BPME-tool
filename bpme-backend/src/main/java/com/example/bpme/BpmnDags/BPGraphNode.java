package com.example.bpme.BpmnDags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerVertex;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class BPGraphNode{
    BPNodeMetadata metadata;
}
