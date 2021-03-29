package socialnetwork.repository.database;
import socialnetwork.domain.User;
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

public class UserDataBaseRepository implements PagingRepository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDataBaseRepository(String url, String username, String password, Validator<User> validator) {
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
    public Optional<User> findOne(Long id) {
        String query="SELECT * FROM users WHERE id="+id+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Optional<User> f= Optional.empty();
            while(resultSet.next()) {
                Long id1 = Long.parseLong(resultSet.getString(1));
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String date = resultSet.getString(4);
                String gender = resultSet.getString(5);
                String email = resultSet.getString( 6);
                String password = resultSet.getString(7);
                String image = resultSet.getString("image");

                User user = new User(firstName, lastName, date, gender,email,password,image);
                user.setId(id1);
                f = Optional.of(user);
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
    public ArrayList<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String date = resultSet.getString("date");
                String gender = resultSet.getString("gender");
                String email = resultSet.getString( "email");
                String password = resultSet.getString("password");
                String image = resultSet.getString("image");

                User user = new User(firstName, lastName, date, gender,email,password,image);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
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
    public Optional<User> save(User entity) {
        validator.validate(entity);
        String query = "INSERT INTO users VALUES('"+entity.getId().intValue()+"','"
                +entity.getFirstName()+"','"+entity.getLastName()+"','"
                +entity.getDate()+"','"+entity.getGender()+"','"+entity.getEmail()+
                "','"+entity.getPassword()+"','"+entity.getImage()+"')";
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
    public Optional<User> delete(Long id) {
        Optional<User> aux = findOne(id);
        if(aux.isEmpty()){
            throw new RepositoryException("User does not exist");
        }
        String query = "DELETE FROM users WHERE id="+aux.get().getId()+";";
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
    public Optional<User> update(User entity) {
        String query="UPDATE users SET first_name='" + entity.getFirstName() + "', last_name='" + entity.getLastName()
                + "', image='" + entity.getImage()
                + "', password='" + entity.getPassword()
                + "' WHERE id="+entity.getId().toString();
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

    @Override
    public Page<User> findAll(Pageable pageable,Long id) {
        Paginator<User> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
