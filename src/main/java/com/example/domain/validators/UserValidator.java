package com.example.domain.validators;


import com.example.domain.User;

public class UserValidator implements Validator<User>{
    @Override
    public void validate(User entity) throws ValidationException {
        String errorMsg = "";
        if (entity.getFirstName().matches(".*\\d.*"))
            errorMsg += "First name cannot contain numbers!\n";
        if (entity.getLastName().matches(".*\\d.*"))
            errorMsg += "Last name cannot contain numbers!\n";
        if (!errorMsg.isEmpty())
            throw new ValidationException(errorMsg);
    }
}