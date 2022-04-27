package com.example.controller;

import com.example.Service.Service;
import com.example.domain.*;
import com.example.social_network.TestSocial_Network;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.management.relation.RelationException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RapoartePrietenController {

    Service srv;
    Long userId;

    ObservableList<FriendDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<MessageDTO> modelMessages = FXCollections.observableArrayList();

    private int from=0;
    private int to=0;
    private int itemPerPage=5;

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
    ComboBox<FriendDTO> comboBox;
    @FXML
    DatePicker datePicker;

    @FXML
    Pagination pagination;

    public void initialize() {

        datePicker.valueProperty().addListener(o->handleFilter());
        comboBox.valueProperty().addListener(o->handleFilter());

        tableColumnFirstNameMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("firstName"));
        tableColumnLastNameMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("lastName"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("dateTime"));
        //tableViewMessages.setItems(modelMessages);
        initComboBox();



    }


    private Node createPage(int pageIndex ){
        from=pageIndex*itemPerPage;
        to=Math.min(from+itemPerPage,modelMessages.size());
        try{
            tableViewMessages.setItems(FXCollections.observableArrayList(modelMessages.subList(from,to)));}
        catch (Exception ex){}
        return tableViewMessages;
    }

    public void initComboBox(){

        comboBox.setItems(modelFriends);
        comboBox.setCellFactory(new Callback<ListView<FriendDTO>, ListCell<FriendDTO>>() {
            @Override
            public ListCell<FriendDTO> call(ListView<FriendDTO> param) {
                return new ListCell<FriendDTO>(){
                    @Override
                    protected void updateItem(FriendDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getFirstName() + " " + item.getLastName());
                        }
                    }
                };
            } });

        comboBox.setConverter(new StringConverter<FriendDTO>() {
            @Override
            public String toString(FriendDTO s) {
                if (s == null) {
                    return null;
                } else {
                    return s.getFirstName() + " " + s.getLastName();
                }
            }
            @Override
            public FriendDTO fromString(String studentString) {
                return null; // No conversion fromString needed.
            }
        });


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



    public void setRapoarteController(Service service,Long id) {
        this.srv=service;
        this.userId=id;
        modelFriends.setAll(getFriendsDTOList());
        modelMessages.setAll(getMessagesDTOList());
        int count=0;
        count=modelMessages.size();
        int pageCount=(count/itemPerPage) +1;
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
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
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

        if(comboBox.getSelectionModel().getSelectedItem()!=null && datePicker.getValue() != null)
        {
            FriendDTO f = comboBox.getSelectionModel().getSelectedItem();
            Predicate<MessageDTO> p1 = u -> u.getFrom().getID().equals(f.getId());
            Predicate<MessageDTO> p = n -> n.getDateTime().toLocalDate().isAfter(datePicker.getValue());
            modelMessages.setAll(getMessagesDTOList()
                        .stream()
                        .filter(p.and(p1))
                        .collect(Collectors.toList()));
            int count=0;
            count=modelMessages.size();
            int pageCount=(count/itemPerPage) +1;
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);

        }




    }

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


        document.save("C:/Reports/RapoartePrieten.pdf");


        document.close();

    }

}
