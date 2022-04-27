package com.example.repository.file;



import com.example.domain.User;
import com.example.domain.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User> {
    public UserFile(String fileName, Validator validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getID() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }

    @Override
    protected User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1), attributes.get(2),attributes.get(3),attributes.get(4));
        user.setID(Long.parseLong(attributes.get(0)));

        return user;
    }
}