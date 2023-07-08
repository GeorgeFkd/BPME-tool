package com.example.bpme.BpmnDags.BPVertices;

import com.example.bpme.BpmnDags.BPVertices.events.EndEvent;
import com.example.bpme.BpmnDags.BPVertices.events.StartEvent;
import com.example.bpme.BpmnDags.BPVertices.gateways.EventBasedGateway;
import com.example.bpme.BpmnDags.BPVertices.gateways.InclusiveGateway;
import com.example.bpme.BpmnDags.BPVertices.gateways.ParallelGateway;
import com.example.bpme.BpmnDags.BPVertices.gateways.XorGateway;
import com.example.bpme.BpmnDags.BPVertices.tasks.NormalTask;
import com.example.bpme.common.BpmnParser;
import org.w3c.dom.Node;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class BPElementConsumerFactory {


    public static boolean isActivity(Node n,BpmnParser parser){
        String parsedType = parser.getLocalNameFromXmlNodeName(n.getNodeName());
        if(parsedType.equals("task") || parsedType.equals("subProcess") || parsedType.equals("callActivity") || parsedType.equals("transaction")){
            return true;
        }
        return false;
    }

    public static boolean isGateway(Node n,BpmnParser parser){
        String parsedType = parser.getLocalNameFromXmlNodeName(n.getNodeName());
        if(parsedType.contains("Gateway")){
            return true;
        }
        return false;
    }

    public static boolean isEvent(Node n,BpmnParser parser){
        String parsedType = parser.getLocalNameFromXmlNodeName(n.getNodeName());
        if(parsedType.contains("Event")){
            return true;
        }
        return false;
    }

    public static BPElementConsumer createFromNode(Node n, BpmnParser parser){
        //Depending on the nodes type, create a new BPVertex
        //For boundary Events i need to check if there are no elements of Type BoundaryEvent with attatchedToRef the same as the current node
        if(isActivity(n,parser)){
            //check if there are boundary events
            ArrayList<Node> boundaryEvents = parser.findNodesWithTagName("boundaryEvent");
            String idOfActivity = n.getAttributes().getNamedItem("id").getTextContent();
            Node boundaryEventOfActivity = boundaryEvents.stream().filter(node->node.getAttributes().getNamedItem("attachedToRef").getTextContent().equals(idOfActivity)).findFirst().orElse(null);
            if(boundaryEventOfActivity==null) {
                //it is a normal task
                return new NormalTask(n, parser);
            }else{
                //it is a task with boundary event

            }

        }else if (isGateway(n,parser)){
            //get the type of Gateway
            String gatewayType = parser.getLocalNameFromXmlNodeName(n.getNodeName());
            String actualType = substringBefore(gatewayType,"Gateway");
            //TODO case complex gateway
            switch (actualType) {
                case "exclusive":
                    //create exclusive gateway
                    return new XorGateway(n,parser);
                case "inclusive":
                    //create inclusive gateway
                    return new InclusiveGateway(n,parser);
                case "parallel":
                    //create parallel gateway
                    return new ParallelGateway(n,parser);

                case "event":
                    //create eventBased gateway
                    return new EventBasedGateway(n,parser);
                default:
                    //create default gateway
                    break;
            }
        }else if (isEvent(n,parser)){
            String eventTagName = parser.getLocalNameFromXmlNodeName(n.getNodeName());

            String actualType = substringBefore(eventTagName,"Event");
            switch (actualType) {
                case "start":
                    //create start event
                    return new StartEvent(n,parser);
                case "end":
                    //create end event
                    return new EndEvent(n,parser);
                case "intermediate":
                    //create intermediate event
                    break;
                default:
                    //create default event
                    break;
            }
            return new StartEvent(n,parser);
        }

        ArrayList<Node> allNodes = parser.getAllXmlElementsOfFile();

        return null;
    }
}
