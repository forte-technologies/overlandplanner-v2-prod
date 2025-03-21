package dev.forte.overlandplannerv2.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfiguration {

    @Value("${spring.ai.openai.api-key}")
    private String openaiApiKey;



    @Bean
    public OpenAiChatModel openAiChatModel() {

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(openaiApiKey)
                .build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.9)
                .maxTokens(500)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();

    }


}