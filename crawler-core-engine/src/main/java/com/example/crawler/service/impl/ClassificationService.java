package com.example.crawler.service.impl;

import com.example.crawler.model.PageData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ClassificationService {

    private static final Pattern PRICE = Pattern.compile(
            "\\$\\s?\\d{1,3}(,\\d{3})*(\\.\\d{2})?|USD\\s?\\d+|\\d+\\s?USD",
            Pattern.CASE_INSENSITIVE
    );

    public String classify(PageData p) {
        Document d = Jsoup.parse("<html><body>" + (p.getText() == null ? "" : p.getText()) + "</body></html>");
        boolean schemaProduct = p.getOg() != null && ("product".equalsIgnoreCase(p.getOg().get("og:type")) || containsJsonLdType(d, "Product"));
        boolean commerceWords = containsAny(d, "add to cart", "buy now", "free shipping", "in stock") || PRICE.matcher(p.getText() == null ? "" : p.getText()).find();
        if (schemaProduct || commerceWords) return "product";

        boolean hasPublished = p.getPublishedTime() != null;
        boolean ogArticle = p.getOg() != null && "article".equalsIgnoreCase(p.getOg().get("og:type"));
        boolean hasByline = containsAny(d, "by ", "reported by", "author");
        if (hasPublished || ogArticle || hasByline) return "news_article";

        boolean urlBlog = p.getCanonical() != null && p.getCanonical().toLowerCase().contains("/blog/");
        boolean headings = containsAny(d, "comments", "posted in", "categories:");
        if (urlBlog || headings) return "blog_post";

        if (p.getLinks() != null && p.getLinks().size() > 30 && (p.getText() == null || p.getText().length() < 1000)) return "category";

        return "unknown";
    }

    private boolean containsJsonLdType(Document d, String type) {
        for (Element e : d.select("script[type=application/ld+json]")) {
            if (e.data().toLowerCase().contains("\"@type\"[ \\t\\r\\n\\f]*:[ \\t\\r\\n\\f]*\"" + type.toLowerCase() + "\"")) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(Document d, String... phrases) {
        String t = d.text().toLowerCase();
        for (String p : phrases) if (t.contains(p.toLowerCase())) return true;
        return false;
    }
}
