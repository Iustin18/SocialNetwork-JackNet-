package socialnetwork.repository.database;

import socialnetwork.domain.CalendarEvent;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class CalendarEventDataBaseRepository implements PagingRepository<Long, CalendarEvent> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<CalendarEvent> validator;

    public CalendarEventDataBaseRepository(String url, String username, String password, Validator<CalendarEvent> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<CalendarEvent> findOne(Long id) {
        String query="SELECT * FROM events WHERE id="+id+";";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()){

            Optional<CalendarEvent> c = Optional.empty();
            while(resultSet.next()){
                Long id1 = Long.parseLong(resultSet.getString(1));
                String name = resultSet.getString(2);
                String date = resultSet.getString(3);
                String time = resultSet.getString(4);
                ArrayList<Long> participants=new ArrayList<>();
                String aux = resultSet.getArray(5).toString();
                Long creator = Long.parseLong(resultSet.getString(6));
                aux=aux.replace(","," ");
                aux=aux.replace("{","");
                aux=aux.replace("}","");
                try{
                    for (String s : aux.split(" ")) {
                        participants.add(Long.parseLong(s));
                    }
                } catch (NumberFormatException e) {
                    participants=null;
                }
                CalendarEvent event = new CalendarEvent(name,date,time,participants,creator);
                event.setId(id1);
                event.setDate(date);
                c=Optional.of(event);
            }
            return c;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<CalendarEvent> findAll() {
        ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM events");
            ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()){
                Long id = Long.parseLong(resultSet.getString(1));
                String name = resultSet.getString(2);
                String date = resultSet.getString(3);
                String time = resultSet.getString(4);
                ArrayList<Long> participants=new ArrayList<>();
                String aux = resultSet.getArray(5).toString();
                Long creator = Long.parseLong(resultSet.getString(6));
                aux = aux.replace(",", " ");
                aux = aux.replace("{", "");
                aux = aux.replace("}", "");
                try{
                    for (String s : aux.split(" ")) {
                        participants.add(Long.parseLong(s));
                    }
                } catch (NumberFormatException e) {
                    participants=null;
                }
                CalendarEvent event = new CalendarEvent(name,date,time,participants,creator);
                event.setId(id);
                event.setDate(date);
                calendarEvents.add(event);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return calendarEvents;
    }

    @Override
    public Optional<CalendarEvent> save(CalendarEvent entity) {
        validator.validate(entity);
        String participants = "{";
        if(entity.getParticipants()!=null) {
            participants = "{";
            for (Long x : entity.getParticipants()) {
                participants = participants.concat("\"").concat(x.toString()).concat("\"").concat(",");
            }
            participants = participants.substring(0, participants.length() - 1);
        }
        participants = participants.concat("}");
        String query="INSERT INTO events VALUES('"+entity.getId().intValue()+"','"
                +entity.getName()+"','"+entity.getDate()+"','"+entity.getTime() +"','"+ participants +"','"+ entity.getCreator() +"')";
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

    @Override
    public Optional<CalendarEvent> delete(Long id) {
        Optional<CalendarEvent> aux = findOne(id);
        if(aux.isEmpty()){
            throw new RepositoryException("CalendarEvent does not exist");
        }
        String query = "DELETE FROM events WHERE id="+id+";";
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
    public Optional<CalendarEvent> update(CalendarEvent entity) {
        String participants = "{";
        if(entity.getParticipants()!=null) {
            for (Long x : entity.getParticipants()) {
                participants = participants.concat("\"").concat(x.toString()).concat("\"").concat(",");
            }
            participants = participants.substring(0, participants.length() - 1);
        }
        participants = participants.concat("}");
        String query="UPDATE events SET participants='" + participants
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
    public Page<CalendarEvent> findAll(Pageable pageable,Long id) {
        Paginator<CalendarEvent> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}

