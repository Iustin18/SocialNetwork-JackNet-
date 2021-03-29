package socialnetwork.repository.database;

import socialnetwork.domain.Message;
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
import java.util.Comparator;
import java.util.Optional;

public class MessagesDataBaseRepository implements PagingRepository<Long, Message> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessagesDataBaseRepository(String url, String username, String password, Validator<Message> validator) {
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
    public Optional<Message> findOne(Long entity) {
        String quarry="SELECT * FROM friendships WHERE id="+entity+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(quarry);
             ResultSet resultSet = statement.executeQuery()) {


            Optional<Message> f = Optional.empty();
            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id_chat");
                String message = resultSet.getString("message");
                Long id2 = resultSet.getLong("replay");
                Long id3 = resultSet.getLong("id_sender");
                Message m;
                if(id2==0) {
                    m = new Message(id1, message, id3);
                }
                else{
                    m = new Message(id1, message, id2, id3);
                }
                m.setId(id);
                f = Optional.of(m);
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
    public ArrayList<Message> findAll() {
        ArrayList<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id_chat");
                String message = resultSet.getString("message");
                Long id2 = resultSet.getLong("replay");
                Long id3 = resultSet.getLong("id_sender");
                String date = resultSet.getString("date");

                Message m;
                if(id2==0) {
                    m = new Message(id1,date, message, id3);
                }
                else{
                    m = new Message(id1,date, message, id2, id3);
                }
                m.setId(id);
                messages.add(m);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public ArrayList<Message> findAllChat(Long id_chat){
        ArrayList<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages where id_chat="+id_chat);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id_chat");
                String message = resultSet.getString("message");
                Long id2 = resultSet.getLong("replay");
                Long id3 = resultSet.getLong("id_sender");
                String date = resultSet.getString("date");

                Message m;
                if(id2==0) {
                    m = new Message(id1,date, message, id3);
                }
                else{
                    m = new Message(id1,date, message, id2, id3);
                }
                m.setId(id);
                messages.add(m);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     *
     * @param entity
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     */
    @Override
    public Optional<Message> save(Message entity) {
        validator.validate(entity);
        String query;
        if(entity.getReplay()!=null) {
            query = "INSERT INTO messages VALUES('" + entity.getId() + "','"
                    + entity.getId_chat() + "','"
                    + entity.getMessage() + "','"
                    + entity.getReplay() + "','"
                    + entity.getId_sender() + "','"
                    + entity.getDate() + "')";
        }
        else{
            query = "INSERT INTO messages (id,id_chat,message,id_sender,date) VALUES('"+entity.getId()+"','"
                    +entity.getId_chat()+"','"
                    +entity.getMessage()+"','"
                    + entity.getId_sender() + "','"
                    + entity.getDate() + "')";
        }
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
    public Optional<Message> delete(Long id) {
        Optional<Message> aux = findOne(id);
        if(aux.isEmpty()){
            throw new RepositoryException("Message does not exist");
        }
        String query = "DELETE FROM message WHERE id="+id+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.execute();
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aux;
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public Page<Message> findAll(Pageable pageable,Long id) {
        Paginator<Message> paginator = new Paginator<>(pageable, this.findAllChat(id));
        return paginator.paginate();
    }
}
