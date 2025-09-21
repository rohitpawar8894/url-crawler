package com.example.crawler.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.Map.Entry;

public class ContentExtractor {

    public Document parse(String html, String baseUrl) {
        return Jsoup.parse(html, baseUrl);
    }

    public void stripNoise(Document doc) {
        doc.select("script, style, noscript, svg, nav, header, footer, form, ads, iframe").remove();
        doc.select(".advert, .ads, .promo, .breadcrumbs, .newsletter, .subscribe").remove();
    }

    public Element findMain(Document doc) {
        List<String> selectors = Arrays.asList("article", "main", "#content", ".article", ".post", ".entry-content");
        for (String sel : selectors) {
            Element e = doc.selectFirst(sel);
            if (e != null && textLen(e) > 200) return e;
        }
        Element best = null; int bestScore = 0;
        for (Element e : doc.select("body *")) {
            if (e.tagName().matches("p|div|section")) {
                int score = textLen(e);
                if (score > bestScore) { bestScore = score; best = e; }
            }
        }
        return (best != null) ? best : doc.body();
    }

    public String extractText(Element main) {
        StringBuilder sb = new StringBuilder();
        for (Element p : main.select("p, h1, h2, h3, li")) {
            String t = p.text().trim();
            if (t.length() >= 40 || p.tagName().matches("h1|h2|h3")) {
                sb.append(t).append("\n\n");
            }
        }
        String s = sb.toString().trim();
        return s.isEmpty() ? main.text() : s;
    }

    public List<Entry<String,String>> extractImages(Element scope, int max) {
        List<Entry<String,String>> out = new ArrayList<>();
        for (Element img : scope.select("img[src]")) {
            String src = img.absUrl("src");
            if (src == null || src.isEmpty()) continue;
            String alt = img.attr("alt");
            out.add(new AbstractMap.SimpleEntry<>(src, alt));
            if (out.size() >= max) break;
        }
        return out;
    }

    public List<Entry<String,String>> extractLinks(Element scope, int max) {
        List<Entry<String,String>> out = new ArrayList<>();
        for (Element a : scope.select("a[href]")) {
            String href = a.absUrl("href");
            if (href == null || href.isEmpty()) continue;
            String rel = a.attr("rel");
            out.add(new AbstractMap.SimpleEntry<>(href, rel));
            if (out.size() >= max) break;
        }
        return out;
    }

    private int textLen(Element e) { return e.text().length(); }
}
