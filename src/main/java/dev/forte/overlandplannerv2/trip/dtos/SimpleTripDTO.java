package dev.forte.overlandplannerv2.trip.dtos;

public class SimpleTripDTO {

    private Long id;
    private Long userId;
    private String name;


    public SimpleTripDTO(Long id, Long userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
