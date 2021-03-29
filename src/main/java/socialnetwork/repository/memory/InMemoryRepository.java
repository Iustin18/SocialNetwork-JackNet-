package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private final Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities= new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new RepositoryException("Id must be not null");
        E entity = entities.get(id);
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public ArrayList<E> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity==null)
            throw new RepositoryException("entity must be not null");
        validator.validate(entity);
        Optional<E> aux = findOne(entity.getId());
        if(aux.isPresent())
            throw new RepositoryException("Object already exist!");
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(),entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        E entity= entities.get(id);
        if(entity==null) {
            throw new RepositoryException("id does not exist");
        }
        return Optional.ofNullable(entities.remove(entity.getId()));
    }

    @Override
    public Optional<E> update(E entity) {

        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);
        return Optional.ofNullable(entities.computeIfPresent(entity.getId(), (k, v) -> entity));

    }

}
