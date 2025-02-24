package dev.forte.overlandplannerv2.weather;

import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "weather")
public class WeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long id;

    @Column(name = "waypoint_id")
    private Long waypointId;

    @Column(name = "avg_min_temperature", nullable = true)
    private Double avgMinTemperature;

    @Column(name = "avg_max_temperature", nullable = true)
    private Double avgMaxTemperature;

    @Column(nullable = false)
    private String temperatureUnit = "fahrenheit";

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
