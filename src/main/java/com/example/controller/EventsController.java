package com.example.controller;

import com.example.Service.Service;
import com.example.domain.Event;
import com.example.domain.EventDTO;
import com.example.domain.User;
import com.example.domain.UserDTO;
import com.example.social_network.TestSocial_Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EventsController {
    Service srv;
    Long userId;

    ObservableList<EventDTO> modelEvents = FXCollections.observableArrayList();
    ObservableList<UserDTO> modelParticipants = FXCollections.observableArrayList();
    ObservableList<EventDTO> modelMyEvents = FXCollections.observableArrayList();

    @FXML
    ListView<EventDTO> listViewEvents;
    @FXML
    ListView<UserDTO> listViewParticipants;
    @FXML
    ListView<EventDTO> listViewMyEvents;
    @FXML
    Label labelName;
    @FXML
    Label labelLocation;
    @FXML
    Label labelDate;
    @FXML
    Label labelDescription;
    @FXML
    Label labelOrganizer;

    public void setEventsController(Service service, Long id) {
        this.srv=service;
        this.userId=id;
        setListViewEvents();
        setListViewMyEvents();
    }

    private void setListViewEvents() {
        modelEvents.setAll(getEventsDTOList());
        listViewEvents.setItems(modelEvents);
        listViewEvents.setCellFactory(param -> new ListCell<EventDTO>() {
            @Override
            protected void updateItem(EventDTO event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getName());
                }
            }
        });
        listViewEvents.getSelectionModel().select(0);
    }

    private List<EventDTO> getEventsDTOList() {
        List<Event> eventsList = new ArrayList<Event>((Collection<? extends Event>) srv.getEvents());
        return eventsList
                .stream()
                .map(e -> new EventDTO(e.getID(), e.getName(), e.getDescription(), e.getLocation(), e.getDate(), e.getOrganizerID()))
                .collect(Collectors.toList());
    }

    @FXML
    private void setEventDetails() {
        EventDTO event = listViewEvents.getSelectionModel().getSelectedItem();
        labelName.setText(event.getName());
        labelLocation.setText(event.getLocation());
        labelDate.setText(event.getDate());
        labelDescription.setText(event.getDescription());
        labelOrganizer.setText(srv.getUser(event.getOrganizerID()).getFirstName() + " " + srv.getUser(event.getOrganizerID()).getLastName());
        setListViewParticipants(listViewEvents);
    }

    @FXML
    private void setMyEventDetails() {
        EventDTO event = listViewMyEvents.getSelectionModel().getSelectedItem();
        labelName.setText(event.getName());
        labelLocation.setText(event.getLocation());
        labelDate.setText(event.getDate());
        labelDescription.setText(event.getDescription());
        labelOrganizer.setText(srv.getUser(event.getOrganizerID()).getFirstName() + " " + srv.getUser(event.getOrganizerID()).getLastName());
        setListViewParticipants(listViewMyEvents);
    }

    @FXML
    private void createEvent() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.createEventScene("CreateEvent-view.fxml",userId);
        setListViewMyEvents();
    }

    @FXML
    private void joinEvent() {
        if (listViewEvents.getSelectionModel().getSelectedItem().getOrganizerID() != userId) {
            System.out.println(listViewEvents.getSelectionModel().getSelectedItem().getId());
            srv.joinEvent(userId, listViewEvents.getSelectionModel().getSelectedItem().getId());
        }
        setListViewParticipants(listViewEvents);
        setListViewMyEvents();
    }

    private void setListViewParticipants(ListView<EventDTO> listView) {
        modelParticipants.setAll(getParticipantsDTOList(listView));
        listViewParticipants.setItems(modelParticipants);
        listViewParticipants.setCellFactory(param -> new ListCell<UserDTO>() {
            @Override
            protected void updateItem(UserDTO user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getFirstName() + " " + user.getLastName());
                }
            }
        });
    }

    private List<UserDTO> getParticipantsDTOList(ListView<EventDTO> listView) {
        List<User> usersList = new ArrayList<User>(srv.getEventParticipants(listView.getSelectionModel().getSelectedItem().getId()));
        return usersList
                .stream()
                .map(u -> new UserDTO(u.getID(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    @FXML
    private void setListViewMyEvents() {
        modelMyEvents.setAll(getMyEventsDTOList());
        listViewMyEvents.setItems(modelMyEvents);
        listViewMyEvents.setCellFactory(param -> new ListCell<EventDTO>() {
            @Override
            protected void updateItem(EventDTO event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getName());
                }
            }
        });
    }

    private List<EventDTO> getMyEventsDTOList() {
        List<Event> eventsList = new ArrayList<Event>(srv.getMyEvents(userId));
        return eventsList
                .stream()
                .map(e -> new EventDTO(e.getID(), e.getName(), e.getDescription(), e.getLocation(), e.getDate(), e.getOrganizerID()))
                .collect(Collectors.toList());
    }

    public void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.userPageScene("Userpage-view.fxml",userId);
    }
}
