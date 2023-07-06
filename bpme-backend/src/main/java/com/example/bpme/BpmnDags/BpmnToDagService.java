package com.example.bpme.BpmnDags;

import com.example.bpme.metrics.MetricUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
@Component("BpmnToDagService")
public class BpmnToDagService implements BpmnToDagAdapter {

    private final MetricUtils metricUtils = new MetricUtils();

    public BpmnToDagService() throws ParserConfigurationException {
    }

    @Override
    public List<Resource> convertFilesToDags(List<Resource> bpmnFiles) {
        return bpmnFiles.stream().map(this::transformBpmnToGraphML).toList();
    }




    public Resource transformBpmnToGraphML(Resource bpmnFile){
        //CORE logic
        try (Graph graph = TinkerGraph.open()) {
            String contentsOfFile = bpmnFile.getContentAsString(Charset.defaultCharset());
            ArrayList<Node> taskNodes = metricUtils.getAllActivitiesOfCompleteModel(contentsOfFile).orElseThrow();
            System.out.println(taskNodes.get(0).getNodeType());
            System.out.println("These are the contents of the file: " + contentsOfFile);
            System.out.println("Converting diagram to graphml");
        } catch (Exception e) {
            System.out.println("======================");
            System.out.println(e.getCause()+" " + e.getLocalizedMessage());
        }

        return null;
    }
}
