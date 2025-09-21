package com.example.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageData {

    private String title;
    private String description;
    private String canonical;
    private String lang;
    private Map<String, String> og;
    private String publishedTime;
    private String text;
    private List<ImageRef> images;
    private List<LinkRef> links;
    private String pageType;
    private List<String> topics;
}
