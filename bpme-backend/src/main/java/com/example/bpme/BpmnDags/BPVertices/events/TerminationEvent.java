package com.example.bpme.BpmnDags.BPVertices.events;

import com.example.bpme.BpmnDags.BPNodeMetadata;
import com.example.bpme.BpmnDags.BPVertices.BPElementConsumer;
import com.example.bpme.common.BpmnParser;
import org.w3c.dom.Node;

public class TerminationEvent extends BPElementConsumer {
    @Override
    public BPNodeMetadata getMetadata() {
        return null;
    }

    public TerminationEvent(Node n, BpmnParser parser) {
        super(n, parser);
    }

}
