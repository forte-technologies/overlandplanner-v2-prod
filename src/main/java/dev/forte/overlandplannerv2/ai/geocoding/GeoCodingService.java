package dev.forte.overlandplannerv2.ai.geocoding;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class GeoCodingService implements Function<CoordinatesRequest, CoordinatesResponse> {

    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Value("${mapbox.baseURL}")
    private String baseURL;

    @Value("${mapbox.accesstoken}")
    private String accessToken;


    @Override
    public CoordinatesResponse apply(CoordinatesRequest coordinatesRequest) {

        String encodedInput = URLEncoder.encode(coordinatesRequest.location(), StandardCharsets.UTF_8);
        String url = String.format("%s=%s&country=us&limit=1&autocomplete=false&access_token=%s", baseURL, encodedInput, accessToken);

        Map<String, Double> coordinates = new HashMap<>();

        try (Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String jsonResponse = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            if (rootNode.has("features") && rootNode.get("features").size() > 0) {
                JsonNode properties = rootNode.get("features").get(0).get("properties");
                JsonNode coords = properties.get("coordinates");

                coordinates.put("latitude", coords.get("latitude").asDouble());
                coordinates.put("longitude", coords.get("longitude").asDouble());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse geocoding response", e);
        }

        return new CoordinatesResponse(coordinates.get("latitude"), coordinates.get("longitude"));

    }
}