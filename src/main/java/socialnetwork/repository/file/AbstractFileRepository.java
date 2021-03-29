/*
package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    */
/**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     *//*

    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    @Override
    public Optional<E> save(E entity){
        Optional<E> e=super.save(entity);
        if (!e.isPresent())
        {
            writeToFile(entity);
        }
        return e;

    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> e = super.delete(id);
        if(e.isPresent()){
            reWriteToFIle();
        }
        return e;
    }


    protected void reWriteToFIle() {
        try {
            new FileWriter(fileName, false).close();
            ArrayList<E> entities=super.findAll();
            for(E e : entities){
                writeToFile(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

*/
