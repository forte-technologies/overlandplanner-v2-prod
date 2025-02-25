package dev.forte.overlandplannerv2.weather;

import dev.forte.overlandplannerv2.waypoint.WaypointEntity;
import jakarta.persistence.*;

import java.util.Date;
@Entity(name = "weather")
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "waypoint_id", unique = true)
    private WaypointEntity waypoint;

    @Column(name = "avg_min_temperature", nullable = true)
    private Double avgMinTemperature;

    @Column(name = "avg_max_temperature", nullable = true)
    private Double avgMaxTemperature;

    @Column(nullable = false)
    private String temperatureUnit = "fahrenheit";

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WaypointEntity getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(WaypointEntity waypoint) {
        this.waypoint = waypoint;

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
