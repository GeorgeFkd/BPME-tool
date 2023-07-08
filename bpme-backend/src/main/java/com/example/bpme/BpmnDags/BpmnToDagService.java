package com.example.bpme.BpmnDags;

import com.example.bpme.common.BpmnParser;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
            Stack<Node> toVisit = new Stack<>();
            toVisit.push(startNode);
            while(!toVisit.isEmpty()){
                Node currentNode = toVisit.pop();
                ArrayList<Node> nextNodes = convertNodeListToArray(bpmnParser.findNextNodes(currentNode));
                for(Node nextNode:nextNodes){
                    toVisit.push(nextNode);
                }
            }
            //Now we can do the iteration starting from this node

//            System.out.println("These are the contents of the file: " + contentsOfFile);
            System.out.println("Converting diagram to graphml");
            System.out.println("Start node of: " + bpmnFile.getFilename() + " is: " + bpmnParser.findStartNode(null).getTextContent());
        } catch (Exception e) {
            System.out.println("======================");
            System.out.println(e.getCause()+" " + e.getLocalizedMessage());
        }

        return null;
    }
    
}
