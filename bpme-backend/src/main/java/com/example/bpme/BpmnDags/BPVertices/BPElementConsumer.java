package com.example.bpme.BpmnDags.BPVertices;

import com.example.bpme.BpmnDags.BPNodeMetadata;
import com.example.bpme.common.BpmnParser;
import lombok.Getter;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.w3c.dom.Node;
@Getter
@Setter
public abstract class BPElementConsumer {

    private Node xmlNode;
    private BpmnParser parser;
    private BPNodeMetadata metadata;
    public abstract BPNodeMetadata getMetadata();

    public Vertex addToGraphWithPrev(Graph g, Vertex prev){
        if(prev == null) return g.addVertex("start",true);
        String idOfNode = this.getXmlNode().getAttributes().getNamedItem("id").getTextContent();
        GraphTraversalSource gts = g.traversal();
        Vertex inGraph = gts.V().has("id", idOfNode).tryNext().orElse(null);
        if(inGraph!=null){
            Edge e = inGraph.addEdge("default", prev);
            return inGraph;
        }else{
            Vertex v = g.addVertex("type", this.getClass().getSimpleName(),"id", idOfNode);
            Edge e = v.addEdge("default", prev);
            return v;
        }
    }

    public BPElementConsumer(Node n, BpmnParser parser) {
        //initialise the common fields in the class
        this.xmlNode = n;
        this.parser = parser;
        this.metadata = new BPNodeMetadata();


    }

    private void fillInCommonMetadata(){
        //use xmlNode to produce some of them
        //the rest will be produced by them
    }
}
