package com.example.bpme.BpmnDags;

import org.springframework.core.io.Resource;

import java.util.List;

public interface BpmnToDagAdapter {

    List<Resource> convertFilesToDags(List<Resource> bpmnFiles);
}
