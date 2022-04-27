package com.example.repository.database;

import com.example.domain.Message;
import com.example.domain.User;
import com.example.domain.validators.ValidationException;
import com.example.repository.Repository;
import com.example.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepo implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Message save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            //Adding to "messages" table
            String insertSQLString = "INSERT INTO messages (sender_id, message, date_time, replied_to, group_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(insertSQLString);
            ps.setInt(1, entity.getFrom().getID().intValue());
            ps.setString(2, entity.getMessage());
            ps.setString(3, entity.getDateTime().format(Constants.DATE_TIME_FORMATTER));
            ps.setInt(4, entity.getRepliedTo().intValue());
            ps.setInt(5, entity.getGroup().intValue());
            ps.executeUpdate();

            //Adding to "messages_users" table
            insertSQLString = "INSERT INTO messages_users (message_id, user_id) VALUES (?, ?)";
            for (User u : entity.getTo()) {
                ps = connection.prepareStatement(insertSQLString);

                PreparedStatement getLastMessage = connection.prepareStatement("SELECT id FROM messages ORDER BY id DESC LIMIT 1");
                ResultSet messageSet = getLastMessage.executeQuery();
                messageSet.next();
                int messageID = messageSet.getInt("id");

                ps.setInt(1, messageID);
                ps.setInt(2, u.getID().intValue());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Message> getMessagesInConv(List<User> usersInConv) {
        List<Message> messages = new ArrayList<>();

        String ids = getIDListAsString(usersInConv);
        String getMessagesInConvSQLString = String.format("SELECT m.id, m.sender_id, m.message, m.date_time, m.replied_to, m.group_id " +
                "FROM messages m INNER JOIN messages_users mu ON m.id = mu.message_id " +
                "INNER JOIN users u ON u.id = mu.user_id " +
                "WHERE m.sender_id IN %s AND u.id IN %s AND m.group_id = 0 " +
                "ORDER BY m.id ", ids, ids);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement(getMessagesInConvSQLString);
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long senderID = resultSet.getLong("sender_id");
                String messageString = resultSet.getString("message");
                LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("date_time"), Constants.DATE_TIME_FORMATTER);
                int repliedTo = resultSet.getInt("replied_to");
                Long group = resultSet.getLong("group_id");

                List<User> receivers = getAllReceivers(id);

                Message message = new Message(getOneUser(senderID), receivers, messageString, repliedTo);
                message.setID(id);
                message.setDateTime(dateTime);
                message.setGroup(group);
                messages.add(message);
            }

            return messages;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    private String getIDListAsString(List<User> usersInConv) {
        StringBuilder ids = new StringBuilder("(");
        for (int i=0; i<usersInConv.size() - 1; i++)
            ids.append(usersInConv.get(i).getID().toString()).append(", ");
        ids.append(usersInConv.get(usersInConv.size() - 1).getID().toString()).append(")");

        return ids.toString();
    }

    private List<User> getAllReceivers(Long messageID) {
        List<User> users = new ArrayList<>();

        String getAllReceiversSQLString = String.format("SELECT u.id, u.first_name, u.last_name,u.email,u.password " +
                "FROM users u INNER JOIN messages_users mu ON u.id = mu.user_id " +
                "WHERE mu.message_id = %s", messageID.toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement getReceiversStatement = connection.prepareStatement(getAllReceiversSQLString);
             ResultSet resultSet = getReceiversStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName,email,password);
                user.setID(id);
                users.add(user);
            }

            return users;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    private User getOneUser(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");

        String findOneSQLString = String.format("SELECT * FROM users WHERE id = %s", id.toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(findOneSQLString);
             ResultSet userSet = ps.executeQuery()) {

            if (!userSet.next())
                throw new ValidationException("ID does not exist!\n");

            String firstName = userSet.getString("first_name");
            String lastName = userSet.getString("last_name");
            String email = userSet.getString("email");
            String password = userSet.getString("password");

            User user = new User(firstName, lastName,email,password);
            user.setID(id);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Message> getGroupMessages(List<User> usersInConv, Long groupID) {
        List<Message> messages = new ArrayList<>();

        String ids = getIDListAsString(usersInConv);
        String getMessagesInConvSQLString = String.format("SELECT DISTINCT m.id, m.sender_id, m.message, m.date_time, m.replied_to, m.group_id " +
                "FROM messages m INNER JOIN messages_users mu ON m.id = mu.message_id " +
                "INNER JOIN users u ON u.id = mu.user_id " +
                "WHERE m.sender_id IN %s AND u.id IN %s AND m.group_id = %s " +
                "ORDER BY m.date_time", ids, ids, String.valueOf(groupID.intValue()));

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement(getMessagesInConvSQLString);
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long senderID = resultSet.getLong("sender_id");
                String messageString = resultSet.getString("message");
                LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("date_time"), Constants.DATE_TIME_FORMATTER);
                int repliedTo = resultSet.getInt("replied_to");
                Long group = resultSet.getLong("group_id");

                List<User> receivers = getAllReceivers(id);

                Message message = new Message(getOneUser(senderID), receivers, messageString, repliedTo);
                message.setID(id);
                message.setDateTime(dateTime);
                message.setGroup(group);
                messages.add(message);
            }

            return messages;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public Message findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");

        String findOneSQLString = String.format("SELECT * FROM messages WHERE id = %s", id.toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(findOneSQLString);
             ResultSet messageSet = ps.executeQuery()) {

            if (!messageSet.next())
                throw new ValidationException("ID does not exist!\n");

            Long senderID = messageSet.getLong("sender_id");
            String messageString = messageSet.getString("message");
            LocalDateTime dateTime = LocalDateTime.parse(messageSet.getString("date_time"), Constants.DATE_TIME_FORMATTER);
            int repliedTo = messageSet.getInt("replied_to");
            Long group = messageSet.getLong("group_id");

            List<User> receivers = getAllReceivers(id);

            Message message = new Message(getOneUser(senderID), receivers, messageString, repliedTo);
            message.setID(id);
            message.setGroup(group);
            message.setDateTime(dateTime);

            return message;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<User> getUserChats(long loggedUserId) {
        List<User> userChats = new ArrayList<>();

        String getAllReceiversSQLString = "SELECT DISTINCT sender_id, user_id FROM (" +
                "SELECT m.sender_id, mu.user_id, m.date_time FROM messages m INNER JOIN messages_users mu ON m.id = mu.message_id " +
                "ORDER BY m.date_time DESC) AS query " +
                "WHERE sender_id = " + loggedUserId + "OR user_id = " + loggedUserId;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement getReceiversStatement = connection.prepareStatement(getAllReceiversSQLString);
             ResultSet resultSet = getReceiversStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id;
                if (resultSet.getLong("sender_id") != loggedUserId)
                    id = resultSet.getLong("sender_id");
                else
                    id = resultSet.getLong("user_id");

                User user = getOneUser(id);
                userChats.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userChats;
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public int numberOfElements() {
        return 0;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
}
