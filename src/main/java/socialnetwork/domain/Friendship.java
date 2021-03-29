package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Friendship extends Entity<Long> {

    String date;
    Tuple<Long,Long> tuple;

    public Friendship(Long id,Tuple<Long,Long> t){
        setId(id);
        tuple=t;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        date=LocalDateTime.now().format(formatter);
    }

    @Override
    public String toString(){
        return " First user id= " + tuple.getRight() +
                " Second user id= " + tuple.getLeft() +
                " Date= " + date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public String getDate() {
        return date;
    }

    public void setDate(String data){
        date= data;
    }


    public Tuple<Long,Long> getPair(){
        return tuple;
    }
}
