package dev.forte.overlandplannerv2.ai.currentWeather;

import org.springframework.ai.tool.annotation.ToolParam;

public record WeatherRequest(
        @ToolParam(description = "Latitude of the location") double latitude,
        @ToolParam(description = "Longitude of the location") double longitude
) {}