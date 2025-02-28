package dev.forte.overlandplannerv2.ai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.forte.overlandplannerv2.trip.TripRepository;
import dev.forte.overlandplannerv2.vehicle.dtos.VehicleDTO;
import dev.forte.overlandplannerv2.vehicle.services.VehicleService;
import dev.forte.overlandplannerv2.waypoint.WaypointRepository;
import dev.forte.overlandplannerv2.weather.WeatherEntity;
import dev.forte.overlandplannerv2.weather.WeatherRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAssistantService {

    private final ChatClient wayPointTipClient;
    private final ChatClient tripAssistantClient;
    private final ChatClient vehicleAssistantClient;
    private final VehicleService vehicleService;
    private final WaypointRepository waypointRepository;
    private final WeatherRepository weatherRepository;
    private final ObjectMapper objectMapper;


    public AiAssistantService(@Qualifier("wayPointTips") ChatClient wayPointTipClient,
                              @Qualifier("tripAssistant") ChatClient tripAssistantClient,
                              @Qualifier("vehicleAssistant") ChatClient vehicleAssistantClient,
                              VehicleService vehicleService, TripRepository tripRepository, WaypointRepository waypointRepository, WeatherRepository weatherRepository, ObjectMapper objectMapper) {
        this.vehicleService = vehicleService;
        this.waypointRepository = waypointRepository;
        this.weatherRepository = weatherRepository;
        this.wayPointTipClient = wayPointTipClient;
        this.tripAssistantClient = tripAssistantClient;
        this.vehicleAssistantClient = vehicleAssistantClient;
        this.objectMapper = objectMapper;
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

        String response = wayPointTipClient.prompt()
                .user(promptBuilder.toString())
                .call()
                .content();
        System.out.println("Sending waypoint tip response: " + response); // Add logging
        return response;


    }

    public String tripAssistant(String message){
        return tripAssistantClient.prompt()
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

        return vehicleAssistantClient.prompt()
                .user(promptBuilder.toString())
                .call()
                .content();
    }
}
