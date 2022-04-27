package com.example.controller;

import com.example.Service.Service;
import com.example.social_network.TestSocial_Network;
import com.example.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class CreateEventController {
    Service srv;
    Long userId;

    @FXML
    TextField textFieldName;
    @FXML
    TextArea textAreaDescription;
    @FXML
    TextField textFieldLocation;
    @FXML
    DatePicker datePickerDate;

    public void setCreateEventController(Service service, Long id) {
        this.srv=service;
        this.userId=id;
    }

    @FXML
    private void createEvent() throws IOException, InterruptedException {
        String name, description, location, date;
        name = textFieldName.getText();
        description = textAreaDescription.getText();
        location = textFieldLocation.getText();
        date = Constants.DATE_FORMATTER.format(datePickerDate.getValue());
        srv.createEvent(name, description, location, date, userId);

        TestSocial_Network t=new TestSocial_Network();
        t.eventsScene("Events-view.fxml",userId);
    }

    public void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.eventsScene("Events-view.fxml",userId);
    }
}
