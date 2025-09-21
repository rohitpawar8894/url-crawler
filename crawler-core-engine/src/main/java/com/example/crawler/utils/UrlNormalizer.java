package com.example.crawler.utils;

import java.net.URI;

public final class UrlNormalizer {
    private UrlNormalizer() {}

    public static String normalize(String url) {
        if (url == null) throw new IllegalArgumentException("url is null");
        URI u = URI.create(url.trim());
        String scheme = (u.getScheme() == null) ? "https" : u.getScheme().toLowerCase();
        String host = u.getHost();
        if (host == null) {
            return scheme + "://" + url;
        }
        int port = u.getPort();
        String normalized = scheme + "://" + host.toLowerCase() + (port > 0 ? (":" + port) : "") + (u.getRawPath() == null ? "/" : u.getRawPath());
        if (u.getRawQuery() != null) normalized += "?" + u.getRawQuery();
        if (u.getRawFragment() != null) normalized += "#" + u.getRawFragment();
        return normalized;
    }
}
