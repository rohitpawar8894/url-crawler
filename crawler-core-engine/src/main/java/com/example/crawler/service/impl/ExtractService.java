package com.example.crawler.service.impl;

import com.example.crawler.dto.*;
import com.example.crawler.integration.Fetcher;
import com.example.crawler.model.FetchResult;
import com.example.crawler.model.ImageRef;
import com.example.crawler.model.LinkRef;
import com.example.crawler.model.PageData;
import com.example.crawler.service.IExtractService;
import com.example.crawler.utils.UrlNormalizer;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@Data
@Service
public class ExtractService implements IExtractService {

    private final Fetcher fetcher;
    private final RobotsService robotsService;
    private final ExtractorService extractorService;
    private final ClassificationService classificationService;
    private final TopicsService topicsService;

    public ExtractService(Fetcher fetcher, RobotsService robotsService, ExtractorService extractorService,
                          ClassificationService classificationService, TopicsService topicsService) {
        this.fetcher = fetcher;
        this.robotsService = robotsService;
        this.extractorService = extractorService;
        this.classificationService = classificationService;
        this.topicsService = topicsService;
    }

    @Override
    public ExtractResponse processExtraction(ExtractRequest req, String userAgent) throws Exception {
        String inputUrl = UrlNormalizer.normalize(req.getUrl());
        URI uri = URI.create(inputUrl);

        if (!robotsService.isAllowed(userAgent, uri)) {
            return createDeniedResponse(inputUrl);
        }

        FetchResult fetch = fetcher.fetch(inputUrl);
        PageData page = extractorService.extract(fetch);

        String pageType = classificationService.classify(page);
        page.setPageType(pageType);
        page.setTopics(topicsService.keyphrases(page));

        return createExtractResponse(inputUrl, fetch, page);
    }

    private ExtractResponse createDeniedResponse(String inputUrl) {
        ExtractResponse denied = new ExtractResponse();
        denied.setInputUrl(inputUrl);
        denied.setUrlFinal(inputUrl);
        denied.setFetchedAt(Instant.now());
        denied.setStatusCode(999); // pseudo status for robots denial
        Metadata metadata = new Metadata();
        metadata.setTitle(null);
        metadata.setDescription("Blocked by robots.txt");
        denied.setMetadata(metadata);
        Content content = new Content();
        content.setText("");
        denied.setContent(content);
        Nlp nlp = new Nlp();
        nlp.setPageType("unknown");
        denied.setNlp(nlp);
        return denied;
    }

    private ExtractResponse createExtractResponse(String inputUrl, FetchResult fetch, PageData page) {
        ExtractResponse response = new ExtractResponse();
        response.setInputUrl(inputUrl);
        response.setUrlFinal(fetch.getFinalUrl());
        response.setFetchedAt(fetch.getFetchedAt());
        response.setStatusCode(fetch.getStatusCode());

        Metadata metadata = new Metadata();
        metadata.setTitle(page.getTitle());
        metadata.setDescription(page.getDescription());
        metadata.setCanonical(page.getCanonical());
        metadata.setLang(page.getLang());
        metadata.setOg(page.getOg());
        metadata.setPublishedTime(page.getPublishedTime());
        response.setMetadata(metadata);

        Content content = new Content();
        content.setText(page.getText());
        content.setImages(page.getImages().stream().map(img -> {
            ImageRef ref = new ImageRef();
            ref.setSrc(img.getSrc());
            ref.setAlt(img.getAlt());
            return ref;
        }).collect(Collectors.toList()));
        content.setLinks(page.getLinks().stream().map(link -> {
            LinkRef ref = new LinkRef();
            ref.setHref(link.getHref());
            ref.setRel(link.getRel());
            return ref;
        }).collect(Collectors.toList()));
        response.setContent(content);

        Nlp nlp = new Nlp();
        nlp.setPageType(page.getPageType());
        nlp.setTopics(page.getTopics());
        response.setNlp(nlp);

        return response;
    }
}
