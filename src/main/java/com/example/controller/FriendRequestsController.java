package com.example.controller;

import com.example.Service.Service;
import com.example.domain.Event;
import com.example.domain.EventDTO;
import com.example.domain.FriendDTO;
import com.example.domain.Relation;
import com.example.social_network.TestSocial_Network;
import com.example.utils.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController {
    Service srv;
    Long userId;
    ObservableList<FriendDTO> modelRelation = FXCollections.observableArrayList();
    ObservableList<EventDTO> modelEvents = FXCollections.observableArrayList();
    private int from=0;
    private int to=0;
    private int itemPerPage=5;

    @FXML
    TableView<FriendDTO> tableView;
    @FXML
    TableColumn<FriendDTO,String> tableColumnName;
    @FXML
    TableColumn<FriendDTO,String> tableColumnPrenume;
    @FXML
    TableColumn<FriendDTO,String> tableColumnDate;
    @FXML
    TableColumn<FriendDTO,String> tableColumnStatus;
    @FXML
    TableView<EventDTO> tableViewEvents;
    @FXML
    TableColumn<EventDTO,String> tableColumnEventName;
    @FXML
    TableColumn<EventDTO,String> tableColumnEventDate;
    @FXML
    TableColumn<EventDTO,String> tableColumnLocation;
    @FXML
    TableColumn<EventDTO,String> tableColumnDescription;
    @FXML
    Pagination pagination;

    public void initialize() {

        tableColumnName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("firstName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("date"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("status"));

        tableColumnEventName.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("name"));
        tableColumnEventDate.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("date"));
        tableColumnLocation.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("location"));
        tableColumnDescription.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("description"));
    }


    private Node createPage(int pageIndex ){
        from=pageIndex*itemPerPage;
        to=Math.min(from+itemPerPage,modelRelation.size());
        try{
            tableView.setItems(FXCollections.observableArrayList(modelRelation.subList(from,to)));}
        catch (Exception ex){}
        return tableView;
    }

    private List<FriendDTO> getFriendDTOList() {
        Iterable<Relation> relations=srv.getFriendRequests(userId);

        List<Relation> requests= StreamSupport.stream(relations.spliterator(),false)
                .collect(Collectors.toList());

        return requests.stream().map(r->new FriendDTO(srv.findOne(r.getID().getRight()).getFirstName(),srv.findOne(r.getID().getRight()).getLastName(),r.getDate(),r.getStatus(),r.getID().getRight()))
            .collect(Collectors.toList());
    }


    public void setUserService(Service service,Long id) {
        this.srv=service;
        this.userId=id;
        modelRelation.setAll(getFriendDTOList());
        int count=0;
        count=modelRelation.size();
        int pageCount=(count/itemPerPage) +1;
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);

        setTableViewEvents();
    }

    public void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.userPageScene("Userpage-view.fxml",userId);
    }

    public void acceptFriendRequest() throws Exception {
        FriendDTO friend = tableView.getSelectionModel().getSelectedItem();
        if(friend!=null) {
            srv.approveRequest(userId,friend.getId());
            modelRelation.setAll(getFriendDTOList());
            tableView.setItems(modelRelation);
        }

    }

    public void cancelFriendRequest()throws Exception{
        FriendDTO friend = tableView.getSelectionModel().getSelectedItem();
        if(friend!=null)
        {srv.cancelRequest(userId,friend.getId());
            modelRelation.setAll(getFriendDTOList());}
    }

    public void rejectFriendRequest() throws Exception {
        FriendDTO friend = tableView.getSelectionModel().getSelectedItem();
        if(friend!=null)
        {srv.rejectRequest(userId, friend.getId());
        modelRelation.setAll(getFriendDTOList());}

    }

    private void setTableViewEvents() {
        List<EventDTO> upcomingEvents = new ArrayList<>();
        LocalDate currentDate = LocalDate.parse(LocalDate.now().toString(), Constants.DATE_FORMATTER);

        for (EventDTO e : getMyEventsDTOList()) {
            LocalDate eventDate = LocalDate.parse(e.getDate(), Constants.DATE_FORMATTER);
            long daysBetween = Duration.between(currentDate.atStartOfDay(), eventDate.atStartOfDay()).toDays();
            if (daysBetween <= 2)
                upcomingEvents.add(e);
        }

        modelEvents.setAll(upcomingEvents);
        tableViewEvents.setItems(modelEvents);
    }

    private List<EventDTO> getMyEventsDTOList() {
        List<Event> eventsList = new ArrayList<Event>(srv.getMyEvents(userId));
        return eventsList
                .stream()
                .map(e -> new EventDTO(e.getID(), e.getName(), e.getDescription(), e.getLocation(), e.getDate(), e.getOrganizerID()))
                .collect(Collectors.toList());
    }
}
