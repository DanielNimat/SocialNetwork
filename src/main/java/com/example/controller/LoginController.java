package com.example.controller;

import com.example.Service.Service;
import com.example.domain.*;
import com.example.social_network.TestSocial_Network;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController {
    Service srv;
    Page userPage=new Page();


    @FXML
    private TextField emailText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Label wrongid;


    public void setLoginService(Service service) {
        this.srv=service;

    }


    public void userRegister() throws IOException{

        TestSocial_Network t=new TestSocial_Network();


        t.registerScene("Registerpage-view.fxml");




        }

    public String encryptString(String password) throws NoSuchAlgorithmException {
        MessageDigest md= MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(passwordText.getText().toString().getBytes());
        BigInteger bigInt=new BigInteger(1,messageDigest);
        return bigInt.toString(16);
    }


    public void userLogin() throws IOException, NoSuchAlgorithmException {
        TestSocial_Network t=new TestSocial_Network();

//        if(emailText.getText().toString().isEmpty())
//            wrongid.setText("E-mail must not be null!");
//        if(passwordText.getText().toString().isEmpty())
//            wrongid.setText("Password must not be null!");
        if(emailText.getText().toString().isEmpty() ||  passwordText.getText().toString().isEmpty())
            wrongid.setText("Invalid credentials");
        else{

        for (User u :srv.getUsers()) {
            if(u.getEmail().toString().equals(emailText.getText().toString()) && u.getPassword().toString().equals(encryptString(passwordText.getText().toString())))
            {
                wrongid.setText("");
                userPage.setFirstName(srv.findOne(u.getID()).getFirstName());
                userPage.setLastName(srv.findOne(u.getID()).getLastName());
                userPage.setFriendRequests(getFriendDTOList(u.getID()));
                userPage.setFriendList(srv.getFriends(u.getID()));
                userPage.setMessageList(getMessagesDTOList(u.getID()));
                t.userPageScene("Userpage-view.fxml", u.getID());
            }
        }}

        wrongid.setText("Invalid credentials");



    }


    private List<FriendDTO> getFriendDTOList(long userId) {
        Iterable<Relation> relations=srv.getFriendRequests(userId);

        List<Relation> requests= StreamSupport.stream(relations.spliterator(),false)
                .collect(Collectors.toList());

        return requests.stream().map(r->new FriendDTO(srv.findOne(r.getID().getRight()).getFirstName(),srv.findOne(r.getID().getRight()).getLastName(),r.getDate(),r.getStatus(),r.getID().getRight()))
                .collect(Collectors.toList());
    }

    private List<User> getUsersInConv(long userId) {
        List<User> usersInConv = new ArrayList<>();
        usersInConv.add(srv.findOne(userId));
        for (UserDTO u : getUserChatsDTOList(userId)) {
            usersInConv.add(srv.findOne(u.getId()));

        }

        return usersInConv;
    }

    private List<UserDTO> getUserChatsDTOList(long userId) {
        List<User> userChatsList = new ArrayList<User>((Collection<? extends User>) srv.getUserChats(userId));
        return userChatsList
                .stream()
                .map(u -> new UserDTO(u.getID(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    private List<MessageDTO> getMessagesDTOList(long userId) {
        List<Message> messages = srv.getMessages(getUsersInConv(userId));

        return messages
                .stream()
                .filter(p -> {
                    if (p.getFrom().getID().equals(userId))
                        return false;
                    return true;
                })
                .map(m -> new MessageDTO(m.getID(), m.getFrom(), m.getTo(), m.getMessage(),m.getDateTime(), m.getRepliedTo(),m.getGroup()))
                .collect(Collectors.toList());
    }

}
