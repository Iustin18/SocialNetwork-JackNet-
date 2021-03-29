package socialnetwork.domain;

import java.util.ArrayList;
import java.util.Arrays;

public class Chat extends Entity<Long>{
    private String name="Chat";
    private ArrayList<Long> participants;

    public Chat(String name, ArrayList<Long> participants) {
        this.name = name;
        this.participants = participants;
    }

    public Chat(ArrayList<Long> participants) {
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Long> getParticipants() {
        return participants;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Id= " + getId() +
                "name= " + name  +
                ", participants= " + Arrays.toString(participants.toArray());
    }
}
