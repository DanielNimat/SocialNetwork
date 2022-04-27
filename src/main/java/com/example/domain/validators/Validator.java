package com.example.domain.validators;

public interface Validator<T> {
    /**
     * @param entity entity must be not null
     * validates an entity
     * @throws ValidationException      if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
