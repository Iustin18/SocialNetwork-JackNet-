package socialnetwork.repository.database;

import socialnetwork.domain.Chat;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class ChatsDataBaseRepository implements PagingRepository<Long, Chat> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Chat> validator;

    public ChatsDataBaseRepository(String url, String username, String password, Validator<Chat> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     *
     */
    @Override
    public Optional<Chat> findOne(Long id) {
        String query="SELECT * FROM chats WHERE id="+id+";";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()){

            Optional<Chat> c = Optional.empty();
            while(resultSet.next()){
                Long id1 = Long.parseLong(resultSet.getString(1));
                String name = resultSet.getString(2);
                ArrayList<Long> participants=new ArrayList<>();
                String aux = resultSet.getArray(3).toString();
                aux=aux.replace(","," ");
                aux=aux.replace("{","");
                aux=aux.replace("}","");
                for(String s: aux.split(" ")) {
                    participants.add(Long.parseLong(s));
                }

                Chat chat = new Chat(name,participants);
                chat.setId(id1);
                c=Optional.of(chat);
            }
            return c;

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
    public ArrayList<Chat> findAll() {
        ArrayList<Chat> chats = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM chats");
            ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()){
                Long id=resultSet.getLong(1);
                String name = resultSet.getString(2);
                ArrayList<Long> participants=new ArrayList<>();
                String aux = resultSet.getArray(3).toString();
                aux=aux.replace(","," ");
                aux=aux.replace("{","");
                aux=aux.replace("}","");
                for(String s: aux.split(" ")) {
                    participants.add(Long.parseLong(s));
                }

                Chat chat = new Chat(name,participants);
                chat.setId(id);
                chats.add(chat);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return chats;
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
    public Optional<Chat> save(Chat entity) {
        validator.validate(entity);
        String participants="{";
        for(Long x : entity.getParticipants()){
            participants = participants.concat("\"").concat(x.toString()).concat("\"").concat(",");
        }
        participants = participants.substring(0,participants.length()-1);
        participants=participants.concat("}");
        String query="INSERT INTO chats VALUES('"+entity.getId().intValue()+"','"
                +entity.getName()+"','"+ participants +"')";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query)
            ) {
            statement.execute();
            return Optional.of(entity);
        }catch (SQLException e){
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
    public Optional<Chat> delete(Long id) {
        Optional<Chat> aux = findOne(id);
        if(aux.isEmpty()){
            throw new RepositoryException("Chat does not exist");
        }
        String query = "DELETE FROM chats WHERE id="+id+";";
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
    public Optional<Chat> update(Chat entity) {
        String query="UPDATE chats SET name='" + entity.getName()
                + "' WHERE id="+entity.getId().toString();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Page<Chat> findAll(Pageable pageable,Long id) {
        Paginator<Chat> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }

}
