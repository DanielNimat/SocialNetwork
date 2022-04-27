package com.example.domain;

public class FriendDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String date;
    private String status;

    public FriendDTO(String firstName, String lastName, String date,String status,Long id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.status=status;
        this.id=id;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName+" "+date+" "+status;/////////////////////////////
    }
}
