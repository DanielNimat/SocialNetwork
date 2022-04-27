package com.example.repository.database;

import com.example.domain.Event;
import com.example.domain.User;
import com.example.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDBRepo implements Repository<Long, Event> {
    private final String url;
    private final String username;
    private final String password;

    public EventDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Event save(Event entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");
        //validator.validate(entity);

        String insertSQLString = "INSERT INTO events (name, description, location, date, organizer) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(insertSQLString)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setString(3, entity.getLocation());
            ps.setString(4, entity.getDate());
            ps.setInt(5, entity.getOrganizerID().intValue());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement("SELECT * FROM events");
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String date = resultSet.getString("date");
                Long organizerID = resultSet.getLong("organizer");

                Event event = new Event(name, description, location, date, organizerID);
                event.setID(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public void joinEvent(Long userID, Long eventID) {
        String insertSQLString = "INSERT INTO events_users (event_id, user_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(insertSQLString)) {

            ps.setLong(1, eventID);
            ps.setLong(2, userID);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getEventParticipants(Long eventID) {
        List<User> users = new ArrayList<>();

        String getParticipantsSQLString = String.format("SELECT u.id, u.first_name, u.last_name, u.email, u.password FROM users AS u INNER JOIN events_users AS eu ON u.id = eu.user_id WHERE eu.event_id = %s", eventID.toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement(getParticipantsSQLString);
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, email, password);
                user.setID(id);
                users.add(user);
            }

            return users;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<Event> getMyEvents(Long userID) {
        List<Event> events = new ArrayList<>();

        String getMyEventsSQLString = String.format("SELECT e.id, e.name, e.description, e.location, e.date, e.organizer FROM events AS e WHERE e.organizer = %s UNION " +
                "SELECT e.id, e.name, e.description, e.location, e.date, e.organizer FROM events AS e INNER JOIN events_users AS eu ON e.id = eu.event_id WHERE eu.user_id = %s " +
                "ORDER BY date", userID.toString(), userID.toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement(getMyEventsSQLString);
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String date = resultSet.getString("date");
                Long organizerID = resultSet.getLong("organizer");

                Event event = new Event(name, description, location, date, organizerID);
                event.setID(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event findOne(Long aLong) {
        return null;
    }

    @Override
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public int numberOfElements() {
        return 0;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }
}
