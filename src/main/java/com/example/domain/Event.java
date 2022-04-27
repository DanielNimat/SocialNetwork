package com.example.domain;

public class Event extends Entity<Long> {
    private String name;
    private String description;
    private String location;
    private String date;
    private Long organizerID;

    public Event(String name, String description, String location, String date, Long organizerID) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.organizerID = organizerID;
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

    public void setCreatorID(Long creatorID) {
        this.organizerID = creatorID;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
