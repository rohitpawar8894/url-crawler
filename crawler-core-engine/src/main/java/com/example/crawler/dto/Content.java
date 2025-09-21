package com.example.crawler.dto;

import com.example.crawler.model.ImageRef;
import com.example.crawler.model.LinkRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    private String text;
    private List<ImageRef> images;
    private List<LinkRef> links;

}
