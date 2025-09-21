package com.example.crawler;

import com.example.crawler.service.impl.TopicsService;
import com.example.crawler.model.PageData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopicsServiceTest {
    @Test
    void topics_basic() {
        TopicsService svc = new TopicsService();
        PageData p = new PageData();
        p.setTitle("Edward Snowden profile - surveillance and NSA");
        p.setText("Edward Snowden worked at NSA. This article profiles Snowden and government surveillance programs.");
        List<String> t = svc.keyphrases(p);
        assertTrue(t.stream().anyMatch(s -> s.contains("snowden")));
    }
}
