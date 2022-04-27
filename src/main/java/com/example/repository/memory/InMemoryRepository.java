package com.example.repository.memory;


import com.example.domain.Entity;
import com.example.domain.validators.ValidationException;
import com.example.domain.validators.Validator;
import com.example.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private Map<ID, E> entities;
    private Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public int numberOfElements() {
        return entities.size();
    }

    @Override
    public E update(E entity) {
        return null;
    }

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new ValidationException("Entity must not be null!\n");
        validator.validate(entity);
        if (entities.get(entity.getID()) != null)
            return entity;
        entities.put(entity.getID(), entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        if (findOne(id) == null)
            throw new ValidationException("ID does not exist!\n");
        return entities.remove(id);
    }

    protected void deleteAll() {
        entities.clear();
    }
}
