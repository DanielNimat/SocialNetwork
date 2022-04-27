package com.example.repository.file;


import com.example.domain.Relation;
import com.example.domain.Tuple;
import com.example.domain.validators.Validator;

import java.util.List;

public class RelationFile extends AbstractFileRepository<Tuple<Long, Long>, Relation> {
    public RelationFile(String fileName, Validator validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(Relation entity) {
        Tuple<Long, Long> t = entity.getID();
        return t.getLeft() + ";" + t.getRight() + ";" + entity.getDate();
    }

    @Override
    protected Relation extractEntity(List<String> attributes) {
        Relation relation = new Relation(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)));
        relation.setDate(attributes.get(2));
        relation.setID(new Tuple<>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))));

        return relation;
    }
}
