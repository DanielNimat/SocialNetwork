package com.example.domain;

import java.util.List;

public class Page {
    private String firstName;
    private String lastName;
    private List<User> friendList;
    private List<MessageDTO> messageList;
    private List<FriendDTO> friendRequests;

//    public Page(String firstName, String lastName, List<User> friendList, List<MessageDTO> messageList, List<FriendDTO> friendRequests) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.friendList = friendList;
//        this.messageList = messageList;
//        this.friendRequests = friendRequests;
//    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public List<MessageDTO> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageDTO> messageList) {
        this.messageList = messageList;
    }

    public List<FriendDTO> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<FriendDTO> friendRequests) {
        this.friendRequests = friendRequests;
    }


    @Override
    public String toString() {
        return "Page{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friendList=" + friendList +
                ", messageList=" + messageList +
                ", friendRequests=" + friendRequests +
                '}';
    }
}
