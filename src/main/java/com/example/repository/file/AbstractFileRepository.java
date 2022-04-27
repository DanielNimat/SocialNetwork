package com.example.repository.file;


import com.example.domain.Entity;
import com.example.domain.validators.Validator;
import com.example.repository.memory.InMemoryRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String fileName;

    public AbstractFileRepository(String fileName, Validator validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    protected abstract String createEntityAsString(E entity);

    protected abstract E extractEntity(List<String> attributes);

    protected void writeTofile(E entity) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(createEntityAsString(entity));
            bufferedWriter.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
//        try (BufferedReader buff = new BufferedReader(new FileReader(this.fileName))) {
//            String line;
//            while ((line = buff.readLine()) != null) {
//                List<String> args = Arrays.asList(line.split(";"));
//                E entity = extractEntity(args);
//                super.save(entity);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Path path = Paths.get(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            lines.forEach(line -> {
                E entity = extractEntity(Arrays.asList(line.split(";")));
                super.save(entity);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param entity
     * @return E
     */
    @Override
    public E save(E entity) {
        E e = super.save(entity);
        if (e == null) {
            writeTofile(entity);
        }
        return e;
    }

    /**
     * @param id
     * @return deleted entity
     * @throws IOException if the repo file cannot be accessed
     */
    @Override
    public E delete(ID id) {
        E deletedEntity = super.delete(id);
        Iterable<E> entities = super.findAll();
        try {
            new FileWriter(fileName, false).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (E entity : entities)
            writeTofile(entity);

        return deletedEntity;
    }

    @Override
    public Iterable<E> findAll() {
        super.deleteAll();
        loadData();
        return super.findAll();
    }

    @Override
    public E findOne(ID id) {
        super.deleteAll();
        loadData();
        if (id == null)
            throw new IllegalArgumentException("ID must not be null");
        return super.findOne(id);
    }
}

