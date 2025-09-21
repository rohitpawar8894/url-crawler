package com.example.crawler.integration;

import com.example.crawler.model.FetchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Component
public class Fetcher {
    private final HttpClient client;
    private final String userAgent;
    private final Duration connectTimeout;
    private final Duration readTimeout;

    public Fetcher(@Value("${app.userAgent}") String userAgent,
                   @Value("${app.connectTimeoutMs}") int connectTimeoutMs,
                   @Value("${app.readTimeoutMs}") int readTimeoutMs) {
        this.userAgent = userAgent;
        this.connectTimeout = Duration.ofMillis(connectTimeoutMs);
        this.readTimeout = Duration.ofMillis(readTimeoutMs);
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(this.connectTimeout)
                .build();
    }

    public FetchResult fetch(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(readTimeout)
                .header("User-Agent", userAgent)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Encoding", "gzip")
                .header("Accept-Language", "en;q=0.9")
                .GET().build();
        Instant start = Instant.now();
        HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
        Instant end = Instant.now();

        String finalUrl = resp.uri().toString();
        int status = resp.statusCode();
        Map<String, List<String>> map = resp.headers().map();
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> e : map.entrySet()) {
            headers.put(e.getKey().toLowerCase(), String.join(", ", e.getValue()));
        }
        byte[] bodyBytes = resp.body();
        if (headers.getOrDefault("content-encoding", "").toLowerCase().contains("gzip")) {
            bodyBytes = gunzip(bodyBytes);
        }
        String body = decodeBody(bodyBytes, headers.get("content-type"));
        return new FetchResult(url, finalUrl, status, body, headers, end);
    }

    private static String decodeBody(byte[] bytes, String contentType) {
        Charset cs = charsetFromContentType(contentType);
        return new String(bytes, cs);
    }

    private static Charset charsetFromContentType(String contentType) {
        if (contentType == null) return StandardCharsets.UTF_8;
        for (String part : contentType.split(";")) {
            String p = part.trim().toLowerCase();
            if (p.startsWith("charset=")) {
                String cs = p.substring(8).trim();
                try { return Charset.forName(cs); } catch (Exception ignored) {}
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static byte[] gunzip(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             GZIPInputStream gis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = gis.read(buf)) != -1) baos.write(buf, 0, r);
            return baos.toByteArray();
        } catch (Exception e) {
            return data; // fallback to raw
        }
    }
}
