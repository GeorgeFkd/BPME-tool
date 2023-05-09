package com.example.bpme;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1/bpme")
public class BpmeController {
    private final BpmeService bpmeService;
    @Autowired
    public BpmeController(BpmeService bpmeService){
        this.bpmeService = bpmeService;
    }

    @PostMapping(path ="/files")
    public ResponseEntity<ArrayList<HashMap<String,Number>>> submitFiles(@RequestParam("files") MultipartFile[] filesArray){
        List<Resource> resourceList = Arrays.stream(filesArray).map((f)-> f.getResource()).collect(Collectors.toList());
//        System.out.println(resourceList);
        System.out.println(resourceList.get(0).getFilename());
        try {
            ArrayList<HashMap<String,Number>> result = this.bpmeService.ParseXmlFiles(resourceList);
            return new ResponseEntity<>(result,HttpStatus.OK);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return new ResponseEntity<>(filesArray.toString(), HttpStatus.OK);
    }
}
