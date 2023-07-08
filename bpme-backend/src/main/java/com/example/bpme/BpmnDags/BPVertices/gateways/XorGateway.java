package com.example.bpme.BpmnDags.BPVertices.gateways;

import com.example.bpme.BpmnDags.BPNodeMetadata;
import com.example.bpme.BpmnDags.BPVertices.BPElementConsumer;
import com.example.bpme.common.BpmnParser;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.w3c.dom.Node;

public class XorGateway extends BPElementConsumer {


    @Override
    public BPNodeMetadata getMetadata() {
        return null;
    }

//    @Override
//    public Vertex addToGraphWithPrev(Graph g, Vertex prev) {
//        Vertex v = g.addVertex(this);
//        v.addEdge("xor",prev);
//        return v;
//    }


    //ill probably need to separate the cases of merge and split gateways
    public XorGateway(Node n, BpmnParser parser) {
        super(n, parser);
        BPNodeMetadata metadata = new BPNodeMetadata();

        this.setMetadata(metadata);
        //produce metadata

    }
}
