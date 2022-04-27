package com.example.controller;

import com.example.Service.Service;
import com.example.social_network.TestSocial_Network;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterController {
    Service srv;

    @FXML
    private TextField emailText;

    @FXML
    private TextField passwordText;
    @FXML
    private TextField firstnameText;
    @FXML
    private TextField lastnameText;
    @FXML
    private Label labelInfo;


    public void registerUser() throws NoSuchAlgorithmException {
        TestSocial_Network t=new TestSocial_Network();
        MessageDigest md= MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(passwordText.getText().toString().getBytes());
        BigInteger bigInt=new BigInteger(1,messageDigest);
        if (emailText.getText().equals("") || passwordText.getText().equals("") || firstnameText.getText().equals("") || lastnameText.getText().equals("")) {
            labelInfo.setText("Invalid credentials!");
        }
        else {
            srv.addUser(firstnameText.getText().toString(), lastnameText.getText().toString(), emailText.getText().toString(), bigInt.toString(16));
            labelInfo.setText("Account created!");
        }
    }


    @FXML
    private void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.loginScene("Login-view.fxml");
    }


    public void setRegisterService(Service service) {
        this.srv=service;


    }
}
