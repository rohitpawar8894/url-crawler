package com.example.crawler.service.impl;

import com.example.crawler.model.PageData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TopicsService {
    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
            "a","an","the","and","or","but","if","then","else","when","at","by","for","with","about","against","between","into","through",
            "during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","once",
            "here","there","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own",
            "same","so","than","too","very","can","will","just","don","should","now","this","these","that","was","his","her"
    ));

    private static final Pattern WORD = Pattern.compile("[a-zA-Z][a-zA-Z\u2019\\-]+|[0-9]+");

    public List<String> keyphrases(PageData p) {
        String text = p.getText() == null ? "" : p.getText();
        Document d = Jsoup.parse(text);
        String title = p.getTitle() == null ? "" : p.getTitle();
        List<String> headings = d.select("h1, h2").eachText();

        List<String> tokens = tokenize(text.toLowerCase());
        List<String> unigrams = tokens.stream().filter(t -> !STOP.contains(t)).collect(Collectors.toList());
        List<String> bigrams = ngrams(unigrams, 2);
        List<String> trigrams = ngrams(unigrams, 3);
        Map<String, Integer> freq = new HashMap<>();
        count(freq, unigrams); count(freq, bigrams); count(freq, trigrams);

        Set<String> boosts = new HashSet<>();
        boosts.addAll(tokenize(title.toLowerCase()));
        for (String h : headings) boosts.addAll(tokenize(h.toLowerCase()));

        Map<String, Double> score = new HashMap<>();
        for (Map.Entry<String,Integer> e : freq.entrySet()) {
            double s = e.getValue();
            boolean boost = true;
            for (String w : e.getKey().split(" ")) {
                if (!boosts.contains(w)) { boost = false; break; }
            }
            if (boost) s *= 1.5;
            score.put(e.getKey(), s);
        }

        return score.entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .filter(k -> k.length() > 2)
                .filter(k -> Character.isLetter(k.charAt(0)))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        java.util.regex.Matcher m = WORD.matcher(s);
        while (m.find()) out.add(m.group().replace("\u2019", "'").toLowerCase());
        return out;
    }

    private List<String> ngrams(List<String> tokens, int n) {
        List<String> out = new ArrayList<>();
        for (int i=0; i<=tokens.size()-n; i++) {
            String gram = String.join(" ", tokens.subList(i, i+n));
            boolean containsStop = false;
            for (String w : gram.split(" ")) { if (STOP.contains(w)) { containsStop = true; break; } }
            if (!containsStop) out.add(gram);
        }
        return out;
    }

    private void count(Map<String,Integer> map, List<String> items) {
        for (String s : items) map.put(s, map.getOrDefault(s, 0) + 1);
    }
}
