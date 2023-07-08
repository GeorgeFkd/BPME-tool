package com.example.bpme.BpmnDags;

import com.example.bpme.BpmnDags.BPVertices.BPElementConsumerFactory;
import com.example.bpme.BpmnDags.BPVertices.BPElementConsumer;
import com.example.bpme.common.BpmnParser;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Component("BpmnToDagService")
public class BpmnToDagService implements BpmnToDagAdapter {


    public BpmnToDagService() throws ParserConfigurationException {
    }

    @Override
    public List<Resource> convertFilesToDags(List<Resource> bpmnFiles) {
        return bpmnFiles.stream().map(this::transformBpmnToGraphML).toList();
    }


    private ArrayList<Node> convertNodeListToArray(NodeList nodeList){
        ArrayList<Node> nodeArrayList = new ArrayList<>();
        for(int i =0;i<nodeList.getLength();i++){
            nodeArrayList.add(nodeList.item(i));
        }
        return nodeArrayList;
    }



    public Resource transformBpmnToGraphML(Resource bpmnFile){
        //CORE logic
        try (Graph graph = TinkerGraph.open()) {
            String contentsOfFile = bpmnFile.getContentAsString(Charset.defaultCharset());
            BpmnParser bpmnParser = new BpmnParser(contentsOfFile,bpmnFile.getFilename());
            NodeList participants = bpmnParser.getParticipantsOfCollaboration();
            Node startNode;
            if(participants.getLength()==0){
                startNode = bpmnParser.findStartNode(null);
            }else{
                String nameOfWhiteBoxProcess = bpmnParser.findNameOfSoleWhiteBoxPool().getAttributes().getNamedItem("name").getTextContent();
                startNode = bpmnParser.findStartNode(nameOfWhiteBoxProcess);
            }
            System.out.println("Converting diagram to graphml");
            System.out.println("Start node of: " + bpmnFile.getFilename() + " is: " + bpmnParser.findStartNode(null).getTextContent());
            //For simple scenarios this works
            LinkedList<Node> toVisitQueue = new LinkedList<>();
            ArrayList<Node> visitedNodes = new ArrayList<>();
            LinkedList<Vertex> toVisitVertices = new LinkedList<>();
            toVisitQueue.offer(startNode);
            BPElementConsumer startElConsumer =  BPElementConsumerFactory.createFromNode(startNode,bpmnParser);
            Vertex startVertex = startElConsumer.addToGraphWithPrev(graph,null);
            toVisitVertices.offer(startVertex);

            while(!toVisitQueue.isEmpty()){
                Node currentNode = toVisitQueue.pop();
                Vertex prevVertex = toVisitVertices.pop();
                visitedNodes.add(currentNode);
                ArrayList<Node> nextNodes = bpmnParser.getNextNodes(currentNode);
                for(Node nextNode:nextNodes){
                    toVisitQueue.offer(nextNode);
                    BPElementConsumer nextVertex = BPElementConsumerFactory.createFromNode(nextNode,bpmnParser);
                    Vertex v = nextVertex.addToGraphWithPrev(graph,prevVertex);
                    toVisitVertices.offer(v);
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

// Create a GraphMLWriter instance
            GraphMLWriter writer = GraphMLWriter.build().create();

// Write the graph to GraphML format
            writer.writeGraph(outputStream, graph);

// Convert the ByteArrayOutputStream to a string using the specified character encoding
            String graphmlString = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println(graphmlString);
            //Now we can do the iteration starting from this node

//            System.out.println("These are the contents of the file: " + contentsOfFile);


        } catch (Exception e) {
            System.out.println("======================");
            System.out.println(e.getCause()+" " + e.getLocalizedMessage());
        }

        return null;
    }

    private void addGraphNodesFromBpmnElement(Graph g,Node n){

    }
    
}
