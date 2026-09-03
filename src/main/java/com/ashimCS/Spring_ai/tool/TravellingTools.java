package com.ashimCS.Spring_ai.tool;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TravellingTools {

    // tool desc is Critical for the LLM to understand, when to use it).
    @Tool(description = "Get the weather of a city")
    public String getWeather(@ToolParam(description = "The city for which to get the weather information", required = true) String city){
        return switch (city) {
            case "London" -> "Cloudy, 15 Degree";
            case "Delhi" -> "Sunny, 35 Degree";
            case "Paris" -> "Rainy, 12 Degree";
            default -> "cannot identify the city";
        };
    }
}
