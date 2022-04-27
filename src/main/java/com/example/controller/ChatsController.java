package com.example.controller;

import com.example.Service.Service;
import com.example.domain.*;
import com.example.social_network.TestSocial_Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class ChatsController {
    Service srv;
    Long userId;

    ObservableList<UserDTO> modelUserChats = FXCollections.observableArrayList();
    ObservableList<MessageDTO> modelMessages = FXCollections.observableArrayList();
    ObservableList<GroupDTO> modelGroups = FXCollections.observableArrayList();
    ObservableList<UserDTO> modelUsersInGroup = FXCollections.observableArrayList();

    @FXML
    ListView<UserDTO> listViewUserChats;
    @FXML
    ListView<MessageDTO> listViewMessages;
    @FXML
    ListView<GroupDTO> listViewGroups;
    @FXML
    ListView<UserDTO> listViewUsersInGroup;
    @FXML
    TextField textFieldSendMessage;
    @FXML
    TextField textFieldCreateGroup;
    @FXML
    Button buttonSendMessage;

    public void setChatsController(Service service,Long id) {
        this.srv=service;
        this.userId=id;
        setListViewUserChats();
        setListViewMessages();
        setListViewGroups();
    }

    private void setListViewUserChats() {
        //Remove duplicates
        List<UserDTO> userChats = getUserChatsDTOList().stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(UserDTO::getId))),
                        ArrayList::new));

        modelUserChats.setAll(userChats);
        listViewUserChats.setItems(modelUserChats);
        listViewUserChats.setCellFactory(param -> new ListCell<UserDTO>() {
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
        listViewUserChats.getSelectionModel().select(0);
    }

    private List<UserDTO> getUserChatsDTOList() {
        List<User> userChatsList = new ArrayList<User>((Collection<? extends User>) srv.getUserChats(userId));
        return userChatsList
                .stream()
                .map(u -> new UserDTO(u.getID(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    private List<MessageDTO> getMessagesDTOList() {
        List<Message> messages = srv.getMessages(getUsersInConv());

        return messages
                .stream()
                .map(m -> new MessageDTO(m.getID(), m.getFrom(), m.getTo(), m.getMessage(),m.getDateTime(), m.getRepliedTo(), m.getGroup()))
                .collect(Collectors.toList());
    }

    private List<User> getUsersInConv() {
        List<User> usersInConv = new ArrayList<>();
        usersInConv.add(srv.findOne(userId));
        usersInConv.add(srv.findOne(listViewUserChats.getSelectionModel().getSelectedItem().getId()));

        return usersInConv;
    }

    public void setListViewMessages() {
        if (listViewUserChats.getSelectionModel().getSelectedItem() != null) {
            modelMessages.setAll(getMessagesDTOList());
            listViewMessages.setItems(modelMessages);
            listViewMessages.setCellFactory(param -> new ListCell<MessageDTO>() {
                @Override
                protected void updateItem(MessageDTO m, boolean empty) {
                    super.updateItem(m, empty);
                    if (empty || m == null) {
                        setText(null);
                    } else {
                        if (m.getRepliedTo() == 0) {
                            if (m.getFrom().getID() == userId) {
                                setText(m.getMessage());
                                setAlignment(Pos.CENTER_RIGHT);
                            } else {
                                setText(srv.findOne(m.getFrom().getID()).getFirstName() + ": " + m.getMessage());
                            }
                        } else {
                            if (m.getFrom().getID() == userId) {
                                setText("Replied to: " + srv.getMessage(m.getRepliedTo()).getMessage() + "\n" + m.getMessage());
                                setAlignment(Pos.CENTER_RIGHT);
                            } else {
                                setText("Replied to: " + srv.getMessage(m.getRepliedTo()).getMessage() + "\n" + srv.findOne(m.getFrom().getID()).getFirstName() + ": " + m.getMessage());
                            }
                        }
                    }
                }
            });
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    public void sendMessage() {
        String messageString = textFieldSendMessage.getText();
        ArrayList<User> usersToReceive = new ArrayList<>();

        if (listViewUserChats.getSelectionModel().getSelectedItem() != null) {
            usersToReceive.add(srv.findOne(listViewUserChats.getSelectionModel().getSelectedItem().getId()));

            if (messageString != "") {
                if (listViewMessages.getSelectionModel().getSelectedItem() == null) {
                    srv.addMessage(userId, usersToReceive, messageString, 0L, 0L);
                } else {
                    srv.addMessage(userId, usersToReceive, messageString, listViewMessages.getSelectionModel().getSelectedItem().getID(), 0L);
                }
            }

            setListViewMessages();
        }
        else {
            if (listViewGroups.getSelectionModel() != null) {
                for (User u : srv.getUsersInGroup(listViewGroups.getSelectionModel().getSelectedItem().getId()))
                    if (u.getID() != userId)
                        usersToReceive.add(u);

                if (messageString != "") {
                    if (listViewMessages.getSelectionModel().getSelectedItem() == null) {
                        srv.addMessage(userId, usersToReceive, messageString, 0L, listViewGroups.getSelectionModel().getSelectedItem().getId());
                    } else {
                        srv.addMessage(userId, usersToReceive, messageString, listViewMessages.getSelectionModel().getSelectedItem().getID(), listViewGroups.getSelectionModel().getSelectedItem().getId());
                    }
                }

                setListViewMessagesGroups();
            }
        }
    }

    //Groups

    private void setListViewGroups() {
        modelGroups.setAll(getGroupsDTOList());
        listViewGroups.setItems(modelGroups);
        listViewGroups.setCellFactory(param -> new ListCell<GroupDTO>() {
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

    private List<GroupDTO> getGroupsDTOList() {
        List<Group> groups = srv.getGroups(userId);

        return groups
                .stream()
                .map(g -> new GroupDTO(g.getID(), g.getName()))
                .collect(Collectors.toList());
    }

    @FXML
    private void createGroup() throws IOException {
        String groupNameString = textFieldCreateGroup.getText();

        if (groupNameString != "") {
            srv.createGroup(groupNameString, userId);
        }

        setListViewGroups();
    }

    public void setListViewMessagesGroups() {
        if (listViewGroups.getSelectionModel().getSelectedItem() != null) {
            modelMessages.setAll(getGroupMessagesDTOList());
            listViewMessages.setItems(modelMessages);
            listViewMessages.setCellFactory(param -> new ListCell<MessageDTO>() {
                @Override
                protected void updateItem(MessageDTO m, boolean empty) {
                    super.updateItem(m, empty);
                    if (empty || m == null || listViewGroups.getSelectionModel().getSelectedItem() == null || m.getGroup() != listViewGroups.getSelectionModel().getSelectedItem().getId()) {
                        setText(null);
                    } else {
                        if (m.getRepliedTo() == 0) {
                            if (m.getFrom().getID() == userId) {
                                setText(m.getMessage());
                                setAlignment(Pos.CENTER_RIGHT);
                            } else {
                                setText(srv.findOne(m.getFrom().getID()).getFirstName() + ": " + m.getMessage());
                            }
                        } else {
                            if (m.getFrom().getID() == userId) {
                                setText(srv.getMessage(m.getRepliedTo()).getMessage() + "\n" + m.getMessage());
                                setAlignment(Pos.CENTER_RIGHT);
                            } else {
                                setText(srv.getMessage(m.getRepliedTo()).getMessage() + "\n" + srv.findOne(m.getFrom().getID()).getFirstName() + ": " + m.getMessage());
                            }
                        }
                    }
                }
            });

            setListViewUsersInGroup();
        }
    }

    private void setListViewUsersInGroup() {
        //Remove duplicates
        List<UserDTO> userChats = getUserChatsDTOList().stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(UserDTO::getId))),
                        ArrayList::new));

        modelUsersInGroup.setAll(getUsersInGroupDTOList());
        listViewUsersInGroup.setItems(modelUsersInGroup);
        listViewUsersInGroup.setCellFactory(param -> new ListCell<UserDTO>() {
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

    private List<UserDTO> getUsersInGroupDTOList() {
        List<User> usersInGroupList = new ArrayList<User>((Collection<? extends User>) srv.getUsersInGroup(listViewGroups.getSelectionModel().getSelectedItem().getId()));
        return usersInGroupList
                .stream()
                .map(u -> new UserDTO(u.getID(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    private List<MessageDTO> getGroupMessagesDTOList() {
        List<Message> messages = srv.getGroupMessages(getUsersInGroupConv(), listViewGroups.getSelectionModel().getSelectedItem().getId());
        return messages
                .stream()
                .map(m -> new MessageDTO(m.getID(), m.getFrom(), m.getTo(), m.getMessage(),m.getDateTime(), m.getRepliedTo(), m.getGroup()))
                .collect(Collectors.toList());
    }

    private List<User> getUsersInGroupConv() {
        List<User> usersInGroupConv = new ArrayList<>();
        usersInGroupConv.add(srv.findOne(userId));
        for (User u : srv.getUsersInGroup(listViewGroups.getSelectionModel().getSelectedItem().getId()))
            usersInGroupConv.add(u);

        return usersInGroupConv;
    }

    @FXML
    private void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.userPageScene("Userpage-view.fxml",userId);
    }

    @FXML
    private void clearSelectionListViewUserChats() {
        if (listViewUserChats.getSelectionModel() != null)
            listViewUserChats.getSelectionModel().clearSelection();
        listViewUsersInGroup.setDisable(false);
        listViewUsersInGroup.setPrefSize(179, Region.USE_COMPUTED_SIZE);
    }

    @FXML
    private void clearSelectionListViewGroups() {
        if (listViewGroups.getSelectionModel() != null)
            listViewGroups.getSelectionModel().clearSelection();
        listViewUsersInGroup.setDisable(true);
        listViewUsersInGroup.setPrefSize(0, Region.USE_COMPUTED_SIZE);
    }
}
