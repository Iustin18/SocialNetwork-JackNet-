package socialnetwork.domain.validators;

import socialnetwork.domain.CalendarEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarEventValidator implements Validator<CalendarEvent>{
    @Override
    public void validate(CalendarEvent entity) throws ValidationException {
        if(entity.getName().equals(""))
            throw new ValidationException("Name can't be null!");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatterH = DateTimeFormatter.ofPattern("HH:mm");
        String date = LocalDate.now().format(formatter);
        String time = LocalDateTime.now().format(formatterH);
        if (entity.getDate().compareTo(date)<0) {
            throw new ValidationException("Date must be later than the current day");
        }
        if(entity.getDate().compareTo(date)==0){
            if(entity.getTime().compareTo(time)<=0){
                throw new ValidationException("Time must be later than the current time");
            }
        }
    }
}
