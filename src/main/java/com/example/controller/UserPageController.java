package com.example.controller;

import com.example.Service.Service;
import com.example.domain.Event;
import com.example.domain.EventDTO;
import com.example.domain.Page;
import com.example.domain.Relation;
import com.example.social_network.TestSocial_Network;
import com.example.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserPageController {
    Service srv;
    Long userId;
    Page userPage=new Page();

    @FXML
    Label labelUser;
    @FXML
    Label labelNumberOfFriends;
    @FXML
    ImageView imageViewNotificationsBell;

    public void setUserService(Service service,Long id) {
        this.srv=service;
        this.userId=id;
        setNumberOfFriends();
        labelUser.setText(srv.findOne(userId).getFirstName() + " " + srv.findOne(userId).getLastName());
        setNotificationBell();
    }
    public void logout() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.loginScene("Login-view.fxml");
    }
    public void showFriendRequestsScene() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.friendRequestsScene("Friendrequests-view.fxml",userId);
    }

    public void showSearchUsersScene() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.searchUsersScene("SearchUsers-view.fxml",userId);
    }

    public void showFriendsScene() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.friendsScene("Friends-view.fxml",userId);
    }

    public void showChatsScene() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.chatsScene("Chats-view.fxml",userId);
    }

    public void showRapoarteScene() throws  IOException{
        TestSocial_Network t=new TestSocial_Network();
        t.rapoarteScene("Rapoarte-view.fxml",userId);
    }

    public void showRapoartePrietenScene() throws  IOException{
        TestSocial_Network t=new TestSocial_Network();
        t.rapoartePrietenScene("RapoartePrieten-view.fxml",userId);}

    public void showEventsScene() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.eventsScene("Events-view.fxml",userId);
    }

    private void setNumberOfFriends() {
        labelNumberOfFriends.setText(String.valueOf(srv.getFriends(userId).size()));
    }

    private void setNotificationBell() {
        if (checkFriendRequestNotification() || checkUpcomingEventNotification()) {
            imageViewNotificationsBell.setImage(new Image("com/example/social_network/images/notificationsBellExclamationMark.png"));
        }
        else {
            imageViewNotificationsBell.setImage(new Image("com/example/social_network/images/notificationsBell.png"));
        }
    }

    private boolean checkFriendRequestNotification() {
        Iterable<Relation> relations=srv.getFriendRequests(userId);
        List<Relation> requests= StreamSupport.stream(relations.spliterator(),false)
                .collect(Collectors.toList());

        for (Relation r : requests) {
            if (r.getStatus().equals("Pending")) {
                return true;
            }
        }

        return false;
    }

    private boolean checkUpcomingEventNotification() {
        List<EventDTO> upcomingEvents = new ArrayList<>();
        LocalDate currentDate = LocalDate.parse(LocalDate.now().toString(), Constants.DATE_FORMATTER);

        for (EventDTO e : getMyEventsDTOList()) {
            LocalDate eventDate = LocalDate.parse(e.getDate(), Constants.DATE_FORMATTER);
            long daysBetween = Duration.between(currentDate.atStartOfDay(), eventDate.atStartOfDay()).toDays();
            if (daysBetween <= 2)
                return true;
        }

        return false;
    }

    private List<EventDTO> getMyEventsDTOList() {
        List<Event> eventsList = new ArrayList<Event>(srv.getMyEvents(userId));
        return eventsList
                .stream()
                .map(e -> new EventDTO(e.getID(), e.getName(), e.getDescription(), e.getLocation(), e.getDate(), e.getOrganizerID()))
                .collect(Collectors.toList());
    }
}
