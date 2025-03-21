package dev.forte.overlandplannerv2.ai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.forte.overlandplannerv2.vehicle.dtos.VehicleDTO;
import dev.forte.overlandplannerv2.vehicle.services.VehicleService;
import dev.forte.overlandplannerv2.weather.WeatherEntity;
import dev.forte.overlandplannerv2.weather.WeatherRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAssistantService {

    private final VehicleService vehicleService;
    private final WeatherRepository weatherRepository;
    private final ObjectMapper objectMapper;
    private final ChatClient chatClient;
    private final ToolCallback weatherToolCallback;
    private final ToolCallback geoToolCallback;

    public AiAssistantService(
            VehicleService vehicleService, WeatherRepository weatherRepository, ObjectMapper objectMapper,
            OpenAiChatModel openAiChatModel, ToolCallback weatherToolCallback, ToolCallback geoToolCallback) {
        this.vehicleService = vehicleService;
        this.weatherRepository = weatherRepository;
        this.objectMapper = objectMapper;

        this.chatClient = ChatClient.builder(openAiChatModel).build();
        this.weatherToolCallback = weatherToolCallback;
        this.geoToolCallback = geoToolCallback;
    }

    public String getWayPointTip(Long waypointId) {

        WeatherEntity weatherDTO = weatherRepository.findByWaypointId(waypointId);
        var minTemp = weatherDTO.getAvgMinTemperature();
        var maxTemp = weatherDTO.getAvgMaxTemperature();
        StringBuilder promptBuilder = new StringBuilder();

        if (minTemp != null && maxTemp != null) {
            promptBuilder.append("The minimum and maximum average temperatures in fahrenheit for this waypoint are, respectively: ")
                    .append(minTemp).append(" , ").append(maxTemp).append("\n");
        } else {
            promptBuilder.append("Give a random tip about overlanding and remote camping in 250 characters or less.");
        }

       return chatClient
               .prompt()
               .system(AiPrompts.WAYPOINT_TIP)
               .user(promptBuilder.toString())
               .call()
               .content();
    }

    public String tripAssistant(String message){
        return chatClient
                .prompt()
                .system(AiPrompts.TRIP_ASSISTANT)
                .user(message)
                .call()
                .content();
    }

    public String vehicleAssistant(String message, Long userId){
        List<VehicleDTO> vehicleList = vehicleService.getVehiclesByUser(userId);
        String vehiclesJson;
        try {
            vehiclesJson = objectMapper.writeValueAsString(vehicleList);
        } catch (JsonProcessingException e) {
            // Handle the exception appropriately
            vehiclesJson = "[]"; // or throw a custom exception
        }
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Here are the user's vehicles: ")
                .append(vehiclesJson)
                .append("\n")
                .append("Here is the users message: ")
                .append(message);

         return chatClient
                 .prompt()
                 .system(AiPrompts.VEHICLE_ASSISTANT)
                 .user(promptBuilder.toString())
                 .call()
                 .content();
    }

    public String weatherAssistant(String message){
        return chatClient
                .prompt()
                .system("You are a weather assistant. Do not format your data with Bold text. Use dashes for bullet points as needed.")
                .user(message)
                .tools(weatherToolCallback,geoToolCallback)
                .call()
                .content();
    }
}
