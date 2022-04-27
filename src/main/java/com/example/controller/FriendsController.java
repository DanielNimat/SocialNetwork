package com.example.controller;

import com.example.Service.Service;
import com.example.domain.FriendDTO;
import com.example.domain.Group;
import com.example.domain.GroupDTO;
import com.example.domain.User;
import com.example.social_network.TestSocial_Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.management.relation.RelationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendsController {
    Service srv;
    Long userId;

    ObservableList<FriendDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<GroupDTO> modelGroups = FXCollections.observableArrayList();
    private int from=0;
    private int to=0;
    private int itemPerPage=5;

    @FXML
    TableColumn<FriendDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnLastName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFriendsSince;
    @FXML
    TableView<FriendDTO> tableViewFriends;
    @FXML
    TextField textFieldSearchBar;
    @FXML
    TextField textFieldSendMessage;
    @FXML
    ComboBox<GroupDTO> comboBoxMyGroups;
    @FXML
    Pagination pagination;


    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("lastName"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("date"));
        //tableViewFriends.setItems(modelFriends);
        textFieldSearchBar.textProperty().addListener(o -> handleFilter());
    }


    private Node createPage(int pageIndex ){
        from=pageIndex*itemPerPage;
        to=Math.min(from+itemPerPage,modelFriends.size());
        try{
            tableViewFriends.setItems(FXCollections.observableArrayList(modelFriends.subList(from,to)));}
        catch (Exception ex){}
        return tableViewFriends;
    }

    public void setFriendsController(Service service,Long id) {
        this.srv=service;
        this.userId=id;
        modelFriends.setAll(getFriendsDTOList());
        int count=0;
        count=modelFriends.size();
        int pageCount=(count/itemPerPage) +1;
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
        setMyGroups();
    }

    private List<FriendDTO> getFriendsDTOList() {
        List<User> friendsList = new ArrayList<User>((Collection<? extends User>) srv.getFriends(userId));
        List<FriendDTO> friendsDTOList = new ArrayList<>();

        friendsList.forEach(f -> {
            try {
                friendsDTOList.add(new FriendDTO(f.getFirstName(), f.getLastName(), srv.getRelationStartDate(userId, f.getID()), "Approved", f.getID()));
            } catch (RelationException e) {
                e.printStackTrace();
            }
        });

        return friendsDTOList;
    }

    private void handleFilter() {
        Predicate<FriendDTO> p = u -> u.getFirstName().startsWith(textFieldSearchBar.getText());

        modelFriends.setAll(getFriendsDTOList()
                .stream()
                .filter(p)
                .collect(Collectors.toList()));
        int count=0;
        count=modelFriends.size();
        int pageCount=(count/itemPerPage) +1;
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
    }

    @FXML
    private void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.userPageScene("Userpage-view.fxml",userId);
    }

    @FXML
    private void removeFriend() {
        FriendDTO friend = tableViewFriends.getSelectionModel().getSelectedItem();
        if(friend!=null) {
            srv.deleteFriend(userId, friend.getId());
            modelFriends.setAll(getFriendsDTOList());
            int count=0;
            count=modelFriends.size();
            int pageCount=(count/itemPerPage) +1;
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);
        }
    }

    @FXML
    private void sendMessage() {
        if (tableViewFriends.getSelectionModel().getSelectedItem() != null) {
            ArrayList<User> usersToReceive = new ArrayList<>();
            usersToReceive.add(srv.findOne(tableViewFriends.getSelectionModel().getSelectedItem().getId()));
            if (textFieldSendMessage.getText() != "")
                srv.addMessage(userId, usersToReceive, textFieldSendMessage.getText(), 0L, 0L);
        }
    }

    private void setMyGroups() {
        modelGroups.setAll(getGroupsDTOList());
        comboBoxMyGroups.setItems(modelGroups);
        comboBoxMyGroups.setCellFactory(param -> new ListCell<GroupDTO>() {
            @Override
            protected void updateItem(GroupDTO group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null) {
                    setText(null);
                } else {
                    setText(group.getName());
                }
            }
        });
    }

    @FXML
    private void comboBoxMyGroupsSelection() {
        comboBoxMyGroups.setButtonCell(new ListCell<GroupDTO>(){
            @Override
            protected void updateItem(GroupDTO group, boolean btl){
                super.updateItem(group, btl);
                if(group != null) {
                    setText(group.getName());
                }
            }
        });
    }

    private List<GroupDTO> getGroupsDTOList() {
        List<Group> groups = srv.getGroups(userId);

        return groups
                .stream()
                .map(g -> new GroupDTO(g.getID(), g.getName()))
                .collect(Collectors.toList());
    }

    @FXML
    private void addUserToGroup() {
        if (comboBoxMyGroups.getSelectionModel().getSelectedItem() != null) {
            srv.addUserToGroup(comboBoxMyGroups.getSelectionModel().getSelectedItem().getId(), tableViewFriends.getSelectionModel().getSelectedItem().getId());
        }
    }
}
