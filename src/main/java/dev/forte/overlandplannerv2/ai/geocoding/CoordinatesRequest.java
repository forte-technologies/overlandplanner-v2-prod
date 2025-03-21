package dev.forte.overlandplannerv2.ai.geocoding;

import org.springframework.ai.tool.annotation.ToolParam;

public record CoordinatesRequest(

        @ToolParam(description = "Location for coordinates request") String location
) {}