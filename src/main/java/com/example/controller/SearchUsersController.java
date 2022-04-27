package com.example.controller;

import com.example.Service.Service;
import com.example.domain.Group;
import com.example.domain.GroupDTO;
import com.example.domain.User;
import com.example.domain.UserDTO;
import com.example.social_network.TestSocial_Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchUsersController {
    Service srv;
    Long userId;
    private int from=0;
    private int to=0;
    private int itemPerPage=5;

    ObservableList<UserDTO> modelUsers = FXCollections.observableArrayList();
    ObservableList<GroupDTO> modelGroups = FXCollections.observableArrayList();

    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;
    @FXML
    ComboBox<GroupDTO> comboBoxMyGroups;
    @FXML
    TableView<UserDTO> tableViewUsers;
    @FXML
    TextField textFieldSearchBar;
    @FXML
    TextField textFieldSendMessage;
    @FXML
    Pagination pagination;


    public void initialize() {

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        //tableViewUsers.setItems(modelUsers);
        textFieldSearchBar.textProperty().addListener(o -> handleFilter());
    }


    private Node createPage(int pageIndex ){
        from=pageIndex*itemPerPage;
        to=Math.min(from+itemPerPage,modelUsers.size());
        try {
            tableViewUsers.setItems(FXCollections.observableArrayList(modelUsers.subList(from,to)));
        }
        catch (Exception ex){}
        return tableViewUsers;
    }

    public void setSearchUsersService(Service service,Long id) {
        this.srv=service;
        this.userId=id;
        modelUsers.setAll(getUsersDTOList());
        int count=0;
        count=modelUsers.size();
        int pageCount=(count/itemPerPage) +1;
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
        setMyGroups();
    }

    private List<UserDTO> getUsersDTOList() {
        List<User> usersList = new ArrayList<User>((Collection<? extends User>) srv.getUsers());
        return usersList
                .stream()
                .map(u -> new UserDTO(u.getID(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    private void handleFilter() {
        Predicate<UserDTO> p = u -> u.getFirstName().startsWith(textFieldSearchBar.getText());

        modelUsers.setAll(getUsersDTOList()
                .stream()
                .filter(p)
                .collect(Collectors.toList()));
        int count=0;
        count=modelUsers.size();
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
    private void sendFriendRequest() throws Exception {
        UserDTO user = tableViewUsers.getSelectionModel().getSelectedItem();
        if(user!=null) {
            srv.sendRequest(userId, user.getId());
        }
    }

    @FXML
    private void sendMessage() {
        if (tableViewUsers.getSelectionModel().getSelectedItem() != null) {
            ArrayList<User> usersToReceive = new ArrayList<>();
            usersToReceive.add(srv.findOne(tableViewUsers.getSelectionModel().getSelectedItem().getId()));
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
            srv.addUserToGroup(comboBoxMyGroups.getSelectionModel().getSelectedItem().getId(), tableViewUsers.getSelectionModel().getSelectedItem().getId());
        }
    }
}
