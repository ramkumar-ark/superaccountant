package com.superaccountant.ETL;

import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JAXBException.class)
    public ResponseEntity<UploadTallyXmlResponse> handleJAXBException(JAXBException ex) {
        Throwable rootCause = ex.getLinkedException() != null ? ex.getLinkedException() : ex;
        log.error("XML parsing error. Root cause: {}", rootCause.getMessage(), ex);
        String errorMessage = "Error parsing XML file: " + rootCause.getMessage();
        return new ResponseEntity<>(new UploadTallyXmlResponse(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<UploadTallyXmlResponse> handleIOException(IOException ex) {
        log.error("File I/O error", ex);
        String errorMessage = "Error reading file. Please check server logs for details.";
        return new ResponseEntity<>(new UploadTallyXmlResponse(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<UploadTallyXmlResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(new UploadTallyXmlResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}