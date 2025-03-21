package dev.forte.overlandplannerv2.ai.currentWeather;

public record WeatherResponse(
        double temperature,
        int weatherCode,
        double windSpeed,
        String timezone
) {}