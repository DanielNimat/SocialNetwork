package com.example.controller;

import com.example.Service.Service;
import com.example.domain.*;
import com.example.social_network.TestSocial_Network;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.management.relation.RelationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;


import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class RapoarteController {

    Service srv;
    Long userId;
    private int from1=0;
    private int to1=0;
    private int from2=0;
    private int to2=0;
    private int itemPerPage=5;
    ObservableList<FriendDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<MessageDTO> modelMessages = FXCollections.observableArrayList();

    @FXML
    TableColumn<FriendDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnLastName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFriendsSince;
    @FXML
    TableView<FriendDTO> tableViewFriends;

    @FXML
    TableColumn<MessageDTO, String> tableColumnFirstNameMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnLastNameMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnDate;
    @FXML
    TableView<MessageDTO> tableViewMessages;

    @FXML
    Pagination pagination1;

    @FXML
    Pagination pagination2;

    @FXML
    DatePicker datePicker;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("lastName"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("date"));
        //tableViewFriends.setItems(modelFriends);
        datePicker.valueProperty().addListener(o -> handleFilter());


        tableColumnFirstNameMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("firstName"));
        tableColumnLastNameMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("lastName"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("dateTime"));
        //tableViewMessages.setItems(modelMessages);


    }




    private List<UserDTO> getUserChatsDTOList() {
        List<User> userChatsList = new ArrayList<User>((Collection<? extends User>) srv.getUserChats(userId));
        return userChatsList
                .stream()
                .map(u -> new UserDTO(u.getID(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }


    private List<MessageDTO> getMessagesDTOList() {
        List<Message> messages =new ArrayList<>();
        List<User> users=getUsersInConv();
//        for (int i=0;i<=users.size()/2-1;i++){
//            List<User> userInConv=new ArrayList<>();
//            userInConv.add(srv.findOne(userId));
//            userInConv.add(users.get(i));
//            for(Message m: srv.getMessages(userInConv))
//                messages.add(m);
//        }


        for(User u:getUsersInConv()){
            List<User> userInConv=new ArrayList<>();
            userInConv.add(srv.findOne(userId));
            userInConv.add(u);
            for(Message m: srv.getMessages(userInConv))
                messages.add(m);
        }

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


    private List<User> getUsersInConv() {
        List<User> usersInConv = new ArrayList<>();
        List<UserDTO> users=getUserChatsDTOList();
        for(int i=0;i<=users.size()/2-1;i++)
            usersInConv.add(srv.findOne(users.get(i).getId()));
//        for (UserDTO u : getUserChatsDTOList()) {
//                usersInConv.add(srv.findOne(u.getId()));
//
//        }

        return usersInConv;


        //usersInConv.add(srv.findOne(userId));
        //usersInConv.add(srv.findOne(listViewUserChats.getSelectionModel().getSelectedItem().getId()));

        //return usersInConv;
    }

    private Node createPage1(int pageIndex ){
        from1=pageIndex*itemPerPage;
        to1=Math.min(from1+itemPerPage,modelFriends.size());
        try{
            tableViewFriends.setItems(FXCollections.observableArrayList(modelFriends.subList(from1,to1)));}
        catch (Exception ex){}
        return tableViewFriends;
    }

    private Node createPage2(int pageIndex ){
        from2=pageIndex*itemPerPage;
        to2=Math.min(from2+itemPerPage,modelMessages.size());
        try{

            tableViewMessages.setItems(FXCollections.observableArrayList(modelMessages.subList(from2,to2)));}
        catch (Exception ex){}
        return tableViewMessages;
    }


    public void setRapoarteController(Service service, Long id) {
        this.srv = service;
        this.userId = id;
        modelFriends.setAll(getFriendsDTOList());
        modelMessages.setAll(getMessagesDTOList());
        int count1=0;
        count1=getFriendsDTOList().size();
        int pageCount1=(count1/itemPerPage) +1;
        pagination1.setPageCount(pageCount1);
        pagination1.setPageFactory(this::createPage1);
        int count2=0;
        count2=getMessagesDTOList().size();
        int pageCount2=(count2/itemPerPage) +1;
        pagination2.setPageCount(pageCount2);
        pagination2.setPageFactory(this::createPage2);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);


        if (datePicker.getValue() != null) {
            Predicate<FriendDTO> p = n -> LocalDate.parse(n.getDate(), formatter).isAfter(datePicker.getValue());
            Predicate<MessageDTO> pm = n -> n.getDateTime().toLocalDate().isAfter(datePicker.getValue());
            modelFriends.setAll(getFriendsDTOList()
                    .stream()
                    .filter(p)
                    .collect(Collectors.toList()));


            modelMessages.setAll(getMessagesDTOList()
                    .stream()
                    .filter(pm)
                    .collect(Collectors.toList()));
            int count1=0;
            count1=modelFriends.size();
            int pageCount1=(count1/itemPerPage) +1;
            pagination1.setPageCount(pageCount1);
            pagination1.setPageFactory(this::createPage1);
            int count2=0;
            count2=modelMessages.size();
            int pageCount2=(count2/itemPerPage) +1;
            pagination2.setPageCount(pageCount2);
            pagination2.setPageFactory(this::createPage2);
    }}





    @FXML
    private void back() throws IOException {
        TestSocial_Network t=new TestSocial_Network();
        t.userPageScene("Userpage-view.fxml",userId);
    }

    public void createPDF() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page=new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_BOLD_ITALIC, 14);
        contentStream.setLeading(14.5f);



        contentStream.newLineAtOffset(20, 600);


        contentStream.showText("FRIENDS:");
        contentStream.newLine();
        for(FriendDTO f:modelFriends){
            String text= String.valueOf(f);
            contentStream.showText(text);
            contentStream.newLine();
        }

        contentStream.newLine();
        contentStream.showText("Messages");
        contentStream.newLine();
        for (MessageDTO m:modelMessages){
            String text= String.valueOf(m);
            contentStream.showText(text);
            contentStream.newLine();

        }


        //Ending the content stream
        contentStream.endText();

        //Closing the content stream
        contentStream.close();


        document.save("C:/Reports/RapoareCalendaristice.pdf");


        document.close();

    }

}
