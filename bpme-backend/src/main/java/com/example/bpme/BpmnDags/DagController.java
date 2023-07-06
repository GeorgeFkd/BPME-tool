package com.example.bpme.BpmnDags;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping(path="api/v1/bpme")
public class DagController {

    private final BpmnToDagAdapter bpmnToDagConverter;
    @Autowired
    public DagController(@Qualifier("BpmnToDagService") BpmnToDagAdapter bpmnToDagConverter){
        this.bpmnToDagConverter = bpmnToDagConverter;
    }

    @PostMapping(path="/dags")
    public ResponseEntity<String> convertBpmnFilesToDags(@RequestParam("files") MultipartFile[] files){
        List<Resource> resourceList = Arrays.stream(files).map(MultipartFile::getResource).collect(Collectors.toList());
        ArrayList<Resource> graphMLFiles = new ArrayList<>(bpmnToDagConverter.convertFilesToDags(resourceList));
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
