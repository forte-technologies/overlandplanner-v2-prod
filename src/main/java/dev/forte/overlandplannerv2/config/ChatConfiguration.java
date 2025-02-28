package dev.forte.overlandplannerv2.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfiguration {

    @Bean
    @Qualifier("wayPointTips")
    public ChatClient wayPointTipClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("You are a trip assistant that helps people with their " +
                        "overlanding related questions about public land, national forests, parks, and " +
                        "offroading, answer in 600 characters or less, unless you receive other" +
                        "instructions for a shorter a response. If you recieve the mininum and maximum temperatures, give tips about either staying warm or cool. You will" +
                        "be giving tips about waypoints, don't say it looks like you're referencing a waypoint. Do not use any formatting in your response like bold or bullet points, just normal text and periods. Do not number things in your response, respond with a paragraph. ")
                .build();
    }

    @Bean
    @Qualifier("tripAssistant")
    public ChatClient tripAssistantClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("You are a trip assistant that helps people with their " +
                        "overlanding related questions about public land, national forests, parks, and " +
                        "offroading, here is the user's input, answer in 600 characters or less: ")
                .build();
    }

    @Bean
    @Qualifier("vehicleAssistant")
    public ChatClient vehicleAssistantClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("You are an overlanding assistant that helps people with their " +
                        "overlanding related questions about public land, national forests, parks, and " +
                        "offroading, with their vehicles and their vehicles' capabilities in mind, if the user has a vehicle it will be " +
                        "included in the prompt" +
                        " here is the user's input (and vehicle/s if it exists), answer in 600 characters or less: ")
                .build();
    }


}