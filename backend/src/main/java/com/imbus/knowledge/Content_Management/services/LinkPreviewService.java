package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.LinkPreviewDto;
import org.jsoup.Jsoup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import org.jsoup.nodes.Document; // ✅ Correct — for HTML parsing

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

@Service
public class LinkPreviewService {

    private final RestTemplate restTemplate = new RestTemplate();

    public LinkPreviewDto fetchFromUrl(String url) {
        try {
            // Follow redirects automatically
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String html = response.getBody();

            Document doc = Jsoup.parse(html);
            String title = doc.select("meta[property=og:title]").attr("content");
            if (title.isEmpty()) title = doc.title();

            String description = doc.select("meta[property=og:description], meta[name=description]").attr("content");
            String image = doc.select("meta[property=og:image]").attr("content");

            // Make image URL absolute if needed
            if (image.startsWith("/") && !image.startsWith("//")) {
                URL baseUrl = new URL(url);
                image = baseUrl.getProtocol() + "://" + baseUrl.getHost() + image;
            }

            return LinkPreviewDto.builder()
                    .url(url)
                    .title(title)
                    .description(description)
                    .imageUrl(image)
                    .build();

        } catch (Exception e) {
            System.err.println("Failed to fetch preview for " + url + ": " + e.getMessage());
            return LinkPreviewDto.builder()
                    .url(url)
                    .title("No title")
                    .description("No description")
                    .imageUrl(null)
                    .build();
        }
    }
}
