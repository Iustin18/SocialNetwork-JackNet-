package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message extends Entity<Long> {
    private final Long id_chat;
    private final String message;
    private final Long id_sender;
    private Long replay=null;
    private String date;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Message(Long id_chat, String message,Long replay, Long sender) {
        this.id_chat = id_chat;
        this.message = message;
        this.id_sender=sender;
        this.replay = replay;
        this.date = LocalDateTime.now().format(formatter);
    }

    public Message(Long id_chat, String message, Long sender) {
        this.id_chat = id_chat;
        this.message = message;
        this.id_sender = sender;
        this.date=LocalDateTime.now().format(formatter);
    }

    public Message(Long id_chat,String data, String message, Long sender) {
        this.id_chat = id_chat;
        this.message = message;
        this.id_sender = sender;
        this.date = data;
    }

    public Message(Long id_chat,String date, String message,Long replay, Long sender) {
        this.id_chat = id_chat;
        this.message = message;
        this.id_sender=sender;
        this.replay = replay;
        this.date = date;
    }

    public Long getId_chat() {
        return id_chat;
    }

    public String getMessage() {
        return message;
    }

    public Long getId_sender(){ return id_sender; }

    public Long getReplay() {
        return replay;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        String aux="";
        if(replay!=null)
            aux="|Replay to the message with id= " + replay + "|\n";
        return aux + "[" + date + "] " +
                message;
    }
}
