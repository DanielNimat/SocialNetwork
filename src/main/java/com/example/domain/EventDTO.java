package com.example.domain;

public class EventDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String date;
    private Long organizerID;

    public EventDTO(Long id, String name, String description, String location, String date, Long creatorID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.organizerID = creatorID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getOrganizerID() {
        return organizerID;
    }

    public void setCreatorID(Long organizerID) {
        this.organizerID = organizerID;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", creatorID=" + organizerID +
                '}';
    }
}
