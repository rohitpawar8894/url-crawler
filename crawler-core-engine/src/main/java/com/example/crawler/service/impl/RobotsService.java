package com.example.crawler.service.impl;

import com.example.crawler.integration.Fetcher;
import com.example.crawler.model.FetchResult;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class RobotsService {
    private final Fetcher fetcher;
    private final Cache<String, BaseRobotRules> cache;

    public RobotsService(Fetcher fetcher, @Value("${app.robotsTtlMinutes}") long ttlMin) {
        this.fetcher = fetcher;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(ttlMin))
                .maximumSize(10_000)
                .build();
    }

    public boolean isAllowed(String userAgent, URI uri) {
        try {
            String key = uri.getScheme() + "://" + uri.getHost();
            BaseRobotRules rules = cache.get(key, k -> fetchRobots(userAgent, uri));
            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) path = "/";
            return rules == null || rules.isAllowed(path);
        } catch (Exception e) {
            // fail-open; switch to false to be stricter
            return true;
        }
    }

    private BaseRobotRules fetchRobots(String userAgent, URI uri) {
        try {
            String robotsUrl = uri.getScheme() + "://" + uri.getHost() + "/robots.txt";
            FetchResult res = fetcher.fetch(robotsUrl);
            byte[] bytes = res.getBody().getBytes(StandardCharsets.UTF_8);
            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            return parser.parseContent(robotsUrl, bytes, "text/plain", userAgent);
        } catch (Exception e) {
            return null;
        }
    }
}
