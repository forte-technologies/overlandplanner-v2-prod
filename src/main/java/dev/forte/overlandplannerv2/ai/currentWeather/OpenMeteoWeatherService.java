package dev.forte.overlandplannerv2.ai.currentWeather;


import com.openmeteo.sdk.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

@Service
public class OpenMeteoWeatherService implements Function<WeatherRequest, WeatherResponse> {

    private OkHttpClient client = new OkHttpClient();

    @Override
    public WeatherResponse apply(WeatherRequest request) {
        try {
            // Build URL with format=flatbuffers
            String url = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=" + request.latitude() +
                    "&longitude=" + request.longitude() +
                    "&temperature_unit=fahrenheit" +
                    "&wind_speed_unit=mph" +
                    "&current=temperature_2m,weather_code,wind_speed_10m" +
                    "&daily=temperature_2m_max,temperature_2m_min,weather_code" +
                    "&timezone=auto" +
                    "&format=flatbuffers";
            // Make synchronous request for simplicity
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response: " + response);
                }

                byte[] responseBytes = response.body().bytes();

                // Process FlatBuffers response
                ByteBuffer buffer = ByteBuffer.wrap(responseBytes).order(ByteOrder.LITTLE_ENDIAN);

                // Skip first 4 bytes (size prefix)
                WeatherApiResponse apiResponse = WeatherApiResponse.getRootAsWeatherApiResponse(
                        (ByteBuffer) buffer.position(4)
                );

                // Extract current weather data
                VariablesWithTime current = apiResponse.current();

                VariableWithValues temperature = new VariablesSearch(current)
                        .variable(Variable.temperature)
                        .altitude(2)
                        .first();

                VariableWithValues weatherCode = new VariablesSearch(current)
                        .variable(Variable.weather_code)
                        .first();

                VariableWithValues windSpeed = new VariablesSearch(current)
                        .variable(Variable.wind_speed)
                        .altitude(10)
                        .first();

                return new WeatherResponse(
                        temperature != null ? temperature.value() : 0,
                        weatherCode != null ? (int)weatherCode.value() : 0,
                        windSpeed != null ? windSpeed.value() : 0,
                        apiResponse.timezone()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather data", e);
        }
    }
}