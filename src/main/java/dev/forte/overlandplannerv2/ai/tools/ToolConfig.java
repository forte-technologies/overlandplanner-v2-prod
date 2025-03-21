package dev.forte.overlandplannerv2.ai.tools;

import dev.forte.overlandplannerv2.ai.currentWeather.OpenMeteoWeatherService;
import dev.forte.overlandplannerv2.ai.currentWeather.WeatherRequest;
import dev.forte.overlandplannerv2.ai.geocoding.CoordinatesRequest;
import dev.forte.overlandplannerv2.ai.geocoding.GeoCodingService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean
    public ToolCallback weatherToolCallback(OpenMeteoWeatherService weatherService) {
        return FunctionToolCallback
                .builder("getCurrentWeather", weatherService)
                .description("Get current weather conditions for a specific latitude and longitude")
                .inputType(WeatherRequest.class)
                .build();
    }

    @Bean
    public ToolCallback geoToolCallback(GeoCodingService geoCodingService) {
        return FunctionToolCallback
                .builder("getCoordinates", geoCodingService)
                .description("get the coordinates for the location given by the user")
                .inputType(CoordinatesRequest.class)
                .build();
    }
}
