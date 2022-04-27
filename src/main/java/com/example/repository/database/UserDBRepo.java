package com.example.repository.database;



import com.example.domain.User;
import com.example.domain.validators.ValidationException;
import com.example.domain.validators.Validator;
import com.example.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDBRepo implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private Validator<User> validator;

    public UserDBRepo(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(Long id) {
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

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = selectAllStatement.executeQuery()) {

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

    @Override
    public User save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");
        validator.validate(entity);

        String insertSQLString = "INSERT INTO users (first_name, last_name, email,password) VALUES (?, ?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(insertSQLString)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getPassword());


            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");

        User user = findOne(id);

        String findOneSQLString = String.format( "DELETE FROM users WHERE id = %s", id.toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(findOneSQLString)) {

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int numberOfElements() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public User update(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");
        validator.validate(entity);
        findOne(entity.getID());

        String modifyUserSQLString = String.format("UPDATE users SET first_name = '%s', last_name = '%s' WHERE id = %s",
                entity.getFirstName(), entity.getLastName(), entity.getID().toString());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(modifyUserSQLString)) {

            ps.executeUpdate();

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entity;
    }
}
