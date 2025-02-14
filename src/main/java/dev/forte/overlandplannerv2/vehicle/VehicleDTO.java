package dev.forte.overlandplannerv2.vehicle;

public class VehicleDTO {
    private Long id;
    private Long userId;
    private String make;
    private String model;
    private int year;
    private String modifications;

    // ✅ Add this constructor
    public VehicleDTO(VehicleEntity vehicle) {
        this.id = vehicle.getId();
        this.userId = vehicle.getUserId(); // Fetch user ID instead of full UserEntity
        this.make = vehicle.getMake();
        this.model = vehicle.getModel();
        this.year = vehicle.getYear();
        this.modifications = vehicle.getModifications();
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCustomizations() {
        return modifications;
    }

    public void setCustomizations(String modifications) {
        this.modifications = modifications;
    }
}