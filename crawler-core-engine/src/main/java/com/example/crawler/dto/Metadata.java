package com.example.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

    private String title;
    private String description;
    private String canonical;
    private String lang;
    private Map<String, String> og;
    private String publishedTime;

}
