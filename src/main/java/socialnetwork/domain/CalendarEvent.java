package socialnetwork.domain;

import java.util.ArrayList;

public class CalendarEvent extends Entity<Long> {
    private String name;
    private String date;
    private String time;
    private ArrayList<Long> participants;
    private Long creator;

    public CalendarEvent(String name, String date, String time, ArrayList<Long> participants,Long creator) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.participants = participants;
        this.creator=creator;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Long> participants) {
        this.participants = participants;
    }

    public Long getCreator() {
        return creator;
    }

    @Override
    public String toString() {
        return name + " " + date + " " + time + " " + participants;
    }
}
