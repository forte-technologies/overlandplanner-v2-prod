package dev.forte.overlandplannerv2.vehicle;

public class CreateVehicleDTO {
    private String make;
    private String model;
    private int year;
    private String modifications;

    public CreateVehicleDTO() {}

    // Getters and setters
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

    public String getModifications() {
        return modifications;
    }

    public void setModifications(String modifications) {
        this.modifications = modifications;
    }
}