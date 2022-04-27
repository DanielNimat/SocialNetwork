package com.example.Service;


import com.example.domain.*;
import com.example.domain.validators.ValidationException;
import com.example.repository.Repository;
import com.example.repository.database.EventDBRepo;
import com.example.repository.database.GroupDBRepo;
import com.example.repository.database.MessageDBRepo;
import com.example.domain.FriendDTO;
import com.example.domain.Relation;
import com.example.domain.Tuple;
import com.example.domain.User;
import com.example.utils.Graph;

import javax.management.relation.RelationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service {
    //Private Variables
    private Repository<Long, User> usersRepo;
    private Repository<Tuple<Long, Long>, Relation> relationsRepo;
    private MessageDBRepo messagesRepo;
    private GroupDBRepo groupsRepo;
    private EventDBRepo eventsRepo;

    //Constructor
    public Service(Repository<Long, User> usersRepo, Repository<Tuple<Long, Long>, Relation> relationsRepo, MessageDBRepo messageDBRepo, GroupDBRepo groupsRepo, EventDBRepo eventsRepo) {
        this.usersRepo = usersRepo;
        this.relationsRepo = relationsRepo;
        this.messagesRepo = messageDBRepo;
        this.groupsRepo = groupsRepo;
        this.eventsRepo = eventsRepo;
    }

    //Get Users
    public Iterable<User> getUsers() {
        return usersRepo.findAll();
    }

    //Get one user by id
    public User getUser(long id) {
        return usersRepo.findOne(id);
    }

    //Add User
    public void addUser(String firstName, String lastName,String email,String password) {
        User newUser = new User(firstName, lastName,email,password);;
        newUser.setID(getAvailableID());
        usersRepo.save(newUser);
    }

    private Long getAvailableID() {
        List<User> usersList = new ArrayList<User>((Collection<? extends User>) getUsers());
        if (usersList.size() == 0)
            return 0L;
        else
            return usersList.get(usersList.size() - 1).getID() + 1;
    }

    //Remove User
    public void removeUser(long id) {
        List<User> friends = getFriends(id);
        usersRepo.delete(id);
        for (User f : friends)
            deleteFriend(id, f.getID());
    }



    //Get Friends Stream
    public List<FriendDTO> getFriendsStream(long userID) {
        return StreamSupport.stream(relationsRepo.findAll().spliterator(),false).
                filter(relation -> (relation.getID().getLeft().equals(userID) || relation.getID().getRight().equals(userID)) && relation.getStatus().equals("Approved") ).
                map(relation -> {
                    User friend;

                    if (relation.getID().getLeft().equals(userID))
                        friend=usersRepo.findOne(relation.getID().getRight());
                    else
                        friend=usersRepo.findOne(relation.getID().getLeft());

                    return new FriendDTO(friend.getFirstName(), friend.getLastName(),relation.getDate(),relation.getStatus(),friend.getID());
                }).collect(Collectors.toList());

    }

    //Get Friends Stream By Month
    public List<FriendDTO> getFriendsStreamByMonth(long userID,int month) {
        return StreamSupport.stream(relationsRepo.findAll().spliterator(),false).
                filter(relation -> {
                    DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd",Locale.ENGLISH);
                    LocalDate date= LocalDate.parse(relation.getDate(),formatter);
                    return (relation.getID().getLeft().equals(userID) || relation.getID().getRight().equals(userID)) && date.getMonthValue()==month && relation.getStatus().equals("Approved");



                }).
                map(relation -> {
                    User friend;
                    if (relation.getID().getLeft().equals(userID))
                        friend=usersRepo.findOne(relation.getID().getRight());
                    else
                        friend=usersRepo.findOne(relation.getID().getLeft());

                    return new FriendDTO(friend.getFirstName(), friend.getLastName(),relation.getDate().toString(),relation.getStatus(),friend.getID());
                }).collect(Collectors.toList());

    }
    //Login User
    public void loginUser(long id) {
        usersRepo.findOne(id);
    }

    //Update User
    public void updateUser(long id, String newFirstName, String newLastName,String email,String password) {
        User newUser = new User(newFirstName, newLastName,email,password);
        newUser.setID(id);
        usersRepo.update(newUser);
    }

    //Add Friend
    public void addFriend(long userID, long newFriendID) throws Exception {
        if (usersRepo.findOne(userID) == null)
            throw new ValidationException("First user does not exist!\n");
        if (usersRepo.findOne(newFriendID) == null)
            throw new ValidationException("Second user does not exist!\n");
        addRelationBetweenUsers(userID, newFriendID);
        addRelationBetweenUsers(newFriendID, userID);
    }



    private void addRelationBetweenUsers(long userID, long newFriendID) throws Exception{
        Relation r = new Relation(userID, newFriendID);
        Relation sent=new Relation(newFriendID,userID);
        r.setID(new Tuple<>(userID, newFriendID));
        sent.setID(new Tuple<>(newFriendID,userID));
        r.setID(new Tuple<>(userID, newFriendID));
        Tuple t=new Tuple(userID,newFriendID);
        Tuple t2=new Tuple(newFriendID,userID);

        if (relationsRepo.findOne(t)!= null && !relationsRepo.findOne(t).getStatus().equals("Rejected") || relationsRepo.findOne(t2)!=null && !relationsRepo.findOne(t2).getStatus().equals("Rejected"))
            throw new Exception("These users are already friends!");
        relationsRepo.save(r);

        if(relationsRepo.findOne(t2)!=null && relationsRepo.findOne(t2).getStatus().equals("Rejected"))
        {
            sent.setStatus("Sent");
            updateRelation(sent);
        }
        else
            relationsRepo.save(sent);

        sent.setStatus("Sent");
        updateRelation(sent);
    }


    //Get Friends
    public List<User> getFriends(long id) {
        User user = usersRepo.findOne(id);
        user.deleteAllFriends();
        for (Relation r : relationsRepo.findAll())
        {if (r.getID().getLeft().equals(id)&& r.getStatus().equals("Approved"))
                user.addFriend(usersRepo.findOne(r.getID().getRight()));
            if (r.getID().getRight().equals(id)&& r.getStatus().equals("Approved"))
                user.addFriend(usersRepo.findOne(r.getID().getLeft()));}
       return user.getFriendList();
    }


    public User findOne(long id){
        return usersRepo.findOne(id);
    }

    //Get FriendRequests
    public Iterable<Relation> getFriendRequests(long id) {
        Map<Tuple<Long,Long>,Relation> requests=new HashMap<>();
        for (Relation r:relationsRepo.findAll())
        {
            //if(r.getID().getLeft().equals(id) && r.getStatus().equals("Pending"))
            if(r.getID().getLeft().equals(id))

            {
                requests.put(r.getID(),r);}
        }
        return requests.values();

    }


    //Delete Friend
    public void deleteFriend(long userID, long friendID) {
        Tuple<Long, Long> relationID1 = new Tuple<>(userID, friendID);
        Tuple<Long, Long> relationID2 = new Tuple<>(friendID, userID);
        relationsRepo.delete(relationID1);
        relationsRepo.delete(relationID2);
    }

    //Number of Communities
    public int getNumberOfCommunities() {
        Map<Long, Integer> pairs = createPairs();
        int[][] graphArray = createGraphArray(pairs);
        Graph graph = new Graph(graphArray);
        return graph.getNumberOfConnectedComponents();
    }

    private int[][] createGraphArray(Map<Long, Integer> pairs) {
        int[][] graph = new int[usersRepo.numberOfElements()][usersRepo.numberOfElements()];

        for (Relation r : relationsRepo.findAll()) {
            Long id1 = r.getID().getLeft();
            Long id2 = r.getID().getRight();
            graph[pairs.get(id1)][pairs.get(id2)] = 1;
        }

        return graph;
    }

    private Map<Long, Integer> createPairs() {
        Map<Long, Integer> pairs = new HashMap<>();
        int var = 0;
        for (User u : usersRepo.findAll()) {
            pairs.put(u.getID(), var);
            var++;
        }
        return pairs;
    }

    public List<User> getTheBiggestCommunity() {
        List<User> users = new ArrayList<>();

        Map<Long, Integer> pairs = createPairs();
        int[][] graphArray = createGraphArray(pairs);
        Graph graph = new Graph(graphArray);

        List<Integer> vertices = graph.verticesFromTheComponentWithTheLongestPath();
        for (Long id : convertToOriginalIDs(vertices, pairs)) {
            users.add(usersRepo.findOne(id));
        }
        return users;
    }

    private List<Long> convertToOriginalIDs(List<Integer> vertices, Map<Long, Integer> pairs) {
        List<Long> originalIDs = new ArrayList<>();
        for (int v : vertices) {
            originalIDs.add(getOriginalID(v, pairs));
        }
        return originalIDs;
    }

    private Long getOriginalID(int vertex, Map<Long, Integer> pairs) {
        long id = -1;
        for(Map.Entry<Long, Integer> p : pairs.entrySet()) {
            if (p.getValue() == vertex)
                id = p.getKey();
        }
        return id;
    }

    public void approveRequest(long loggedUserID, long friendID) throws Exception {

        Tuple t=new Tuple(loggedUserID,friendID);

        if(relationsRepo.findOne(t)!=null&& relationsRepo.findOne(t).getStatus().equals("Pending")) {

            Relation r1= relationsRepo.findOne(t);
            Tuple t2=new Tuple(friendID,loggedUserID);
            relationsRepo.delete(t2);
            r1.setStatus("Approved");

            updateRelation(r1);
        }

    }

    public void cancelRequest(long loggedUserID, long friendID) throws Exception {

        Tuple t=new Tuple(loggedUserID,friendID);
        Tuple t2=new Tuple(friendID,loggedUserID);
        if(relationsRepo.findOne(t).getStatus().equals("Sent"))
        {relationsRepo.delete(t);
        relationsRepo.delete(t2);}

    }

    public void rejectRequest(long loggedUserID, long friendID) throws Exception {
        Tuple t=new Tuple(loggedUserID,friendID);

        if(relationsRepo.findOne(t)!=null&& relationsRepo.findOne(t).getStatus().equals("Pending")) {
            Relation r=relationsRepo.findOne(t);
            Tuple t2=new Tuple(friendID,loggedUserID);
            relationsRepo.delete(t2);
            r.setStatus("Rejected");
            updateRelation(r);
        }
    }

    public void sendRequest(long loggedUserID, long newFriendID) throws Exception {
        if (usersRepo.findOne(loggedUserID) == null)
            throw new ValidationException("First user does not exist!\n");
        if (usersRepo.findOne(newFriendID) == null)
            throw new ValidationException("Second user does not exist!\n");
        if (loggedUserID==newFriendID)
            throw new ValidationException("Can not send a friend request to yourself\n");

        Tuple t=new Tuple(loggedUserID,newFriendID);
        Tuple t2=new Tuple(newFriendID,loggedUserID);

        if((relationsRepo.findOne(t)!=null && !relationsRepo.findOne(t).getStatus().equals("Rejected"))||(relationsRepo.findOne(t2)!=null ))
            throw new RelationException("Ati trimis deja o cerere catre acest utilizator");
        addRelationBetweenUsers(newFriendID, loggedUserID);
    }


    public void updateRelation(Relation r){
        relationsRepo.update(r);
    }

    public String getRelationStartDate(long loggedUserID, long friendID) throws RelationException {
        for (Relation r:relationsRepo.findAll())
        {
            if ((r.getID().getLeft().equals(loggedUserID) && r.getID().getRight().equals(friendID) && r.getStatus().equals("Approved")) || (r.getID().getLeft().equals(friendID) && r.getID().getRight().equals(loggedUserID) && r.getStatus().equals("Approved")))
                return r.getDate();
        }

        throw new RelationException("This relation does not exist!");
    }

    //Messages

    //Send message
    public void addMessage(long loggedUserID, List<User> usersToReceive, String messageString, Long repliedTo, Long group) {
        Message message = new Message(usersRepo.findOne(loggedUserID), usersToReceive, messageString, repliedTo);
        message.setGroup(group);
        message.setDateTime(LocalDateTime.now());
        messagesRepo.save(message);

    }

    //Get messages
    public List<Message> getMessages(List<User> usersInConv) {
        return messagesRepo.getMessagesInConv(usersInConv);
    }

    //Get one message
    public Message getMessage(long id) {
        return messagesRepo.findOne(id);
    }

    //Get the users that have chats with the logged user
    public List<User> getUserChats(long id) {
        return messagesRepo.getUserChats(id);
    }

    //Groups

    //Create group
    public void createGroup(String name, long loggedUserID) {
        Group newGroup = groupsRepo.save(new Group(name));
        groupsRepo.addUserToGroup(newGroup.getID(), loggedUserID);
    }

    public List<Group> getGroups(long loggedUserID) {
        return groupsRepo.getUserGroups(loggedUserID);
    }

    public List<User> getUsersInGroup(long groupID) {
        return groupsRepo.getUsersInGroup(groupID);
    }

    public List<Message> getGroupMessages(List<User> usersInConv, long groupID) {
        return messagesRepo.getGroupMessages(usersInConv, groupID);
    }

    public void addUserToGroup(long groupID, long userID) {
        groupsRepo.addUserToGroup(groupID, userID);
    }

    //Events

    //Create event
    public void createEvent(String name, String description, String location, String date, long creatorID) {
        Event event = new Event(name, description, location, date, creatorID);
        eventsRepo.save(event);
    }

    //Get all events
    public Iterable<Event> getEvents() {
        return eventsRepo.findAll();
    }

    //Join event
    public void joinEvent(long userID, long eventID) {
        eventsRepo.joinEvent(userID, eventID);
    }

    //Get the participants of an event
    public List<User> getEventParticipants(long eventID) {
        return eventsRepo.getEventParticipants(eventID);
    }

    //Get logged user's events
    public List<Event> getMyEvents(long userID) {
        return eventsRepo.getMyEvents(userID);
    }
}
