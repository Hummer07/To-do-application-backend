package com.example.todo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String generateSummary(List<String> todos) {
        String prompt = "Summarize the following todos: " + String.join(", ", todos);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        Map response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            var choices = (List<Map<String, Object>>) response.get("choices");
            var firstChoice = choices.get(0);
            var message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        }
        return "No summary generated.";
    }
}
