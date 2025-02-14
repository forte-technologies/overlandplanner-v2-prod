package dev.forte.overlandplannerv2.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/ai")
public class AiAssistantController {

    private final ChatClient chatClient;

    public AiAssistantController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/trip-assistant")
    public ResponseEntity<?> generation(@RequestParam String userInput) {
        String response = this.chatClient.prompt()
                .user("You are a trip assistant that helps people with their " +
                        "overlanding related questions about public land, national forests, parks, and " +
                        "offroading, here is the user's input, answer in 350 characters or less: " + userInput)
                .call()
                .content();
        // Wrap the response in a JSON object
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", response);
        return ResponseEntity.ok(jsonResponse);

    }

}