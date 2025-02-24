package dev.forte.overlandplannerv2.weather;

public class WeatherDTO {

    private Long weatherId;
    private Long waypointId;
    private Double avgMinTemperature;
    private Double avgMaxTemperature;
    private String temperatureUnit;

    public WeatherDTO(Long weatherId, Long waypointId, Double avgMinTemperature, Double avgMaxTemperature, String temperatureUnit) {
        this.weatherId = weatherId;
        this.waypointId = waypointId;
        this.avgMinTemperature = avgMinTemperature;
        this.avgMaxTemperature = avgMaxTemperature;
        this.temperatureUnit = temperatureUnit;
    }

    public WeatherDTO() {}

    // Getters and setters
    public Long getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(Long weatherId) {
        this.weatherId = weatherId;
    }

    public Long getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(Long waypointId) {
        this.waypointId = waypointId;
    }

    public Double getAvgMinTemperature() {
        return avgMinTemperature;
    }

    public void setAvgMinTemperature(Double avgMinTemperature) {
        this.avgMinTemperature = avgMinTemperature;
    }

    public Double getAvgMaxTemperature() {
        return avgMaxTemperature;
    }

    public void setAvgMaxTemperature(Double avgMaxTemperature) {
        this.avgMaxTemperature = avgMaxTemperature;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }
}
