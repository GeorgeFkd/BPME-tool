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

    private ArrayList<BPMetric> metrics = new ArrayList<>();

    public ArrayList<BPMetric> getMetrics() {
        return metrics;
    }
    DocumentBuilder domBuilder;
    XPath xpathObj;
    public MetricUtils() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        this.domBuilder = builderFactory.newDocumentBuilder();
        this.xpathObj = XPathFactory.newInstance().newXPath();

        MetricUtils metricUtils = this;
        this.metrics.add(new BPMetric("CFC","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.CFC(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("AGD","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.AGD(xmlString,-1);
            }
        });
        this.metrics.add(new BPMetric("NSFG","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.NSFG(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("NOA","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.NOA(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("NOAJS","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.NOAJS(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("DENSITY","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.DENSITY(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("CNC","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.CNC(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("GH","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.GH(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("MGD","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.MGD(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("CLA","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.CLA(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("TNG","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.TNG(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("TS","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.TS(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("NSFE","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.NSFE(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("NSFA","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.NSFA(xmlString,-1);
            }
        });

        this.metrics.add(new BPMetric("GM","",0,-1) {
            @Override
            public Number calculateMetric(String xmlString) throws IOException, SAXException {
                return metricUtils.GM(xmlString,-1);
            }
        });



    }



    public NodeList getParticipants(String xmlString){
        return null;
    }

    public double AGD(String xmlString,double ErrValue) throws IOException, SAXException {
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();

        int sumOfFlowsFromGateways = gateways.stream().map((gateway -> {
            String expression = "./*";
            try {
                NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(gateway,XPathConstants.NODESET);
                ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,(elemName)->elemName.equals("incoming") || elemName.equals("outgoing"));

                return nodes.size();
            } catch (XPathExpressionException e) {
                System.out.println("PROBLEM WITH XPATH " + e.getCause() + " --" + e.getMessage());
                return (int) ErrValue;
            }

        })).reduce(0,(a,b) -> a + b );
        if(gateways.size() == 0) return ErrValue;

        return sumOfFlowsFromGateways/gateways.size();
    }

    public int CFC(String xmlString,int ErrValue) throws IOException, SAXException {
//        return NOA(xmlString,0)/NSFA(xmlString,-1);
        String xorGatewayTagName = "exclusiveGateway";
        String orGatewayTagName = "inclusiveGateway";
        String andGatewayTagName = "parallelGateway";
        String eventBasedGatewayTagName = "eventBasedGateway";
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
        List<Node> xorGateways = gateways.stream().filter((node-> getLocalNameFromXmlNodeName(node.getNodeName()).equals(xorGatewayTagName))).collect(Collectors.toList());
        List<Node> andGateways = gateways.stream().filter((node-> getLocalNameFromXmlNodeName(node.getNodeName()).equals(andGatewayTagName))).collect(Collectors.toList());
        List<Node> orGateways = gateways.stream().filter((node-> getLocalNameFromXmlNodeName(node.getNodeName()).equals(orGatewayTagName))).collect(Collectors.toList());
        List<Node> eventGateways = gateways.stream().filter((node-> getLocalNameFromXmlNodeName(node.getNodeName()).equals(eventBasedGatewayTagName))).collect(Collectors.toList());


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

    public double CLA(String xmlString,double ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        int Noa = NOA(xmlString,-1);
        int Nsfa = NSFA(xmlString,-1);
        if(Noa == -1 || Nsfa == -1 ){ return ErrValue;}
        if(Nsfa == 0) return ErrValue;

        return Noa/(double) Nsfa;

    }

    public Optional<ArrayList<Node>> getAllSequenceFlowsArr(String xmlString) throws IOException, SAXException {
        String sequenceFlowsExpression = "/definitions//*/*";
        try {
            Document xmlDoc = ParseXmlStringToDocument(xmlString);
            NodeList nodes = (NodeList) xpathObj.compile(sequenceFlowsExpression).evaluate(xmlDoc,XPathConstants.NODESET);
            return Optional.of(getNodesWithLocalNameThatPassesPredicate(nodes,(elemName)->elemName.equals("sequenceFlow")));
        } catch (XPathExpressionException e) {

            return Optional.empty();

        }
    }




    public Optional<NodeList> getAllSequenceFlows(String xmlString) throws IOException, SAXException {
        String sequenceFlowsExpression = "/definitions//*/sequenceFlow";
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
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


    public  double CNC(String xmlString,double ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        int arcs = getAllSequenceFlows(xmlString).orElseThrow().getLength();
        int events = getEventsInDiagram(xmlString).orElseThrow().size();
        int nodes = NOA(xmlString,0) + TNG(xmlString,0) + events;
        if(nodes == 0) return ErrValue;

        return arcs/(double) nodes;

    }

    public  int sequenceFlowsInDiagram(String xmlString,int ErrValue) throws IOException, SAXException {
        return getAllSequenceFlows(xmlString).orElseThrow().getLength();
    }

    public  double DENSITY(String xmlString,double ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        int arcs = getAllSequenceFlows(xmlString).orElseThrow().getLength();
        int events = getEventsInDiagram(xmlString).orElseThrow().size();
        int nodes = NOA(xmlString,0) + TNG(xmlString,0) + events;
        if(nodes == 0) return ErrValue;
        int divWith = nodes * (nodes-1);
        return arcs/(double) divWith;
    }

    public double GH(String xmlString,double ErrValue) throws IOException, SAXException {
            ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
            int numberOfGatewaysInDiagram = gateways.size();
            if(numberOfGatewaysInDiagram == 0 ) return ErrValue;
            HashMap<String,Integer> countGatewaysPerType = new HashMap<>();
            for(Node n: gateways){
                String typeOfGateway = getLocalNameFromXmlNodeName(n.getNodeName());
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
    public double GM(String xmlString,double ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        String xorGatewayTagName = "exclusiveGateway";
        String orGatewayTagName = "inclusiveGateway";
        String andGatewayTagName = "parallelGateway";
        String eventBasedGatewayTagName = "eventBasedGateway";
        String[] gatewayTypes= {xorGatewayTagName,orGatewayTagName,andGatewayTagName,eventBasedGatewayTagName};
        NodeList allNodes = null;
        int totalGM = 0;
        try {
            allNodes = (NodeList) xpathObj.compile("//*").evaluate(xmlDoc,XPathConstants.NODESET);


        ArrayList<Node> gatewaysOfDiagram = getGatewaysInDiagram(xmlString).orElseThrow();
        for(String gateType: gatewayTypes){

            List<Node> gatewaysOfType = gatewaysOfDiagram.stream().filter((node) -> getLocalNameFromXmlNodeName(node.getNodeName()).equals(gateType)).collect(Collectors.toList());
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
                if (incoming > outgoing) {
                    //merge node
                    return total - incoming;
                }else if (incoming == outgoing) {
                    return total;
                }else {
                    return total + outgoing;
                }
            },Integer::sum);

            totalGM += Math.abs(gmOfType);
        }
        } catch (XPathExpressionException e) {
            System.out.println("xpath gm: " + e.getCause() + "--" + e.getMessage());
            return ErrValue;
        }

        return totalGM;
    }

    public double MGD(String xmlString,double ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        //thelw ola ta gateways
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
        //meta metraw posa flows exei to kathena
        System.out.println(gateways);
        ArrayList<Integer> gatewaysDegrees = new ArrayList<>();
        for(Node n: gateways){
            String expressionForChildren = "./*";
            try {
                NodeList children = (NodeList) xpathObj.compile(expressionForChildren).evaluate(n,XPathConstants.NODESET);
                int flowsOfNode = getNodesWithLocalNameThatPassesPredicate(children,(elemName)-> elemName.equals("incoming") || elemName.equals("outgoing")).size();
                gatewaysDegrees.add(gateways.indexOf(n),flowsOfNode);
            } catch (XPathExpressionException e) {
                System.out.println("XPATH MGD " + e.getCause() + "--" + e.getMessage());
                return ErrValue;
            }
        }
        //pairnw to megisto
        System.out.println(gatewaysDegrees);
        Optional<Integer> res = gatewaysDegrees.stream().max((num1,num2)-> num1 - num2);
        if(res.isPresent())
            return res.get();

        return 0;


    }

    public int NMF(String xmlString,int ErrValue) throws IOException, SAXException {
        //generally a problematic metric
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
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

    public int NOA(String xmlString,int ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        String expression = ".//*";
        try {
            NodeList totalNodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc, XPathConstants.NODESET);
            ArrayList<Node> activitiesNodeList = getNodesWithLocalNameThatPassesPredicate(totalNodeList,(elemName)->elemName.matches(".*(t|T)ask"));
            return activitiesNodeList.size();
        } catch (XPathExpressionException e) {
            System.out.println(e);
            return ErrValue;
        }
    }

    public  int NOAJS(String xmlString,int ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        return NOA(xmlString,0) + TNG(xmlString,0);

    }

    public Optional<ArrayList<Node>> getAllActivitiesOfCompleteModel(String xmlString) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        try {
            NodeList allNodes = (NodeList) xpathObj.compile(".//*").evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> allActivities = getNodesWithLocalNameThatPassesPredicate(allNodes,(localName)->localName.matches(".*(t|T)ask"));
            return Optional.of(allActivities);
        } catch (XPathExpressionException e) {
            return Optional.empty();
//            throw new RuntimeException(e);
        }
    }


    public int NSFA(String xmlString,int ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        int sum = 0;
        ArrayList<Node> sequenceFlowsInDiagram = getAllSequenceFlowsArr(xmlString).orElseThrow();
        System.out.println("Found " + sequenceFlowsInDiagram.size() + " Sequence Flows in Diagram");
        for(Node n: sequenceFlowsInDiagram){
            String sourceRef;
            String targetRef;
            try{
                sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
                targetRef = n.getAttributes().getNamedItem("targetRef").getNodeValue();
                System.out.println(sourceRef + "\t" + targetRef);
            }catch (NullPointerException e){
                return -1;
            }
            try {
                Node sourceNode = (Node) xpathObj.compile("//*[@id='"  + sourceRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String sourceNodeName = getLocalNameFromXmlNodeName(sourceNode.getNodeName());
                Node targetNode = (Node) xpathObj.compile("//*[@id='"  + targetRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String targetNodeName = getLocalNameFromXmlNodeName(targetNode.getNodeName());
                if(sourceNodeName.matches(".*(t|T)ask") && targetNodeName.matches(".*(t|T)ask")){
                    sum += 1;
//                    System.out.println("FROM: " + sourceRef + "TO: " + targetRef);
//                    System.out.println("N1: " + sourceNodeName + " TO: " + targetNodeName);
                }

            } catch (XPathExpressionException e) {
                System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
            }
        }
        return sum;
    }
    public  int NSFE(String xmlString,int ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        int sum = 0;
        ArrayList<Node> sequenceFlowsInDiagram = getAllSequenceFlowsArr(xmlString).orElseThrow();
        for(Node n: sequenceFlowsInDiagram){

            try {
                String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
                Node sourceNode = (Node) xpathObj.compile("//*[@id='"  + sourceRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String sourceNodeName = getLocalNameFromXmlNodeName(sourceNode.getNodeName());
                if(sourceNodeName.matches(".+Event$")){
                    sum += 1;
                }

            } catch (XPathExpressionException e) {
                System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
                return -1;
            } catch (Exception e){
                return -1;
            }
        }
        return sum;
    }


    private String getLocalNameFromXmlNodeName(String nodeName){
        String[] splitLocalNameFromNamespace = nodeName.split(":");
        if(splitLocalNameFromNamespace.length < 2 ){
            return splitLocalNameFromNamespace[0];
        }
        return splitLocalNameFromNamespace[1];
    }
    public int NSFG(String xmlString,int ErrValue) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        ArrayList<Node> activitiesInDiagram = getAllActivitiesOfCompleteModel(xmlString).orElseThrow();
        System.out.println("How many activities in diagram: " + activitiesInDiagram.size());
        int sum = 0;
        ArrayList<Node> sequenceFlowsInDiagram = getAllSequenceFlowsArr(xmlString).orElseThrow();
        for(Node n: sequenceFlowsInDiagram){

            try {
                String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
                Node sourceNode = (Node) xpathObj.compile("//*[@id='"  + sourceRef  + "']").evaluate(xmlDoc,XPathConstants.NODE);
                String sourceNodeName = getLocalNameFromXmlNodeName(sourceNode.getNodeName());
                if(sourceNodeName.matches(".+Gateway$")){
                    sum += 1;
                }
            } catch (XPathExpressionException e) {
                System.out.println("Something XPATH went wrong: " + e.getCause() + "--" + e.getMessage());
                return -1;
            } catch (Exception e){
                System.out.println("Something went wrong " + e.getCause() + " " + e.getMessage());
                return -1;
            }
        }
        return sum;
    }
    public int TNG(String xmlString,int ErrValue) throws IOException, SAXException {
        return getGatewaysInDiagram(xmlString).orElseThrow().size();
    }
    public int TS(String xmlString,int ErrValue) throws IOException, SAXException {
        //pairnoume ta OR,AND split nodes kai kathe fora pairnoume ta branches -1
        ArrayList<Node> gateways = getGatewaysInDiagram(xmlString).orElseThrow();
        List<Node> onlyOR_ANDGateways = gateways.stream()
                .filter(node->
                        getLocalNameFromXmlNodeName(node.getNodeName()).equals("parallelGateway") ||
                                getLocalNameFromXmlNodeName(node.getNodeName()).equals("inclusiveGateway")).toList();
        //pote thewroume ena node split node h merge node
        int result = onlyOR_ANDGateways.stream().reduce(0,(acc,node)->acc + getAmountOfOutgoingFlowsFromNode(node) - 1 ,Integer::sum);
        return result;
    }


    private Document ParseXmlStringToDocument(String xmlString) throws SAXException {


//            if(xmlString.isEmpty())throw new XmlFileException.EmptyFileException();
            InputSource inputSource = new InputSource(new StringReader(xmlString));
            try{
                return domBuilder.parse(inputSource);
            } catch (IOException e){
                System.out.println("HELLO");
                System.out.println(e + e.getMessage() + e.getCause());
            }
        return null;
    }

    private Optional<ArrayList<Node>> getEventsInDiagram(String xmlString) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
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

    private Optional<ArrayList<Node>> getGatewaysInDiagram(String xmlString) throws IOException, SAXException {
        Document xmlDoc = ParseXmlStringToDocument(xmlString);
        String expression = "/definitions//*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> gatewayNodes = new ArrayList<>();
            for(int i = 0; i< nodeList.getLength(); i++){
                Node current = nodeList.item(i);
                if(current.getNodeType() == Node.ELEMENT_NODE){
                    String localName = getLocalNameFromXmlNodeName(current.getNodeName());
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
                String localName = getLocalNameFromXmlNodeName(current.getNodeName());
                if(predicate.test(localName))nodesThatMatchPredicate.add(current);
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
    private int getAmountOfIncomingFlowsFromNode(Node node){
        String expression = "./*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(node,XPathConstants.NODESET);
            ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,(elemName)->elemName.equals("incoming"));
            return nodes.size();
        } catch (XPathExpressionException e) {
            return -1;
        }
    }


}
