package com.example.bpme.BpmnDags.BPVertices.tasks;

import com.example.bpme.BpmnDags.BPNodeMetadata;
import com.example.bpme.BpmnDags.BPVertices.BPElementConsumer;
import com.example.bpme.common.BpmnParser;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.w3c.dom.Node;

public class CompensationTask extends BPElementConsumer {
    public CompensationTask(Node n, BpmnParser parser) {
        super(n, parser);
    }

    @Override
    public BPNodeMetadata getMetadata() {
        return null;
    }

    @Override
    public Vertex addToGraphWithPrev(Graph g, Vertex prev) {
        return null;
    }
}
