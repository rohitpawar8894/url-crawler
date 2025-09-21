package com.example.crawler.service;

import com.example.crawler.dto.ExtractRequest;
import com.example.crawler.dto.ExtractResponse;

public interface IExtractService {
    ExtractResponse processExtraction(ExtractRequest req, String userAgent) throws Exception;
}