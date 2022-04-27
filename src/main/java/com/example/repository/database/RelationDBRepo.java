package com.example.repository.database;


import com.example.domain.Relation;
import com.example.domain.Tuple;
import com.example.domain.validators.Validator;
import com.example.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RelationDBRepo implements Repository<Tuple<Long, Long>, Relation> {
    private final String url;
    private final String username;
    private final String password;
    private Validator<Relation> validator;

    public RelationDBRepo(String url, String username, String password, Validator<Relation> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Relation findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");

        String findOneSQLString = String.format( "SELECT * FROM relations WHERE id1 = %s AND id2 = %s", id.getLeft().toString(), id.getRight().toString());
        Relation relation=null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(findOneSQLString);
             ResultSet relationSet = ps.executeQuery()){

            while(relationSet.next())
            { Long id1 = relationSet.getLong("id1");
            Long id2 = relationSet.getLong("id2");
            String date = relationSet.getString("date");
            String status=relationSet.getString(("status"));
             relation = new Relation(id1, id2);
            relation.setID(id);
            relation.setStatus(status);
            relation.setDate(date);}
            return relation;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Relation> findAll() {
        Set<Relation> relations = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectAllStatement = connection.prepareStatement("SELECT * FROM relations");
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");
                String status=resultSet.getString("status");
                Relation relation = new Relation(id1, id2);
                relation.setID(new Tuple<>(id1, id2));
                relation.setStatus(status);
                relation.setDate(date);
                relations.add(relation);
            }
            return relations;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relations;
    }

    @Override
    public Relation save(Relation entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");
        validator.validate(entity);

        String insertSQLString = "INSERT INTO relations (id1, id2, date,status) VALUES (?, ?, ?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(insertSQLString)) {

            ps.setLong(1, entity.getID().getLeft());
            ps.setLong(2, entity.getID().getRight());
            ps.setString(3, entity.getDate().toString());
            ps.setString(4,entity.getStatus().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Relation delete(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");

        Relation relation = findOne(id);

        String findOneSQLString = String.format( "DELETE FROM relations WHERE id1 = %s AND id2 = %s", id.getLeft().toString(), id.getRight().toString());
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
             PreparedStatement selectAllStatement = connection.prepareStatement("SELECT COUNT(*) FROM relations");
             ResultSet resultSet = selectAllStatement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }



    /**
     * modifies a Relation
     */
    @Override
    public Relation update(Relation entity) {
        String sql = "update relations set status = ? where id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql))
        {

            ps.setString(1, entity.getStatus());
            ps.setLong(2,entity.getID().getLeft());
            ps.setLong(3,entity.getID().getRight());

            ps.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }
}