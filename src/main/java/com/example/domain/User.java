package com.example.domain;

import java.util.ArrayList;
import java.util.List;

public class User extends Entity<Long> {
    //Private variables
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<User> friendList;


    public User(String firstName, String lastName,String email,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email=email;
        this.password=password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param friend entity must be not null
     * adds a friend to the user's friendlist
     */
    public void addFriend(User friend) {
        if (friendList == null)
            this.friendList = new ArrayList<>();
        this.friendList.add(friend);
    }

    /**
     * removes all friends from the user's friendlist
     */
    public void deleteAllFriends() {
        if (this.friendList != null)
            this.friendList.clear();
    }

    /**
     * @return the user's friendlist
     */
    public List<User> getFriendList() {
        if (friendList == null)
            this.friendList = new ArrayList<>();
        return friendList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return id + " " + firstName + " " + lastName;
    }
}
