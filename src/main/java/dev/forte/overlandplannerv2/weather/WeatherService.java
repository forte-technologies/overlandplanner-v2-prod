package dev.forte.overlandplannerv2.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmeteo.sdk.*;
import dev.forte.overlandplannerv2.waypoint.WaypointEntity;
import dev.forte.overlandplannerv2.waypoint.WaypointRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@Transactional
public class WeatherService {

    private final OkHttpClient okHttpClient;
    private final WeatherRepository weatherRepository;
    private final WaypointRepository waypointRepository;
    private final WeatherUpdateService weatherUpdateService;

    @Value("${weather.api.historical.baseurl}")
    private String baseUrlHist;

    @Value("${weather.api.forecast.baseurl}")
    private String baseUrlForecast;

    @Value("${weather.api.timezone}")
    private String timezone;

    @Value("${weather.api.temperature_unit}")
    private String temperatureUnit;

    public WeatherService(
            OkHttpClient okHttpClient,
            WeatherRepository weatherRepository,
            WaypointRepository waypointRepository,
            WeatherUpdateService weatherUpdateService
    ) {
        this.okHttpClient = okHttpClient;
        this.weatherRepository = weatherRepository;
        this.waypointRepository = waypointRepository;
        this.weatherUpdateService = weatherUpdateService;
    }

    /**
     * Main entry point. Figures out if we should call the forecast method or the historical method.
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrCreateWeather(Long waypointId, Double minTemp, Double maxTemp) {
        WaypointEntity waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new RuntimeException("Waypoint not found"));

        WeatherEntity weather = weatherRepository.findByWaypointId(waypointId);
        if (weather != null) {
            weather.setAvgMinTemperature(minTemp);
            weather.setAvgMaxTemperature(maxTemp);
            weatherRepository.save(weather);
        } else {
            weather = new WeatherEntity();
            weather.setWaypoint(waypoint);
            weather.setAvgMinTemperature(minTemp);
            weather.setAvgMaxTemperature(maxTemp);
            waypoint.setWeather(weather);
            waypointRepository.save(waypoint);
        }
    }



    public WeatherDTO getWeatherForWaypoint(Long userId, Long tripId, Long waypointId) {

        // Fetch the waypoint
        WaypointEntity waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new RuntimeException("Waypoint not found."));

        // Basic checks
        if (!waypoint.getTrip().getId().equals(tripId) || !waypoint.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied or invalid trip ID.");
        }
        if (waypoint.getStartDate() == null || waypoint.getEndDate() == null) {
            throw new RuntimeException("Weather data requires a start and end date.");
        }

        LocalDate now = LocalDate.now();
        LocalDate start = waypoint.getStartDate();
        LocalDate end = waypoint.getEndDate();

        // Decide if the date range is within the forecast window
        // For example, up to 15 days from today:
        long daysFromNowToEnd = ChronoUnit.DAYS.between(now, end);

        // If the user’s end date is still in the future AND within 15 days,
        // use the forecast endpoint. Otherwise, use the historical endpoint.
        if (end.isAfter(now) && daysFromNowToEnd <= 15) {
            // Use forecast
            return getForecastWeather(waypoint);
        } else {
            // Use historical
            return getHistoricalWeather(waypoint);
        }
    }

    /**
     * Fetches and parses forecast data (JSON) for the given waypoint's date range.
     */
    private WeatherDTO getForecastWeather(WaypointEntity waypoint) {
        // Build forecast URL with start_date/end_date
        // Because the official docs say it supports up to 16 days, but let's stick to 15 as an example
        String url = String.format(
                "%s?latitude=%s&longitude=%s&start_date=%s&end_date=%s" +
                        "&daily=temperature_2m_max&daily=temperature_2m_min&timezone=%s&temperature_unit=%s",
                baseUrlForecast,
                waypoint.getLatitude(),
                waypoint.getLongitude(),
                waypoint.getStartDate(),  // must be <= 15 days from now
                waypoint.getEndDate(),
                timezone,
                temperatureUnit
        );

        try (Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Forecast API call failed: " + response);
            }

            // Parse JSON response
            String jsonBody = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonBody);
            JsonNode dailyNode = root.path("daily");
            if (dailyNode.isMissingNode()) {
                throw new RuntimeException("No 'daily' field in forecast response.");
            }

            // Extract arrays
            JsonNode timeArray = dailyNode.path("time");
            JsonNode maxArray = dailyNode.path("temperature_2m_max");
            JsonNode minArray = dailyNode.path("temperature_2m_min");

            if (timeArray.isMissingNode() || maxArray.isMissingNode() || minArray.isMissingNode()) {
                throw new RuntimeException("Missing daily temperature data in forecast response.");
            }

            double sumMax = 0.0;
            double sumMin = 0.0;
            int count = 0;

            // Filter only the dates that fall within [startDate, endDate]
            for (int i = 0; i < timeArray.size(); i++) {
                String dateStr = timeArray.get(i).asText();
                LocalDate forecastDate = LocalDate.parse(dateStr);

                if (!forecastDate.isBefore(waypoint.getStartDate()) &&
                        !forecastDate.isAfter(waypoint.getEndDate())) {
                    sumMax += maxArray.get(i).asDouble();
                    sumMin += minArray.get(i).asDouble();
                    count++;
                }
            }

            if (count == 0) {
                throw new RuntimeException("No forecast data found for the requested date range.");
            }

            log.info("Forecast weather data retrieved.");
            double avgMax = Math.round(sumMax / count);
            double avgMin = Math.round(sumMin / count);

            // Save to DB
         updateOrCreateWeather(waypoint.getId(), avgMin, avgMax);

            // Return DTO
            WeatherEntity weatherEntity = weatherRepository.findByWaypointId(waypoint.getId());
            return new WeatherDTO(
                    weatherEntity.getId(),
                    waypoint.getId(),
                    weatherEntity.getAvgMinTemperature(),
                    weatherEntity.getAvgMaxTemperature(),
                    weatherEntity.getTemperatureUnit()
            );


        } catch (IOException e) {
            throw new RuntimeException("Error fetching forecast weather data", e);
        }
    }

    /**
     * Fetches and parses historical data (FlatBuffers) for the given waypoint's date range.
     */
    private WeatherDTO getHistoricalWeather(WaypointEntity waypoint) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int wayPointYear = waypoint.getStartDate().getYear();
        int diffYear = wayPointYear - currentYear;

        // Adjust the dates for historical (example logic from your code)
        LocalDate historicalStartDate = waypoint.getStartDate().minusYears(1 + diffYear);
        LocalDate historicalEndDate = waypoint.getEndDate().minusYears(1 + diffYear);

        String url = String.format(
                "%s?latitude=%s&longitude=%s&start_date=%s&end_date=%s" +
                        "&daily=temperature_2m_max&daily=temperature_2m_min&timezone=%s&temperature_unit=%s&format=flatbuffers",
                baseUrlHist,
                waypoint.getLatitude(),
                waypoint.getLongitude(),
                historicalStartDate,
                historicalEndDate,
                timezone,
                temperatureUnit
        );

        try (Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Historical API call failed: " + response);
            }
            byte[] responseBytes = response.body().bytes();
            ByteBuffer buffer = ByteBuffer.wrap(responseBytes).order(ByteOrder.LITTLE_ENDIAN);

            // Parse using FlatBuffers
            WeatherApiResponse apiResponse = WeatherApiResponse.getRootAsWeatherApiResponse(buffer.position(4));
            VariablesWithTime daily = apiResponse.daily();
            if (daily == null) {
                throw new RuntimeException("No daily data found in historical response.");
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
                throw new RuntimeException("Temperature variables not found in historical data.");
            }

            int numDays = tempMax.valuesLength();
            double sumMax = 0.0;
            double sumMin = 0.0;
            for (int i = 0; i < numDays; i++) {
                sumMax += tempMax.values(i);
                sumMin += tempMin.values(i);
            }

            double avgMax = Math.round(sumMax / numDays);
            double avgMin = Math.round(sumMin / numDays);

            // Save to DB
             updateOrCreateWeather(waypoint.getId(), avgMin, avgMax);

            // Return DTO
            WeatherEntity weatherEntity = weatherRepository.findByWaypointId(waypoint.getId());
            return new WeatherDTO(
                    weatherEntity.getId(),
                    waypoint.getId(),
                    weatherEntity.getAvgMinTemperature(),
                    weatherEntity.getAvgMaxTemperature(),
                    weatherEntity.getTemperatureUnit()
            );

        } catch (IOException e) {
            throw new RuntimeException("Error fetching historical weather data", e);
        }
    }
}
