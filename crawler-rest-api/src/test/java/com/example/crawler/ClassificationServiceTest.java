package com.example.crawler;

import com.example.crawler.service.impl.ClassificationService;

import com.example.crawler.model.PageData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassificationServiceTest {
    @Test
    void classify_product() {
        ClassificationService s = new ClassificationService();
        PageData p = new PageData();
        p.setText("Buy now! Add to cart. Free shipping.");
        assertEquals("product", s.classify(p));
    }

    @Test
    void classify_news() {
        ClassificationService s = new ClassificationService();
        PageData p = new PageData();
        p.setPublishedTime("2013-06-10");
        assertEquals("news_article", s.classify(p));
    }
}
