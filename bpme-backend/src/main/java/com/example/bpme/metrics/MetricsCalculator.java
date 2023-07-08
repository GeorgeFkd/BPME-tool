package com.example.bpme.metrics;

import com.example.bpme.common.BpmnParser;
import com.example.bpme.metrics.records.MetricResults;
import org.apache.commons.lang3.NotImplementedException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MetricsCalculator {
    private BpmnParser bpmnParser;
//    private List<String> metricsSupported = new ArrayList<>();
    private ArrayList<BPMetric> metricsSupported = new ArrayList<>();
    private void initialiseMetricObjects() {
        BPMetric CFC = new BPMetric("CFC", "", 0, -1) {
            @Override
            public Number calculateMetric() {

                String xorGatewayTagName = "exclusiveGateway";
                String orGatewayTagName = "inclusiveGateway";
                String andGatewayTagName = "parallelGateway";
                String eventBasedGatewayTagName = "eventBasedGateway";
                ArrayList<Node> gateways = bpmnParser.getGatewaysInDiagram().orElseThrow();
                List<Node> xorGateways = gateways.stream().filter((node -> bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals(xorGatewayTagName))).collect(Collectors.toList());
                List<Node> andGateways = gateways.stream().filter((node -> bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals(andGatewayTagName))).collect(Collectors.toList());
                List<Node> orGateways = gateways.stream().filter((node -> bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals(orGatewayTagName))).collect(Collectors.toList());
                List<Node> eventGateways = gateways.stream().filter((node -> bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals(eventBasedGatewayTagName))).collect(Collectors.toList());


                int cfcXor = xorGateways.stream().reduce(0, (total, gatewayNode) -> {
                    //see if gateway is a split node
                    NodeList childrenOfGateway = null;
                    try {
                        childrenOfGateway = (NodeList) bpmnParser.getXpathObj().compile(".//*").evaluate(gatewayNode, XPathConstants.NODESET);
                    } catch (XPathExpressionException e) {
                        return (int) -1;
                    }
                    int incoming = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
                    int outgoing = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
                    if (incoming < outgoing) {
                        //is split node
                        return total + outgoing;
                    } else {
                        return total;
                    }
                }, Integer::sum);
                int cfcEventBased = eventGateways.stream().reduce(0, (total, gatewayNode) -> {
                    //see if gateway is a split node
                    NodeList childrenOfGateway = null;
                    try {
                        childrenOfGateway = (NodeList) bpmnParser.getXpathObj().compile(".//*").evaluate(gatewayNode, XPathConstants.NODESET);
                    } catch (XPathExpressionException e) {
                        return (int) -1;
                    }
                    int incoming = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
                    int outgoing = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
                    if (incoming < outgoing) {
                        //is split node
                        return total + outgoing;
                    } else {
                        return total;
                    }
                }, Integer::sum);

                int cfcOR = orGateways.stream().reduce(0, (total, gatewayNode) -> {
                    //see if gateway is a split node
                    NodeList childrenOfGateway = null;
                    try {
                        childrenOfGateway = (NodeList) bpmnParser.getXpathObj().compile(".//*").evaluate(gatewayNode, XPathConstants.NODESET);
                    } catch (XPathExpressionException e) {
                        return (int) -1;
                    }
                    int incoming = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
                    int outgoing = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
                    if (incoming < outgoing) {
                        //is split node
                        return total + (int) Math.pow(2, outgoing) - 1;
                    } else {
                        return total;
                    }
                }, Integer::sum);

                //and gateway doesn't have separate possible scenarios
                int cfcAnd = andGateways.size();


                return cfcAnd + cfcOR + cfcEventBased + cfcXor;
            }
        };
        BPMetric AGD = new BPMetric("AGD", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                ArrayList<Node> gateways = bpmnParser.getGatewaysInDiagram().orElseThrow();

                int sumOfFlowsFromGateways = gateways.stream().map((gateway -> {
                    String expression = "./*";
                    try {
                        NodeList nodeList = (NodeList) bpmnParser.getXpathObj().compile(expression).evaluate(gateway, XPathConstants.NODESET);
                        ArrayList<Node> nodes = bpmnParser.getNodesWithLocalNameThatPassesPredicate(nodeList, (elemName) -> elemName.equals("incoming") || elemName.equals("outgoing"));

                        return nodes.size();
                    } catch (XPathExpressionException e) {
                        System.out.println("PROBLEM WITH XPATH " + e.getCause() + " --" + e.getMessage());
                        return (int) -1;
                    }

                })).reduce(0, (a, b) -> a + b);
                if (gateways.size() == 0) return -1;

                return sumOfFlowsFromGateways / gateways.size();
            }
        };
        BPMetric NSFG = new BPMetric("NSFG", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                ArrayList<Node> activitiesInDiagram = bpmnParser.getAllActivitiesOfCompleteModel().orElseThrow();
                System.out.println("How many activities in diagram: " + activitiesInDiagram.size());
                int sum = 0;
                ArrayList<Node> sequenceFlowsInDiagram = bpmnParser.getAllSequenceFlowsArr().orElseThrow();
                for (Node n : sequenceFlowsInDiagram) {

                    try {
                        String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
                        Node sourceNode = (Node) bpmnParser.getXpathObj().compile("//*[@id='" + sourceRef + "']").evaluate(this.bpmnParser.getXmlDoc(), XPathConstants.NODE);
                        String sourceNodeName = bpmnParser.getLocalNameFromXmlNodeName(sourceNode.getNodeName());
                        if (sourceNodeName.matches(".+Gateway$")) {
                            sum += 1;
                        }
                    } catch (XPathExpressionException e) {
                        System.out.println("Something XPATH went wrong: " + e.getCause() + "--" + e.getMessage());
                        return -1;
                    } catch (Exception e) {
                        System.out.println("Something went wrong " + e.getCause() + " " + e.getMessage());
                        return -1;
                    }
                }
                return sum;
            }
        };
        BPMetric NOA = new BPMetric("NOA", "", 0, -1) {
            @Override
            public Number calculateMetric(){
                String expression = ".//*";
                try {
                    NodeList totalNodeList = (NodeList) bpmnParser.getXpathObj().compile(expression).evaluate(bpmnParser.getXmlDoc(), XPathConstants.NODESET);
                    ArrayList<Node> activitiesNodeList = bpmnParser.getNodesWithLocalNameThatPassesPredicate(totalNodeList, (elemName) -> elemName.matches(".*(t|T)ask"));
                    return activitiesNodeList.size();
                } catch (XPathExpressionException e) {
                    System.out.println(e);
                    return -1;
                }
            }
        };

        BPMetric TNG = new BPMetric("TNG", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                return bpmnParser.getGatewaysInDiagram().orElseThrow().size();
            }
        };
        BPMetric NOAJS = new BPMetric("NOAJS", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                return (Number) ((int) TNG.calculateMetric() + (int) NOA.calculateMetric());
            }
        };

        BPMetric NSFA = new BPMetric("NSFA", "", 0, -1) {
            @Override
            public Number calculateMetric() {

                int sum = 0;
                ArrayList<Node> sequenceFlowsInDiagram = bpmnParser.getAllSequenceFlowsArr().orElseThrow();
                System.out.println("Found " + sequenceFlowsInDiagram.size() + " Sequence Flows in Diagram");
                for (Node n : sequenceFlowsInDiagram) {
                    String sourceRef;
                    String targetRef;
                    try {
                        sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
                        targetRef = n.getAttributes().getNamedItem("targetRef").getNodeValue();
                        System.out.println(sourceRef + "\t" + targetRef);
                    } catch (NullPointerException e) {
                        return -1;
                    }
                    try {
                        Node sourceNode = (Node) bpmnParser.getXpathObj().compile("//*[@id='" + sourceRef + "']").evaluate(bpmnParser.getXmlDoc(), XPathConstants.NODE);
                        String sourceNodeName = bpmnParser.getLocalNameFromXmlNodeName(sourceNode.getNodeName());
                        Node targetNode = (Node) bpmnParser.getXpathObj().compile("//*[@id='" + targetRef + "']").evaluate(bpmnParser.getXmlDoc(), XPathConstants.NODE);
                        String targetNodeName = bpmnParser.getLocalNameFromXmlNodeName(targetNode.getNodeName());
                        if (sourceNodeName.matches(".*(t|T)ask") && targetNodeName.matches(".*(t|T)ask")) {
                            sum += 1;
                        }

                    } catch (XPathExpressionException e) {
                        System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
                    }
                }
                return sum;
            }
        };
        BPMetric NSFE = new BPMetric("NSFA", "", 0, -1) {
            @Override
            public Number calculateMetric(){
                int sum = 0;
                ArrayList<Node> sequenceFlowsInDiagram = bpmnParser.getAllSequenceFlowsArr().orElse(new ArrayList<>());
                for (Node n : sequenceFlowsInDiagram) {

                    try {
                        String sourceRef = n.getAttributes().getNamedItem("sourceRef").getNodeValue();
                        Node sourceNode = (Node) bpmnParser.getXpathObj().compile("//*[@id='" + sourceRef + "']").evaluate(bpmnParser.getXmlDoc(), XPathConstants.NODE);
                        String sourceNodeName = bpmnParser.getLocalNameFromXmlNodeName(sourceNode.getNodeName());
                        if (sourceNodeName.matches(".+Event$")) {
                            sum += 1;
                        }

                    } catch (XPathExpressionException e) {
                        System.out.println("Something went wrong: " + e.getCause() + "--" + e.getMessage());
                        return -1;
                    } catch (Exception e) {
                        return -1;
                    }
                }
                return sum;
            }
        };

        BPMetric TS = new BPMetric("TS", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                ArrayList<Node> gateways = bpmnParser.getGatewaysInDiagram().orElse(new ArrayList<>());
                List<Node> onlyOR_ANDGateways = gateways.stream()
                        .filter(node ->
                                bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals("parallelGateway") ||
                                        bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals("inclusiveGateway")).toList();
                //pote thewroume ena node split node h merge node
                int result = onlyOR_ANDGateways.stream().reduce(0, (acc, node) -> acc + bpmnParser.getAmountOfOutgoingFlowsFromNode(node) - 1, Integer::sum);
                return result;
            }
        };

        BPMetric MGD = new BPMetric("MGD", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                //thelw ola ta gateways
                ArrayList<Node> gateways = bpmnParser.getGatewaysInDiagram().orElse(new ArrayList<>());
                //meta metraw posa flows exei to kathena
                System.out.println(gateways);
                ArrayList<Integer> gatewaysDegrees = new ArrayList<>();
                for (Node n : gateways) {
                    String expressionForChildren = "./*";
                    try {
                        NodeList children = (NodeList) bpmnParser.getXpathObj().compile(expressionForChildren).evaluate(n, XPathConstants.NODESET);
                        int flowsOfNode = bpmnParser.getNodesWithLocalNameThatPassesPredicate(children, (elemName) -> elemName.equals("incoming") || elemName.equals("outgoing")).size();
                        gatewaysDegrees.add(gateways.indexOf(n), flowsOfNode);
                    } catch (XPathExpressionException e) {
                        System.out.println("XPATH MGD " + e.getCause() + "--" + e.getMessage());
                        return -1;
                    }
                }
                //pairnw to megisto
                System.out.println(gatewaysDegrees);
                Optional<Integer> res = gatewaysDegrees.stream().max((num1, num2) -> num1 - num2);
                if (res.isPresent())
                    return res.get();

                return 0;
            }
        };

        BPMetric GM = new BPMetric("GM", "", 0, -1) {
            @Override
            public Number calculateMetric() {

                String xorGatewayTagName = "exclusiveGateway";
                String orGatewayTagName = "inclusiveGateway";
                String andGatewayTagName = "parallelGateway";
                String eventBasedGatewayTagName = "eventBasedGateway";
                String[] gatewayTypes = {xorGatewayTagName, orGatewayTagName, andGatewayTagName, eventBasedGatewayTagName};
                NodeList allNodes = null;
                int totalGM = 0;
                try {
                    allNodes = (NodeList) bpmnParser.getXpathObj().compile("//*").evaluate(bpmnParser.getXmlDoc(), XPathConstants.NODESET);


                    ArrayList<Node> gatewaysOfDiagram = bpmnParser.getGatewaysInDiagram().orElse(new ArrayList<>());
                    for (String gateType : gatewayTypes) {

                        List<Node> gatewaysOfType = gatewaysOfDiagram.stream().filter((node) -> bpmnParser.getLocalNameFromXmlNodeName(node.getNodeName()).equals(gateType)).collect(Collectors.toList());
                        System.out.println(gatewaysOfType.size());
                        int gmOfType = gatewaysOfType.stream().reduce(0, (total, node) -> {
                            NodeList childrenOfGateway = null;
                            try {
                                childrenOfGateway = (NodeList) bpmnParser.getXpathObj().compile(".//*").evaluate(node, XPathConstants.NODESET);
                            } catch (XPathExpressionException e) {
                                return (int) -1;
                            }
                            int incoming = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("incoming")).size();
                            int outgoing = bpmnParser.getNodesWithLocalNameThatPassesPredicate(childrenOfGateway, (elemName) -> elemName.equals("outgoing")).size();
                            if (incoming > outgoing) {
                                //merge node
                                return total - incoming;
                            } else if (incoming == outgoing) {
                                return total;
                            } else {
                                return total + outgoing;
                            }
                        }, Integer::sum);

                        totalGM += Math.abs(gmOfType);
                    }
                } catch (XPathExpressionException e) {
                    System.out.println("xpath gm: " + e.getCause() + "--" + e.getMessage());
                    return -1;
                }

                return totalGM;
            }
        };

        BPMetric GH = new BPMetric("GH", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                ArrayList<Node> gateways = bpmnParser.getGatewaysInDiagram().orElseThrow();
                int numberOfGatewaysInDiagram = gateways.size();
                if (numberOfGatewaysInDiagram == 0) return -1;
                HashMap<String, Integer> countGatewaysPerType = new HashMap<>();
                for (Node n : gateways) {
                    String typeOfGateway = bpmnParser.getLocalNameFromXmlNodeName(n.getNodeName());
                    int prevValue = countGatewaysPerType.getOrDefault(typeOfGateway, 0);
                    countGatewaysPerType.put(typeOfGateway, prevValue + 1);
                }
                double sum = 0;
                for (Integer val : countGatewaysPerType.values()) {
                    double percentageOfTotal = val / (double) numberOfGatewaysInDiagram;
                    sum += -1 * (Math.log(percentageOfTotal) / Math.log(3)) * percentageOfTotal;
                }
                return sum;
            }
        };


        BPMetric DENSITY = new BPMetric("DENSITY", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                Optional<NodeList> thenodes = bpmnParser.getAllSequenceFlows();

                int arcs = thenodes.isEmpty() ? 0 : thenodes.get().getLength();

                int events = bpmnParser.getEventsInDiagram().orElse(new ArrayList<>()).size();
                int nodes = (int) NOA.calculateMetric() + (int) TNG.calculateMetric() + events;
                if (nodes == 0) return -1;
                int divWith = nodes * (nodes - 1);
                return arcs / (double) divWith;
            }
        };
        BPMetric CNC = new BPMetric("CNC", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                Optional<NodeList> thenodes = bpmnParser.getAllSequenceFlows();

                int arcs = thenodes.isEmpty() ? 0 : thenodes.get().getLength();
                int events = bpmnParser.getEventsInDiagram().orElseThrow().size();
                int nodes = (int) NOA.calculateMetric() + (int)TNG.calculateMetric() + events;
                if(nodes == 0) return -1;

                return arcs/(double) nodes;
            }
        };
        BPMetric CLA = new BPMetric("CLA", "", 0, -1) {
            @Override
            public Number calculateMetric() {
                Number noa = NOA.calculateMetric();
                Number nsfa = NSFA.calculateMetric();
                if(noa.intValue() == 0) return -1;
                if(nsfa.intValue() == 0) return -1;
                return noa.doubleValue()/(double) nsfa.intValue();
            }
        };
        metricsSupported.addAll(Arrays.asList(NOA, TNG, NSFA, MGD, GM, GH, DENSITY, CNC, CLA,TS,NSFE,NSFG,CFC,AGD,NOAJS));
    }
    public MetricsCalculator(String xmlString,String filename) throws XmlFileException{
        try{
            bpmnParser = new BpmnParser(xmlString,filename);
        }catch (ParserConfigurationException | SAXException e){
            throw new XmlFileException(e.getMessage(),filename,e.getClass().getName());
        }

        this.initialiseMetricObjects();

    }

    public ArrayList<MetricResults> calculateMetricsForFile(List<String> metricsChosen) throws NotImplementedException{
        ArrayList<MetricResults> metricResults = new ArrayList<>();
        for (String metricName : metricsChosen) {
            MetricResults res = new MetricResults(metricName, metricsSupported.stream().filter((metric) -> metric.getName().equals(metricName)).findFirst().orElseThrow(()-> new NotImplementedException()).calculateMetric());
            metricResults.add(res);
        }
        return metricResults;
    }
}
