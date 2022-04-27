package com.example.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class Entity<ID> implements Serializable {

    // private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;
    public ID getID() {
        return id;
    }
    public void setID(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return getID().equals(entity.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}