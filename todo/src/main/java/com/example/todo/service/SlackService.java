package com.example.todo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class SlackService {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public void postToSlack(String message) {
        WebClient.create(slackWebhookUrl)
                .post()
                .bodyValue(Map.of("text", message))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
