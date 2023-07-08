package com.example.bpme.metrics;


import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
@Getter
public class XmlFileException extends Exception{

    private String filename;
    private String exceptionClass;
    private String exceptionMessage;
    public XmlFileException(String message,String filename,String exceptionClass) {
        super(message);
        this.exceptionMessage = message;
        this.filename = filename;
        this.exceptionClass = exceptionClass;

    }



}
