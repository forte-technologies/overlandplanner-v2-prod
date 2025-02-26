package dev.forte.overlandplannerv2.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static dev.forte.overlandplannerv2.jwtconfig.AuthUtils.getAuthenticatedUserId;


@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/ai")
public class AiAssistantController {

    private final ChatClient chatClient;
    private final AiAssistantService aiAssistantService;

    public AiAssistantController(ChatClient.Builder chatClientBuilder, AiAssistantService aiAssistantService) {
        this.chatClient = chatClientBuilder.build();
        this.aiAssistantService = aiAssistantService;
    }

    @GetMapping("/trip-assistant")
    public ResponseEntity<?> generation(@RequestParam String userInput) {
        String response = aiAssistantService.tripAssistant(userInput);
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", response);
        return ResponseEntity.ok(jsonResponse);

    }

    @GetMapping("/waypoint-tip/{waypointId}")
    public ResponseEntity<?> getTripTip(@PathVariable Long waypointId) {
        var response = aiAssistantService.getWayPointTip(waypointId);
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", response);
        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("vehicle-assistant")
    public ResponseEntity<?> getVehicleAssistant(Authentication authentication, @RequestParam String userInput) {

        Long userId = getAuthenticatedUserId(authentication);
        var response = aiAssistantService.vehicleAssistant(userInput, userId);
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", response);
        return ResponseEntity.ok(jsonResponse);
    }
}