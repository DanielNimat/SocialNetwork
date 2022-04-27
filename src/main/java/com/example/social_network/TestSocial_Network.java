package com.example.social_network;

import com.example.Service.Service;
import com.example.controller.*;
import com.example.domain.Relation;
import com.example.domain.Tuple;
import com.example.domain.User;
import com.example.domain.validators.RelationValidator;
import com.example.domain.validators.UserValidator;
import com.example.repository.Repository;
import com.example.repository.database.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TestSocial_Network extends Application {

    Repository<Long, User> userDBRepo;
    Repository<Tuple<Long, Long>, Relation> relationsRepo;
    GroupDBRepo groupsRepo;
    EventDBRepo eventsRepo;
    Service srv;


    private static Stage stg;
    @Override
    public void start(Stage primaryStage) throws IOException {

         userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        stg=primaryStage;

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource("Login-view.fxml"));
        AnchorPane root=loader.load();
        LoginController controller=loader.getController();
        controller.setLoginService(srv);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Login");
        stg.show();

    }

    public void userPageScene(String fxml,Long id) throws IOException {

        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        UserPageController controller=loader.getController();
        controller.setUserService(srv,id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Home");
        stg.show();


    }


    public void friendRequestsScene(String fxml,Long id) throws IOException {

        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        FriendRequestsController controller=loader.getController();
        controller.setUserService(srv,id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Notifications");
        stg.show();
    }


    public void loginScene(String fxml) throws IOException {

        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        LoginController controller=loader.getController();
        controller.setLoginService(srv);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Login");
        stg.show();
    }

    public void searchUsersScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        SearchUsersController controller=loader.getController();
        controller.setSearchUsersService(srv, id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Users");
        stg.show();
    }


    public void registerScene(String fxml)throws IOException{
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();

        RegisterController controller = loader.getController();
        controller.setRegisterService(srv);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Register");
        stg.show();
    }

    public void friendsScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        FriendsController controller = loader.getController();
        controller.setFriendsController(srv, id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Friends");
        stg.show();
    }

    public void rapoarteScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        RapoarteController controller = loader.getController();
        controller.setRapoarteController(srv, id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("User Activity");
        stg.show();
    }


    public void rapoartePrietenScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        RapoartePrietenController controller = loader.getController();
        controller.setRapoarteController(srv, id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Friends Activity");
        stg.show();
    }

    public void chatsScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        ChatsController controller = loader.getController();
        controller.setChatsController(srv, id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Chats");
        stg.show();
    }

    public void eventsScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        EventsController controller = loader.getController();
        controller.setEventsController(srv, id);
        stg.setScene(new Scene(root, 700, 500));
        stg.setTitle("Events");
        stg.show();
    }

    public void createEventScene(String fxml, Long id) throws IOException {
        userDBRepo = new UserDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator());
        relationsRepo = new RelationDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new RelationValidator());
        MessageDBRepo messagesRepo = new MessageDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        groupsRepo = new GroupDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        eventsRepo = new EventDBRepo("jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres");
        Service srv = new Service(userDBRepo, relationsRepo, messagesRepo, groupsRepo, eventsRepo);

        FXMLLoader loader = new FXMLLoader(TestSocial_Network.class.getResource(fxml));
        AnchorPane root=loader.load();
        CreateEventController controller = loader.getController();
        controller.setCreateEventController(srv, id);
        stg.setScene(new Scene(root, 400, 450));
        stg.setTitle("Create Event");
        stg.show();
    }

    public static void main(String[] args) {
        launch();
    }
}