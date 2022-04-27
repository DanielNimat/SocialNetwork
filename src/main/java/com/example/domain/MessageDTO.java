package com.example.domain;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
    private Long id;
    private User from;
    private String firstName;
    private String lastName;
    private List<User> to;
    private String message;
    private LocalDateTime dateTime;
    private Long repliedTo;
    private Long group;

    public MessageDTO(Long id, User from, List<User> to, String message,LocalDateTime dateTime, long repliedTo, Long group) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.firstName= from.getFirstName();
        this.lastName=from.getLastName();
        this.message = message;
        this.repliedTo = repliedTo;
        this.group = group;
        this.dateTime=dateTime;
    }



    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getID() {
        return id;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Long getRepliedTo() {
        return repliedTo;
    }

    public Long getGroup() {
        return group;
    }

    @Override
    public String toString() {

        return  from.getFirstName()+" "+from.getLastName()+":"+message;


    }
}
