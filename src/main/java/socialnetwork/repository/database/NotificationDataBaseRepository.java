package socialnetwork.repository.database;

import socialnetwork.domain.Notification;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.memory.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.utils.NotificationTypes;
import socialnetwork.utils.State;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class NotificationDataBaseRepository implements PagingRepository<Long, Notification> {
    private final String url;
    private final String username;
    private final String password;

    public NotificationDataBaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
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
    public Optional<Notification> findOne(Long id) {
        String query="SELECT * FROM notification WHERE id="+id+";";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement((query));
            ResultSet resultSet = statement.executeQuery()){

            Optional<Notification> f = Optional.empty();
            while (resultSet.next()){
                Long idF = resultSet.getLong(1);
                Long id_sender = resultSet.getLong(2);
                Long id_receiver = resultSet.getLong(3);
                State state=State.pending;
                switch(resultSet.getString(4)){
                    case "accepted" -> state=State.accepted;
                    case "declined" -> state=State.declined;
                }
                String date = resultSet.getString((5));
                NotificationTypes type = null;
                if(resultSet.getString((6)).equals(NotificationTypes.FRIENDREQUEST))
                    type=NotificationTypes.FRIENDREQUEST;
                else
                    type=NotificationTypes.REMINDER;
                String message = resultSet.getString((7));
                Notification f2;
                if(message!=null)
                    f2 = new Notification(id_sender,id_receiver,state,type,message);
                else
                    f2 = new Notification(id_sender,id_receiver,state,type);
                f2.setId(idF);
                f2.setDate(date);
                f=Optional.of(f2);
            }
            return f;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     *
     * @return all entities
     */
    @Override
    public ArrayList<Notification> findAll() {
        ArrayList<Notification> notifications = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM notification");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long idF = resultSet.getLong(1);
                Long id_sender = resultSet.getLong(2);
                Long id_receiver = resultSet.getLong(3);
                State state=State.pending;
                switch(resultSet.getString(4)){
                    case "accepted" -> state=State.accepted;
                    case "declined" -> state=State.declined;
                }
                String date = resultSet.getString((5));
                NotificationTypes type = null;
                if(resultSet.getString((6)).equals(NotificationTypes.FRIENDREQUEST))
                    type=NotificationTypes.FRIENDREQUEST;
                else
                    type=NotificationTypes.REMINDER;
                String message = resultSet.getString((7));
                Notification f2;
                if(message!=null)
                    f2 = new Notification(id_sender,id_receiver,state,type,message);
                else
                    f2 = new Notification(id_sender,id_receiver,state,type);
                f2.setId(idF);
                f2.setDate(date);
                notifications.add(f2);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return notifications;
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
    public Optional<Notification> save(Notification entity) {
        String query;
        if(entity.getId_receiver()!=null)
            query="INSERT INTO notification VALUES('"+entity.getId().intValue()+"','"
                +entity.getId_sender().intValue()+"','"
                + entity.getId_receiver().intValue()+"','"
                + entity.getState() + "','"
                + entity.getDate() + "','"
                + entity.getNotificationTypes() + "','"
                + entity.getMessage() + "')";
        else
            query="INSERT INTO notification VALUES('"+entity.getId().intValue()+"','"
                    +entity.getId_sender().intValue()+"','"
                    + 0 +"','"
                    + entity.getState() + "','"
                    + entity.getDate() + "','"
                    + entity.getNotificationTypes() + "','"
                    + entity.getMessage() + "')";
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
    public Optional<Notification> delete(Long id) {
        Optional<Notification> aux = findOne(id);
        if(aux.isEmpty()){
            throw new RepositoryException("Chat does not exist");
        }
        String query = "DELETE FROM notification WHERE id="+id+";";
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
    public Optional<Notification> update(Notification entity) {
        String query="UPDATE notification SET state='" + entity.getState().toString()
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
    public Page<Notification> findAll(Pageable pageable, Long id) {
        Paginator<Notification> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
