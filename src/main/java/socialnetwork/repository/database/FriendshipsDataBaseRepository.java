package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class FriendshipsDataBaseRepository implements PagingRepository<Long, Friendship> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipsDataBaseRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    /**
     *
     * @param entity -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     *
     */
    @Override
    public Optional<Friendship> findOne(Long entity) {
        String quarry="SELECT * FROM friendships WHERE id="+entity+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(quarry);
             ResultSet resultSet = statement.executeQuery()) {

            long id1;
            long id2;
            long id;
            String date;
            Optional<Friendship> f = Optional.empty();
            while(resultSet.next()) {
                id = Long.parseLong(resultSet.getString("id"));
                id1 = Long.parseLong(resultSet.getString("id1"));
                id2 = Long.parseLong(resultSet.getString("id2"));
                date = resultSet.getString("date");
                Friendship f2 = new Friendship(id,new Tuple<>(id1, id2));
                f2.setDate(date);
                f = Optional.of(f2);
            }
            return f;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     *
     * @return all entities
     */
    @Override
    public ArrayList<Friendship> findAll() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");

                Tuple<Long,Long> t = new Tuple<>(id1,id2);
                Friendship friendship = new Friendship(id,t);
                friendship.setDate(date);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    /**
     *
     * @param entity
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid   *
     */
    @Override
    public Optional<Friendship> save(Friendship entity) {
        validator.validate(entity);

        for(Friendship f: findAll())
            if(f.getPair().getLeft().equals(entity.getPair().getRight()) && f.getPair().getRight().equals(entity.getPair().getLeft()))
                throw new RepositoryException("Friendship already exist");

        String query = "INSERT INTO friendships VALUES('"+entity.getId()+"','"
                +entity.getPair().getRight()+"','"
                +entity.getPair().getLeft()+"','"+entity.getDate()+"')";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.execute();
            return Optional.of(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     *
     */
    @Override
    public Optional<Friendship> delete(Long id) {
        Optional<Friendship> aux = findOne(id);
        if(aux.isEmpty()){
            throw new RepositoryException("Friendship does not exist");
        }
        String query = "DELETE FROM friendships WHERE id="+id+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.execute();
            return aux;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }


    @Override
    public Page<Friendship> findAll(Pageable pageable,Long id) {
        Paginator<Friendship> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }

}
