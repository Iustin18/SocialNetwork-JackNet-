package socialnetwork.domain;

import socialnetwork.utils.NotificationTypes;
import socialnetwork.utils.State;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification extends Entity<Long>{
    private final Long id_sender;
    private final Long id_receiver;
    private State state;
    private String date;
    private String message=null;
    private NotificationTypes notificationTypes;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public Notification(Long id_sender, Long id_reciver, State state, NotificationTypes notificationTypes1) {
        this.id_sender = id_sender;
        this.id_receiver = id_reciver;
        this.state = state;
        date= LocalDateTime.now().format(formatter);
        this.notificationTypes = notificationTypes1;
    }

    public Notification(Long id_sender, Long id_reciver, State state,NotificationTypes notificationTypes1,String message) {
        this.id_sender = id_sender;
        this.id_receiver = id_reciver;
        this.state = state;
        this.message = message;
        date= LocalDateTime.now().format(formatter);
        this.notificationTypes = notificationTypes1;
    }

    public Long getId_sender() {
        return id_sender;
    }

    public Long getId_receiver() {
        return id_receiver;
    }

    public State getState() {
        return state;
    }

    public void setState(State state){
        this.state=state;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date=date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationTypes getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(NotificationTypes notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    @Override
    public String toString() {
        String rez;
        if(id_receiver!=0)
            rez="Friend Request from user with id: " + id_sender + ", " + date;
        else
            rez="Reminder: " + message + " " + date;
        return rez;
    }
}
