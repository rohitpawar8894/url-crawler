package com.example.crawler;

import com.example.crawler.service.impl.ExtractorService;
import com.example.crawler.model.FetchResult;
import com.example.crawler.model.PageData;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtractorServiceTest {
    @Test
    void extract_basic() {
        String html = "<html lang=\"en\"><head><title>Page Title</title><meta name=\"description\" content=\"Desc\"></head><body><main><h1>Header</h1><p>Some meaningful paragraph with more than forty characters for extraction.</p></main></body></html>";
        FetchResult f = new FetchResult("http://example.com","http://example.com",200, html, Collections.emptyMap(), Instant.now());
        ExtractorService svc = new ExtractorService();
        PageData p = svc.extract(f);
        assertEquals("Page Title", p.getTitle());
        assertTrue(p.getText().contains("meaningful paragraph"));
    }
}
