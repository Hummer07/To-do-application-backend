package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.OpenAIService;
import com.example.todo.service.SlackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoRepository repository;
    private final OpenAIService openAIService;
    private final SlackService slackService;

    public TodoController(TodoRepository repository, OpenAIService openAIService, SlackService slackService) {
        this.repository = repository;
        this.openAIService = openAIService;
        this.slackService = slackService;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Todo addTodo(@RequestBody Todo todo) {
        return repository.save(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/summarize")
    public Map<String, String> summarizeAndSend() {
        List<Todo> todos = repository.findAll();
        List<String> todoTitles = todos.stream().map(Todo::getTitle).toList();
        String summary = openAIService.generateSummary(todoTitles);
        slackService.postToSlack(summary);
        return Map.of("summary", summary, "status", "sent to Slack");
    }
}
