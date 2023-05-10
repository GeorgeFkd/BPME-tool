package com.example.bpme;

import jdk.swing.interop.SwingInterOpUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.print.DocFlavor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MetricUtils {

    public static final ArrayList<String> supportedMetrics = new ArrayList<>(
            Arrays.asList("CFC","AGD","NSFG","NOA","NOAJS","DENSITY","CNC","GH","MGD",
                    "CLA","TNG","TS","NSFE","NSFA","GM")
    );

    private static final String BPMN_XOR = "exclusiveGateway";
    private static final String BPMN_AND = "parallelGateway";
    private static final String BPMN_START = "startEvent";
    private static final String BPMN_END = "endEvent";
    private static final String BPMN_OR = "inclusiveGateway";
    private static final String BPMN_EVENT_BASED = "eventBasedGateway";
    private static final String BPMN_INCOMING = "incoming";
    private static final String BPMN_OUTGOING = "outgoing";
    private static final String BPMN_SEQUENCE_FLOW = "sequenceFlow";
    DocumentBuilder domBuilder;
    XPath xpathObj;
    public MetricUtils() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        this.domBuilder = builderFactory.newDocumentBuilder();
        this.xpathObj = XPathFactory.newInstance().newXPath();
    }



    public NodeList getParticipants(String xmlString){
        return null;
    }

    public double AGD(String xmlString,double ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();

        int sumOfFlowsFromGateways = gateways.stream().map((gateway -> {
            String expression = "./*";
            try {
                NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(gateway,XPathConstants.NODESET);
                System.out.println("IN AGD: " + nodeList.getLength());
                ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,(elemName)->elemName.equals("incoming") || elemName.equals("outgoing"));
                System.out.println("IN ARRLIST " + nodes.size());

                return nodes.size();
            } catch (XPathExpressionException e) {
                System.out.println("PROBLEM WITH XPATH " + e.getCause() + " --" + e.getMessage());
                return (int) ErrValue;
            }

        })).reduce(0,(a,b) -> a + b );
        if(gateways.size() == 0) return ErrValue;

        return sumOfFlowsFromGateways/gateways.size();
    }

    public int CFC(String xmlString,int ErrValue){
//        return NOA(xmlString,0)/NSFA(xmlString,-1);
        String xorGatewayTagName = "exclusiveGateway";
        String orGatewayTagName = "inclusiveGateway";
        String andGatewayTagName = "parallelGateway";
        String eventBasedGatewayTagName = "eventBasedGateway";
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
        List<Node> xorGateways = gateways.stream().filter((node-> node.getNodeName().split(":")[1].equals(xorGatewayTagName))).collect(Collectors.toList());
        List<Node> andGateways = gateways.stream().filter((node-> node.getNodeName().split(":")[1].equals(andGatewayTagName))).collect(Collectors.toList());
        List<Node> orGateways = gateways.stream().filter((node-> node.getNodeName().split(":")[1].equals(orGatewayTagName))).collect(Collectors.toList());
        List<Node> eventGateways = gateways.stream().filter((node-> node.getNodeName().split(":")[1].equals(eventBasedGatewayTagName))).collect(Collectors.toList());


        int cfcXor = xorGateways.stream().reduce(0,(total,gatewayNode)->{
            //see if gateway is a split node
            NodeList childrenOfGateway = null;
            try {
                childrenOfGateway = (NodeList) xpathObj.compile(".//*").evaluate(gatewayNode, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                return (int) ErrValue;
            }
            int incoming = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
            int outgoing = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
            if(incoming < outgoing){
                //is split node
                return total + outgoing;
            } else {
                return total;
            }






        },Integer::sum);
        int cfcEventBased = eventGateways.stream().reduce(0,(total,gatewayNode)->{
            //see if gateway is a split node
            NodeList childrenOfGateway = null;
            try {
                childrenOfGateway = (NodeList) xpathObj.compile(".//*").evaluate(gatewayNode, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                return (int) ErrValue;
            }
            int incoming = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
            int outgoing = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
            if(incoming < outgoing){
                //is split node
                return total + outgoing;
            } else {
                return total;
            }
        },Integer::sum);

        int cfcOR = orGateways.stream().reduce(0,(total,gatewayNode)-> {
            //see if gateway is a split node
            NodeList childrenOfGateway = null;
            try {
                childrenOfGateway = (NodeList) xpathObj.compile(".//*").evaluate(gatewayNode, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                return (int) ErrValue;
            }
            int incoming = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
            int outgoing = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
            if (incoming < outgoing) {
                //is split node
                return total + (int) Math.pow(2,outgoing) - 1 ;
            } else {
                return total;
            }
        },Integer::sum);

        //and gateway doesn't have separate possible scenarios
        int cfcAnd = andGateways.size();


        return cfcAnd + cfcOR + cfcEventBased + cfcXor;
    }

    public double CLA(String xmlString,double ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        int Noa = NOA(xmlString,-1);
        int Nsfa = NSFA(xmlString,-1);
        if(Noa == -1 || Nsfa == -1 ){ return ErrValue;}
        if(Nsfa == 0) return ErrValue;

        return Noa/(double) Nsfa;

    }

    public Optional<ArrayList<Node>> getAllSequenceFlowsArr(String xmlString){
        String sequenceFlowsExpression = "/definitions//*/*";
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        try {
            NodeList nodes = (NodeList) xpathObj.compile(sequenceFlowsExpression).evaluate(xmlDoc,XPathConstants.NODESET);
            return Optional.of(getNodesWithLocalNameThatPassesPredicate(nodes,(elemName)->elemName.equals("sequenceFlow")));
        } catch (XPathExpressionException e) {

            return Optional.empty();

        }
    }

    public Optional<NodeList> getAllSequenceFlows(String xmlString){
        String sequenceFlowsExpression = "/definitions//*/sequenceFlow";
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        NodeList sequenceFlowsNodeList = null;
        try {
            sequenceFlowsNodeList = (NodeList) xpathObj.compile(sequenceFlowsExpression).evaluate(xmlDoc, XPathConstants.NODESET);
            return Optional.of(sequenceFlowsNodeList);
        } catch (XPathExpressionException e) {
            System.out.println(e);
            System.out.println("all sfs list " + e.getCause() + "--" + e.getMessage());
            return Optional.empty();
        }
    }


    public double CNC(String xmlString,double ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        int arcs = getAllSequenceFlows(xmlString).orElseThrow().getLength();
        int events = getEventsInDiagram(xmlString).orElseThrow().size();
        int nodes = NOA(xmlString,0) + TNG(xmlString,0) + events;
        if(nodes == 0) return ErrValue;
        System.out.println("Arcs = " + arcs + " Nodes= " + nodes);

        return arcs/(double) nodes;

    }

    public int sequenceFlowsInDiagram(String xmlString,int ErrValue){
        return getAllSequenceFlows(xmlString).orElseThrow().getLength();
    }

    public double DENSITY(String xmlString,double ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        int arcs = getAllSequenceFlows(xmlString).orElseThrow().getLength();
        int events = getEventsInDiagram(xmlString).orElseThrow().size();
        int nodes = NOA(xmlString,0) + TNG(xmlString,0) + events;
        if(nodes == 0) return ErrValue;
        System.out.println("Arcs = " + arcs + " Nodes= " + nodes);
        int divWith = nodes * (nodes-1);
        return arcs/(double) divWith;
    }

    public double GH(String xmlString,double ErrValue){
            ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
            int numberOfGatewaysInDiagram = gateways.size();
            if(numberOfGatewaysInDiagram == 0 ) return ErrValue;
            HashMap<String,Integer> countGatewaysPerType = new HashMap<>();
            for(Node n: gateways){
                String typeOfGateway = n.getNodeName().split(":")[1];
                int prevValue = countGatewaysPerType.getOrDefault(typeOfGateway,0);
                countGatewaysPerType.put(typeOfGateway,prevValue + 1);
            }
            double sum = 0;
            for(Integer val : countGatewaysPerType.values()){
                double percentageOfTotal = val /(double) numberOfGatewaysInDiagram;
                sum += -1 * (Math.log(percentageOfTotal)/Math.log(3)) * percentageOfTotal;
            }
            return sum;
    }
    public double GM(String xmlString,double ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        String xorGatewayTagName = "exclusiveGateway";
        String orGatewayTagName = "inclusiveGateway";
        String andGatewayTagName = "parallelGateway";
        String eventBasedGatewayTagName = "eventBasedGateway";
        System.out.println("WELCOME TO GM");
        String[] gatewayTypes= {xorGatewayTagName,orGatewayTagName,andGatewayTagName,eventBasedGatewayTagName};
        NodeList allNodes = null;
        int totalGM = 0;
        try {
            allNodes = (NodeList) xpathObj.compile("//*").evaluate(xmlDoc,XPathConstants.NODESET);


        ArrayList<Node> gatewaysOfDiagram = getGatewaysInDiagram(xmlString).orElseThrow();
        for(String gateType: gatewayTypes){

            List<Node> gatewaysOfType = gatewaysOfDiagram.stream().filter((node) -> node.getNodeName().split(":")[1].equals(gateType)).collect(Collectors.toList());
            System.out.println(gatewaysOfType.size());
            int gmOfType = gatewaysOfType.stream().reduce(0,(total,node)-> {
                NodeList childrenOfGateway = null;
                try {
                    childrenOfGateway = (NodeList) xpathObj.compile(".//*").evaluate(node, XPathConstants.NODESET);
                } catch (XPathExpressionException e) {
                    return (int) ErrValue;
                }
                int incoming = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
                int outgoing = getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
                System.out.println("Found this--> " + incoming +"  " +outgoing +"<----");
                if (incoming > outgoing) {
                    //merge node
                    return total - incoming;
                }else if (incoming == outgoing) {
                    return total;
                }else {
                    return total + outgoing;
                }
            },Integer::sum);

            System.out.println("FOUND RESULT: " + gmOfType + "-----" + gateType );
            totalGM += Math.abs(gmOfType);
        }
        } catch (XPathExpressionException e) {
            System.out.println("xpath gm: " + e.getCause() + "--" + e.getMessage());
            return ErrValue;
        }

        return totalGM;
    }

    public double MGD(String xmlString,double ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        //thelw ola ta gateways
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
        //meta metraw posa flows exei to kathena
        System.out.println(gateways);
        ArrayList<Integer> gatewaysDegrees = new ArrayList<>();
        for(Node n: gateways){
            String expressionForChildren = "./*";
            try {
                NodeList children = (NodeList) xpathObj.compile(expressionForChildren).evaluate(n,XPathConstants.NODESET);
                System.out.println("HELOOOOO---->" + children.getLength());
                int flowsOfNode = getNodesWithLocalNameThatPassesPredicate(children,(elemName)-> elemName.equals("incoming") || elemName.equals("outgoing")).size();
                gatewaysDegrees.add(gateways.indexOf(n),flowsOfNode);
            } catch (XPathExpressionException e) {
                System.out.println("XPATH MGD " + e.getCause() + "--" + e.getMessage());
                return ErrValue;
            }
        }
        //pairnw to megisto
        System.out.println(gatewaysDegrees);
        return gatewaysDegrees.stream().max((num1,num2)-> num1 - num2).orElseThrow();


    }

    public int NMF(String xmlString,int ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        String expression = "//*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,(elemName) -> elemName.equals("messageFlow"));
            return nodes.size();
        } catch (XPathExpressionException e) {
            System.out.println("NMF " + e.getCause() + "--" + e.getMessage());
            return ErrValue;
        }
    }

    public int NOA(String xmlString,int ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        String expression = ".//*";
        try {
            NodeList totalNodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc, XPathConstants.NODESET);
            System.out.println("NOA WORKING WITH this many nodes: " + totalNodeList.getLength());
            ArrayList<Node> activitiesNodeList = getNodesWithLocalNameThatPassesPredicate(totalNodeList,(elemName)->elemName.matches(".*(t|T)ask"));
            return activitiesNodeList.size();
        } catch (XPathExpressionException e) {
            System.out.println(e);
            return ErrValue;
        }
    }

    public int NOAJS(String xmlString,int ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        return NOA(xmlString,0) + TNG(xmlString,0);

    }

    public Optional<ArrayList<Node>> getAllActivitiesOfCompleteModel(String xmlString){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        try {
            NodeList allNodes = (NodeList) xpathObj.compile(".//*").evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> allActivities = getNodesWithLocalNameThatPassesPredicate(allNodes,(localName)->localName.matches(".*(t|T)ask"));
            System.out.println("All activities are:" + allActivities);
            return Optional.of(allActivities);
        } catch (XPathExpressionException e) {
            return Optional.empty();
//            throw new RuntimeException(e);
        }
    }


    public int NSFA(String xmlString,int ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        ArrayList<Node> activitiesInDiagram = getAllActivitiesOfCompleteModel(xmlString).orElseThrow();
        System.out.println("How many activities in diagram: " + activitiesInDiagram.size());
        int sum = 0;
        ArrayList<Node> sequenceFlowsInDiagram = getAllSequenceFlowsArr(xmlString).orElseThrow();
        for(Node n: sequenceFlowsInDiagram){
            String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
            String targetRef = n.getAttributes().getNamedItem("targetRef").getNodeValue();



            try {
                Node sourceNode = (Node) xpathObj.compile("//*[@id='"  + sourceRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String sourceNodeName = sourceNode.getNodeName().split(":")[1];

                Node targetNode = (Node) xpathObj.compile("//*[@id='"  + targetRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String targetNodeName = targetNode.getNodeName().split(":")[1];
//                System.out.println("NODE NAMES: " + targetNodeName + "---" + sourceNodeName);
                if(sourceNodeName.matches(".*(t|T)ask") && targetNodeName.matches(".*(t|T)ask")){
                    sum += 1;
                    System.out.println("FROM: " + sourceRef + "TO: " + targetRef);
                    System.out.println("N1: " + sourceNodeName + " TO: " + targetNodeName);
                }

            } catch (XPathExpressionException e) {
                System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
            }
        }
        return sum;
//        for(int i = 0; i < activitiesInDiagram.size()-1; i++){
//            for(int j = i + 1; j< activitiesInDiagram.size();j++){
//
//                Node activity1 = activitiesInDiagram.get(i);
//                Node activity2 = activitiesInDiagram.get(j);
//                try {
//                    NodeList childNodesOfFirst = (NodeList) xpathObj.compile("./*").evaluate(activity1,XPathConstants.NODESET);
//                    NodeList childNodesOfSecond = (NodeList) xpathObj.compile("./*").evaluate(activity2,XPathConstants.NODESET);
//
//                    ArrayList<Node> outgoingOfFirst = getNodesWithLocalNameThatPassesPredicate(childNodesOfFirst,(elemName)->elemName.equals("outgoing"));
//                    ArrayList<Node> outgoingOfSecond = getNodesWithLocalNameThatPassesPredicate(childNodesOfSecond,(elemName)->elemName.equals("outgoing"));
//
//                    ArrayList<Node> incomingOfFirst = getNodesWithLocalNameThatPassesPredicate(childNodesOfFirst,(elemName)->elemName.equals("incoming"));
//                    ArrayList<Node> incomingOfSecond = getNodesWithLocalNameThatPassesPredicate(childNodesOfSecond,(elemName)->elemName.equals("incoming"));
//
//
//
//                    if(outgoingOfFirst.size() != 0 && incomingOfSecond.size() !=0 ){
//                        for(Node outgoing: outgoingOfFirst){
//                            for(Node incoming: incomingOfSecond){
//                                if(outgoing.getTextContent().equals(incoming.getTextContent())){
//                                    sum += 1;
//                                    System.out.println(outgoing.getNodeName());
//
//                                }
//                            }
//                        }
//                    }
//                    if(incomingOfFirst.size() != 0 && outgoingOfSecond.size() !=0 ){
//                        for(Node outgoing: outgoingOfSecond){
//                            for(Node incoming: incomingOfSecond){
//                                if(outgoing.getTextContent().equals(incoming.getTextContent())){
//                                    sum += 1;
//                                    System.out.println(outgoing.getTextContent());
//                                }
//                            }
//                        }
//                    }
//
//
//                } catch (XPathExpressionException e) {
//                    System.out.println("ERROR IN NSFA: " + e.getCause() + "--" + e.getMessage());
//                    return ErrValue;
//
//                }
//
//            }
//        }
//        return sum;

    }
    public int NSFE(String xmlString,int ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        ArrayList<Node> activitiesInDiagram = getAllActivitiesOfCompleteModel(xmlString).orElseThrow();
        System.out.println("How many activities in diagram: " + activitiesInDiagram.size());
        int sum = 0;
        ArrayList<Node> sequenceFlowsInDiagram = getAllSequenceFlowsArr(xmlString).orElseThrow();
        for(Node n: sequenceFlowsInDiagram){
            String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
            try {
                Node sourceNode = (Node) xpathObj.compile("//*[@id='"  + sourceRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String sourceNodeName = sourceNode.getNodeName().split(":")[1];
                if(sourceNodeName.matches(".+Event$")){
                    sum += 1;
                }

            } catch (XPathExpressionException e) {
                System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
            }
        }
        return sum;
    }
    public int NSFG(String xmlString,int ErrValue){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        ArrayList<Node> activitiesInDiagram = getAllActivitiesOfCompleteModel(xmlString).orElseThrow();
        System.out.println("How many activities in diagram: " + activitiesInDiagram.size());
        int sum = 0;
        ArrayList<Node> sequenceFlowsInDiagram = getAllSequenceFlowsArr(xmlString).orElseThrow();
        for(Node n: sequenceFlowsInDiagram){
            String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
            try {
                Node sourceNode = (Node) xpathObj.compile("//*[@id='"  + sourceRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String sourceNodeName = sourceNode.getNodeName().split(":")[1];
                if(sourceNodeName.matches(".+Gateway$")){
                    sum += 1;
                }
            } catch (XPathExpressionException e) {
                System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
            }
        }
        return sum;
    }
    public int TNG(String xmlString,int ErrValue){
        //ENDS-WITH IS NOT VALID IN XPATH
        return getGatewaysInDiagram(xmlString).orElseThrow().size();
    }
    public int TS(String xmlString){
        return -1;
    }


    private Optional<Document> ParseXmlStringToDocument(String xmlString){

        try {
            Document xmlDoc = domBuilder.parse(new InputSource(new StringReader(xmlString)));
            return Optional.of(xmlDoc);
        } catch (SAXException e) {
            System.out.println("Sax Exception");

            return Optional.empty();
        } catch (IOException e) {
            System.out.println("IO exception");
            return Optional.empty();
        }
    }

    private Optional<ArrayList<Node>> getEventsInDiagram(String xmlString){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        //to matches kai to end-with den yparxoun sto specification
//        String expression = ".//*[matches(local-name(),'.+Event$')]";
        String expression = ".//*";
        try {

            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            Predicate<String> isEventElement = elemName -> elemName.matches(".+Event$");
            ArrayList<Node> nodesWithEventLikeName = getNodesWithLocalNameThatPassesPredicate(nodeList,isEventElement);
            return Optional.of(nodesWithEventLikeName);
        } catch (XPathExpressionException e) {
            System.out.println("Error in xpath-->" + e);
            return Optional.empty();
        }
    }

    private Optional<ArrayList<Node>> getGatewaysInDiagram(String xmlString){
        Document xmlDoc = ParseXmlStringToDocument(xmlString).orElseThrow();
        String expression = "/definitions//*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> gatewayNodes = new ArrayList<>();
            for(int i = 0; i< nodeList.getLength(); i++){
                Node current = nodeList.item(i);

                if(current.getNodeType() == Node.ELEMENT_NODE){
                    String localName = current.getNodeName().split(":")[1];
                    if(localName.endsWith("Gateway")) gatewayNodes.add(current);
                }
            }
            return Optional.of(gatewayNodes);
        } catch (XPathExpressionException e) {
            System.out.println("Error in xpath:" + e.getCause());
            return Optional.empty();
        }
    }


    private ArrayList<Node> getNodesWithLocalNameThatPassesPredicate(NodeList nodeList,Predicate<String> predicate){

        ArrayList<Node> nodesThatMatchPredicate = new ArrayList<>();
        for(int i = 0; i< nodeList.getLength(); i++){
            Node current = nodeList.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE){
                //System.out.println("NODE: " + current.getNodeName() + "LOCAL: " + current.getLocalName());
                String localName = current.getNodeName().split(":")[1];
                if(predicate.test(localName)) nodesThatMatchPredicate.add(current);
//                if(current.getLocalName() != null){
//                    System.out.println("ELEM LOCAL NAME: " + current.getLocalName());
//                    if(predicate.test(current.getLocalName())) {
//
//                        nodesThatMatchPredicate.add(current);
//                    }
//
//                }
                // System.out.println(current.getLocalName());
            }
        }
        return nodesThatMatchPredicate;

    }

    private int getAmountOfOutgoingFlowsFromNode(Node node){
        String expression = "./*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(node,XPathConstants.NODESET);
            ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,(elemName)->elemName.equals("outgoing"));
            return nodes.size();
        } catch (XPathExpressionException e) {
            return -1;
        }
    }


}
