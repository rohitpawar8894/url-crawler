package com.example.controller;

import com.example.crawler.dto.ExtractRequest;
import com.example.crawler.dto.ExtractResponse;
import com.example.crawler.service.IExtractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExtractController {

    private IExtractService extractService;
    private final String userAgent;

    public ExtractController(IExtractService extractService, @Value("${app.userAgent}") String userAgent) {
        this.extractService = extractService;
        this.userAgent = userAgent;
    }

    @PostMapping(path = "/extract",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ExtractResponse extract(@RequestBody ExtractRequest req) throws Exception {
        return extractService.processExtraction(req, userAgent);
    }
}