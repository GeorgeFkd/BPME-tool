package com.example.bpme.BpmnDags.BPVertices.tasks;

import com.example.bpme.BpmnDags.BPNodeMetadata;
import com.example.bpme.BpmnDags.BPVertices.BPElementConsumer;
import com.example.bpme.common.BpmnParser;
import org.w3c.dom.Node;

public class LoopTask extends BPElementConsumer {
    @Override
    public BPNodeMetadata getMetadata() {
        return null;
    }

    public LoopTask(Node n, BpmnParser parser) {
        super(n, parser);
    }
}
