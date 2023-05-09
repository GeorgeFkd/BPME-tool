package com.example.bpme;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
@Service
public class BpmeService {

    public ArrayList<HashMap<String, Number>> ParseXmlFiles(List<Resource> fileList) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        //get file contents
        ArrayList<String> fileContents = new ArrayList<>();

        for(Resource r: fileList) {
            try {
                fileContents.add(r.getContentAsString(Charset.defaultCharset()));
            } catch (IOException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
        MetricUtils m = new MetricUtils();
        m.NSFG(fileContents.get(0),-10);
        ArrayList<HashMap<String,Number>> hashMaps = new ArrayList<>();
        for(String fileStr:fileContents){
            HashMap<String,Number> metricResultsForFile = new HashMap<>();
            metricResultsForFile.put("NSFG",m.NSFG(fileStr,-1));//✔️
            metricResultsForFile.put("NOA",m.NOA(fileStr,-1));//✔️
            metricResultsForFile.put("NOAJS",m.NOAJS(fileStr,-1));//✔️
            metricResultsForFile.put("DENSITY",m.DENSITY(fileStr,  -1.0));//✔️
            metricResultsForFile.put("CNC",m.CNC(fileStr,  -1.0));//✔️
            metricResultsForFile.put("CFC",m.CFC(fileStr,  -1));//✔️
            metricResultsForFile.put("GH",m.GH(fileStr,  -1.0));//✔️
            metricResultsForFile.put("MGD",m.MGD(fileStr,  -1.0));//✔️
            metricResultsForFile.put("GM",m.GM(fileStr,  -1.0));//✔️
            metricResultsForFile.put("NMF",m.NMF(fileStr,-1));//✔️ !![with a catch for pools-> probably should be deactivated]
            metricResultsForFile.put("NSFA",m.NSFA(fileStr,-1));//✔️
            metricResultsForFile.put("NSFE",m.NSFE(fileStr,-1));//✔️
            metricResultsForFile.put("TNG",m.TNG(fileStr,-1));//✔️
            metricResultsForFile.put("CLA",m.CLA(fileStr,-1));//✔️
            metricResultsForFile.put("AGD",m.AGD(fileStr,-1));//✔️
            metricResultsForFile.put("SFs",m.sequenceFlowsInDiagram(fileStr,-1));//✔️
            hashMaps.add(metricResultsForFile);
        }


//        metricResults.put("TNG",allGatewaysNodeList.getLength());

        return hashMaps;


    }
}
