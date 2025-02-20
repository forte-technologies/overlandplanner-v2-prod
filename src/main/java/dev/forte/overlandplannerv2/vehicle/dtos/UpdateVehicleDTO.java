package dev.forte.overlandplannerv2.vehicle.dtos;

public class UpdateVehicleDTO {
    private String make;
    private String model;
    private Integer year;
    private String modifications;

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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getModifications() {
        return modifications;
    }

    public void setModifications(String modifications) {
        this.modifications = modifications;
    }
// Getters and Setters
}
