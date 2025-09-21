package com.example.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchResult {

    private String inputUrl;
    private String finalUrl;
    private int statusCode;
    private String body;
    private Map<String, String> headers;
    private Instant fetchedAt;

}
