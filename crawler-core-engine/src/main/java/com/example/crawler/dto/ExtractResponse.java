package com.example.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ExtractResponse {

    private String inputUrl;
    private String urlFinal;
    private Instant fetchedAt;
    private int statusCode;
    private Metadata metadata;
    private Content content;
    private Nlp nlp;

}
