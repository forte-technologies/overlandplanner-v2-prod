package dev.forte.overlandplannerv2.weather;

import com.openmeteo.sdk.*;
import dev.forte.overlandplannerv2.waypoint.WaypointEntity;
import dev.forte.overlandplannerv2.waypoint.WaypointRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;

@Service
public class WeatherService {

    private final OkHttpClient okHttpClient;
    private final WeatherRepository weatherRepository;
    private final WaypointRepository waypointRepository;

    @Value("${weather.api.baseurl}")
    private String baseUrl;

    @Value("${weather.api.timezone}")
    private String timezone;

    @Value("${weather.api.temperature_unit}")
    private String temperatureUnit;

    public WeatherService(OkHttpClient okHttpClient, WeatherRepository weatherRepository, WaypointRepository waypointRepository) {
        this.okHttpClient = okHttpClient;
        this.weatherRepository = weatherRepository;
        this.waypointRepository = waypointRepository;
    }

    public WeatherDTO getWeatherForWaypoint(Long userId, Long tripId, Long waypointId) {
        WaypointEntity waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new RuntimeException("Waypoint not found."));

        if (!waypoint.getTrip().getId().equals(tripId) || !waypoint.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied or invalid trip ID.");
        }

        if (waypoint.getStartDate() == null || waypoint.getEndDate() == null) {
            throw new RuntimeException("Weather data requires a start and end date.");
        }

        LocalDate historicalStartDate = waypoint.getStartDate().minusYears(1);
        LocalDate historicalEndDate = waypoint.getEndDate().minusYears(1);

        String url = String.format(
                "%s?latitude=%s&longitude=%s&start_date=%s&end_date=%s" +
                        "&daily=temperature_2m_max&daily=temperature_2m_min&timezone=%s&temperature_unit=%s&format=flatbuffers",
                baseUrl,
                waypoint.getLatitude(),
                waypoint.getLongitude(),
                historicalStartDate,
                historicalEndDate,
                timezone,
                temperatureUnit
        );

        try (Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch weather data: " + response);
            }
            byte[] responseBytes = response.body().bytes();
            ByteBuffer buffer = ByteBuffer.wrap(responseBytes).order(ByteOrder.LITTLE_ENDIAN);
            // Adjust for FlatBuffers offset as needed.
            WeatherApiResponse apiResponse = WeatherApiResponse.getRootAsWeatherApiResponse(buffer.position(4));

            VariablesWithTime daily = apiResponse.daily();
            if (daily == null) {
                throw new RuntimeException("No daily data available.");
            }

            VariableWithValues tempMax = new VariablesSearch(daily)
                    .variable(Variable.temperature)
                    .aggregation(Aggregation.maximum)
                    .first();
            VariableWithValues tempMin = new VariablesSearch(daily)
                    .variable(Variable.temperature)
                    .aggregation(Aggregation.minimum)
                    .first();

            if (tempMax == null || tempMin == null) {
                throw new RuntimeException("Temperature variables not found.");
            }

            int numDays = tempMax.valuesLength();
            double sumMax = 0.0;
            double sumMin = 0.0;

            for (int i = 0; i < numDays; i++) {
                sumMax += tempMax.values(i);
                sumMin += tempMin.values(i);
            }
            double avgMax = sumMax / numDays;
            double avgMin = sumMin / numDays;

            // Create and persist the WeatherEntity.
            WeatherEntity weatherEntity = new WeatherEntity();
            weatherEntity.setWaypointId(waypoint.getId());
            weatherEntity.setAvgMaxTemperature(avgMax);
            weatherEntity.setAvgMinTemperature(avgMin);
            weatherEntity.setTemperatureUnit(temperatureUnit);

            weatherEntity = weatherRepository.save(weatherEntity);

            // Map the saved entity to the DTO including IDs.
            return new WeatherDTO(
                    weatherEntity.getId(),       // weatherId
                    waypoint.getId(),            // waypointId
                    weatherEntity.getAvgMinTemperature(),
                    weatherEntity.getAvgMaxTemperature(),
                    weatherEntity.getTemperatureUnit()
            );

        } catch (IOException e) {
            throw new RuntimeException("Error fetching weather data", e);
        }
    }
}

