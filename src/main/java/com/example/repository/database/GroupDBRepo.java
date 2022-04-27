package com.example.repository.database;

import com.example.domain.Group;
import com.example.domain.User;
import com.example.domain.validators.ValidationException;
import com.example.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDBRepo implements Repository<Long, Group> {
    private final String url;
    private final String username;
    private final String password;

    public GroupDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Group save(Group entity) {
        Group newGroup = null;

        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertSQLString = "INSERT INTO groups (name) VALUES (?)";
            PreparedStatement ps = connection.prepareStatement(insertSQLString);
            ps.setString(1, entity.getName());
            ps.executeUpdate();

            String findOneSQLString = "SELECT max(id) AS id FROM groups";
            ps = connection.prepareStatement(findOneSQLString);
            ResultSet groupSet = ps.executeQuery();

            if (!groupSet.next())
                throw new ValidationException("ID does not exist!\n");

            Long groupID = groupSet.getLong("id");
            newGroup = new Group(entity.getName());
            newGroup.setID(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newGroup;
    }

    public void addUserToGroup(Long groupID, Long userID) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertSQLString = "INSERT INTO groups_users (group_id, user_id) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(insertSQLString);
            ps.setInt(1, groupID.intValue());
            ps.setInt(2, userID.intValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Group> getUserGroups(Long userID) {
        List<Group> groups = new ArrayList<>();

        String getGroupsSQLString = String.format("SELECT g.id, g.name FROM groups AS g INNER JOIN groups_users as gu ON g.id = gu.group_id " +
                "WHERE gu.user_id = %s ", userID);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement(getGroupsSQLString);
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Group group = new Group(name);
                group.setID(id);
                groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public List<User> getUsersInGroup(Long groupdID) {
        List<User> users = new ArrayList<>();

        String getUsersInGroup = String.format("SELECT u.id, u.first_name, u.last_name, u.email, u.password FROM groups AS g INNER JOIN groups_users as gu ON g.id = gu.group_id " +
                "INNER JOIN users as u ON u.id = gu.user_id " +
                "WHERE g.id = %s ", groupdID);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement(getUsersInGroup);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public Group findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Group> findAll() {
        return null;
    }

    @Override
    public Group delete(Long aLong) {
        return null;
    }

    @Override
    public int numberOfElements() {
        return 0;
    }

    @Override
    public Group update(Group entity) {
        return null;
    }
}
