package com.example.crawler.service.impl;

import com.example.crawler.model.FetchResult;
import com.example.crawler.model.ImageRef;
import com.example.crawler.model.LinkRef;
import com.example.crawler.model.PageData;
import com.example.crawler.utils.ContentExtractor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExtractorService {
    private final ContentExtractor contentExtractor = new ContentExtractor();

    @Value("${app.maxImages}") private int maxImages;
    @Value("${app.maxLinks}") private int maxLinks;

    public PageData extract(FetchResult fetch) {
        Document doc = contentExtractor.parse(fetch.getBody(), fetch.getFinalUrl());
        contentExtractor.stripNoise(doc);
        Element main = contentExtractor.findMain(doc);

        PageData p = new PageData();
        p.setTitle(safe(doc.title()));
        p.setDescription(meta(doc, "meta[name=description]", "content"));
        p.setCanonical(attr(doc, "link[rel=canonical]", "href"));
        p.setLang(lang(doc));
        p.setOg(og(doc));
        p.setPublishedTime(published(doc));
        p.setText(contentExtractor.extractText(main));
        p.setImages(contentExtractor.extractImages(main, maxImages).stream()
                .map(e -> new ImageRef(e.getKey(), e.getValue())).collect(Collectors.toList()));
        p.setLinks(contentExtractor.extractLinks(main, maxLinks).stream()
                .map(e -> new LinkRef(e.getKey(), e.getValue())).collect(Collectors.toList()));
        return p;
    }

    private String safe(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private String meta(Document d, String selector, String attr) {
        Element e = d.selectFirst(selector);
        return e != null ? safe(e.attr(attr)) : null;
    }

    private String attr(Document d, String selector, String attr) {
        Element e = d.selectFirst(selector);
        return e != null ? safe(e.absUrl(attr).isBlank() ? e.attr(attr) : e.absUrl(attr)) : null;
    }

    private String lang(Document d) {
        String l = d.select("html").attr("lang");
        if (l == null || l.isBlank()) {
            Element e = d.selectFirst("meta[http-equiv=content-language]");
            if (e != null) l = e.attr("content");
        }
        if ((l == null || l.isBlank())) {
            Element e2 = d.selectFirst("meta[property=og:locale]");
            if (e2 != null) l = e2.attr("content");
        }
        return safe(l);
    }

    private Map<String, String> og(Document d) {
        Map<String, String> map = new LinkedHashMap<>();
        d.select("meta[property^=og:], meta[name^=twitter:]").forEach(m -> {
            String k = m.hasAttr("property") ? m.attr("property") : m.attr("name");
            String v = m.attr("content");
            if (!v.isBlank()) map.put(k, v);
        });
        return map;
    }

    private String published(Document d) {
        Element e = d.selectFirst("meta[property=article:published_time], time[datetime]");
        if (e == null) return null;
        return e.hasAttr("content") ? e.attr("content") : e.attr("datetime");
    }
}
