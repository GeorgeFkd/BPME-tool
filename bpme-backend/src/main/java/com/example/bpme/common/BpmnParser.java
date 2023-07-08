package com.example.bpme.common;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import java.util.function.Predicate;
@Getter
@Slf4j
public class BpmnParser {


    private static final String BPMN_INCOMING = "incoming";
    private static final String BPMN_OUTGOING = "outgoing";
    private static final String BPMN_GATEWAY_POSTFIX = "Gateway";
    private static final String BPMN_EVENTS_POSTFIX = "Event";
    private static final String BPMN_START_EVENT_PREFIX = "start";
    private static final String BPMN_SEQUENCE_FLOW = "sequenceFlow";
    private final String xmlString;
    private final Document xmlDoc;
    private final DocumentBuilder domBuilder;
    private final XPath xpathObj;
    private final String filename;
    public Optional<ArrayList<Node>> getAllSequenceFlowsArr() {
        String sequenceFlowsExpression = "/definitions//*/*";
        try {
            NodeList nodes = (NodeList) xpathObj.compile(sequenceFlowsExpression).evaluate(xmlDoc, XPathConstants.NODESET);
            return Optional.of(getNodesWithLocalNameThatPassesPredicate(nodes,elemName->elemName.equals(BPMN_SEQUENCE_FLOW)));
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return Optional.empty();

        }
    }

    public BpmnParser(String xmlString,String filename) throws ParserConfigurationException, SAXException {
        this.xmlString = xmlString;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        this.domBuilder = builderFactory.newDocumentBuilder();
        this.xpathObj = XPathFactory.newInstance().newXPath();
        this.xmlDoc = parseXmlStringToDocument(xmlString);
        this.filename = filename;
    }
    public String getLocalNameFromXmlNodeName(String nodeName){
        String[] splitLocalNameFromNamespace = nodeName.split(":");
        if(splitLocalNameFromNamespace.length < 2 ){
            return splitLocalNameFromNamespace[0];
        }
        return splitLocalNameFromNamespace[1];
    }
    public ArrayList<Node> getNodesWithLocalNameThatPassesPredicate(NodeList nodeList, Predicate<String> predicate){
        ArrayList<Node> nodesThatMatchPredicate = new ArrayList<>();

        for(int i = 0; i < nodeList.getLength(); i++){
            Node current = nodeList.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE){
                String localName = getLocalNameFromXmlNodeName(current.getNodeName());
                if(predicate.test(localName))nodesThatMatchPredicate.add(current);
            }
        }
        return nodesThatMatchPredicate;
    }

    public Node getPreviousNodeOfSequenceFlow(Node sequenceFlow){
        String idOfPreviousNode = sequenceFlow.getAttributes().getNamedItem("sourceRef").getNodeValue();
        String expression = String.format("//*[@id=%s]",idOfPreviousNode);
        log.debug("Looking for node with id: " + idOfPreviousNode);
        try {
            Node prevNode = (Node) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODE);
            log.debug("Previous node tag: " + prevNode.getNodeName());
            return prevNode;
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return null;
        }
    }


    public Node getNodeWithValueOfAttribute(String attribute,String value){
        String expression = String.format("//*[@%s=%s]",attribute,value);
        log.debug("Looking for node with attribute-value: " +attribute + "-" + value);
        try {
            Node prevNode = (Node) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODE);
            log.debug("Previous node tag: " + prevNode.getNodeName());
            return prevNode;
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return null;
        }
    }

    public Node getNextNodeOfSequenceFlow(Node sequenceFlow){
        String idOfNextNode = sequenceFlow.getAttributes().getNamedItem("targetRef").getNodeValue();
        String expression = String.format("//*[@id=%s]",idOfNextNode);
        log.debug("Looking for node with id: " + idOfNextNode);
        try {
            Node prevNode = (Node) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODE);
            log.debug("Previous node tag: " + prevNode.getNodeName());
            return prevNode;
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return null;
        }
    }


    private void logIoError(IOException e){

        log.error("=========Error==========\n"
                +e.getClass().getName() + e.getCause() + e.getLocalizedMessage());
    }
    private Document parseXmlStringToDocument(String xmlString) throws SAXException {
        InputSource inputSource = new InputSource(new StringReader(xmlString));
        try{
            return domBuilder.parse(inputSource);
        } catch (IOException e){
            logIoError(e);
        }
        return null;
    }

    public Optional<ArrayList<Node>> getGatewaysInDiagram() {
        String expression = "/definitions//*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> gatewayNodes = new ArrayList<>();
            for(int i = 0; i< nodeList.getLength(); i++){
                Node current = nodeList.item(i);
                if(current.getNodeType() == Node.ELEMENT_NODE){
                    String localName = getLocalNameFromXmlNodeName(current.getNodeName());
                    if(localName.endsWith(BPMN_GATEWAY_POSTFIX)) gatewayNodes.add(current);
                }
            }
            return Optional.of(gatewayNodes);
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return Optional.empty();
        }
    }

    public int getAmountOfOutgoingFlowsFromNode(Node node){
        String expression = "./*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(node,XPathConstants.NODESET);
            ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,elemName->elemName.equals(BPMN_OUTGOING));
            return nodes.size();
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return -1;
        }
    }

    @SneakyThrows
    public NodeList getAllXmlElementsOfFile(){
        String expression = "./*";
        return (NodeList) xpathObj.compile(expression).evaluate(xmlString,XPathConstants.NODESET);
    }

    public int getAmountOfIncomingFlowsFromNode(Node node){
        String expression = "./*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(node,XPathConstants.NODESET);
            ArrayList<Node> nodes = getNodesWithLocalNameThatPassesPredicate(nodeList,elemName->elemName.equals(BPMN_INCOMING));
            return nodes.size();
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return 0;
        }
    }
    public Optional<NodeList> getAllSequenceFlows() {
        String sequenceFlowsExpression = "/definitions//*/sequenceFlow";

        NodeList sequenceFlowsNodeList = null;
        try {
            sequenceFlowsNodeList = (NodeList) this.xpathObj.compile(sequenceFlowsExpression).evaluate(xmlDoc, XPathConstants.NODESET);
            return Optional.of(sequenceFlowsNodeList);
        } catch (XPathExpressionException e) {
            System.out.println(e);
            System.out.println("all sfs list " + e.getCause() + "--" + e.getMessage());
            return Optional.empty();
        }
    }

    private void logXPathError(XPathExpressionException e){
        log.error("Error in XPath: " + e.getCause() + e.getLocalizedMessage());
    }


    public List<Node> getParticipantsNodes(){
        return Collections.emptyList();
    }

    public List<String> getParticipantNames(){
        return Collections.emptyList();
    }


    public Optional<ArrayList<Node>> getEventsInDiagram(){
        String expression = ".//*";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            Predicate<String> isEventElement = elemName -> elemName.matches(".+" + BPMN_EVENTS_POSTFIX + "$");
            ArrayList<Node> nodesWithEventLikeName = getNodesWithLocalNameThatPassesPredicate(nodeList,isEventElement);
            return Optional.of(nodesWithEventLikeName);
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return Optional.empty();
        }
    }
    public Optional<ArrayList<Node>> getAllActivitiesOfCompleteModel() {
        try {
            NodeList allNodes = (NodeList) xpathObj.compile(".//*").evaluate(xmlDoc,XPathConstants.NODESET);
            ArrayList<Node> allActivities = getNodesWithLocalNameThatPassesPredicate(allNodes,(localName)->localName.matches(".*(t|T)ask"));
            return Optional.of(allActivities);
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return Optional.empty();
        }
    }

    public ArrayList<Node> getNextNodes(Node n){
        ArrayList<Node> nextNodes = new ArrayList<>();
        String expression = "./outgoing";
        try {
            NodeList nodeList = (NodeList) xpathObj.compile(expression).evaluate(n,XPathConstants.NODESET);
            for(int i = 0; i< nodeList.getLength(); i++){
                Node current = nodeList.item(i);
                if(current.getNodeType() == Node.ELEMENT_NODE && current.getNodeName().endsWith(BPMN_OUTGOING)){
                    nextNodes.add(current);
                }
            }
            return nextNodes;
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return nextNodes;
        }
    }

    public Node findStartNode(String participant){
        if(participant == null) return findStartNodeOfPrivateProcess();
        else return findStartNodeOfParticipant(participant);
    }

    public Node findStartNodeOfParticipant(String participant){
        String expressionToFindParticipant = "/definitions/collaboration/participant[@name='" + participant + "']";
        try {
            Node participantNode = (Node) xpathObj.compile(expressionToFindParticipant).evaluate(xmlDoc,XPathConstants.NODE);
            String participantProcessId = participantNode.getAttributes().getNamedItem("processRef").getNodeValue();
            String expressionToFindProcessOfParticipant = "/definitions/process[@id='" + participantProcessId + "']" + "/startEvent";
            Node startEventOfProcessOfParticipant = (Node) xpathObj.compile(expressionToFindProcessOfParticipant).evaluate(xmlDoc,XPathConstants.NODE);
            return startEventOfProcessOfParticipant;

        } catch (XPathExpressionException e) {
            logXPathError(e);
            return null;
        }
    }
    public Node findStartNodeOfPrivateProcess(){
        String expressionToFindStartNode = "/definitions/process/startEvent";
        try {
            Node startEvent = (Node) xpathObj.compile(expressionToFindStartNode).evaluate(xmlDoc,XPathConstants.NODE);
            return startEvent;
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return null;
        }
    }

    public NodeList getParticipantsOfCollaboration(){
        String expression = "/definitions/collaboration/participant";
        try {
            NodeList participants = (NodeList) xpathObj.compile(expression).evaluate(xmlDoc,XPathConstants.NODESET);
            return participants;
        } catch (XPathExpressionException e) {
            logXPathError(e);
            return null;
        }
    }

    public Node findNameOfSoleWhiteBoxPool(){
        NodeList participants = getParticipantsOfCollaboration();
        if(participants.getLength() == 1){
            Node participant = participants.item(0);
            return participant.getAttributes().getNamedItem("name");
        }else if(participants.getLength() > 1) {
            log.info("More than one participant found in collaboration. This is not supported yet.");
            for(int i = 0; i < participants.getLength(); i++){
                Node participant = participants.item(i);
                if(participant.getAttributes().getNamedItem("processRef")!=null){
                    return participant.getAttributes().getNamedItem("name");
                }
            }
            return null;
        }
        else return null;
    }



}
