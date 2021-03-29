package socialnetwork.domain.validators;

import socialnetwork.domain.Chat;

import java.util.HashMap;

public class ChatValidator implements Validator<Chat> {
    @Override
    public void validate(Chat entity) throws ValidationException {
        String s="";
        HashMap<Long,Long> aux= new HashMap<>();
        for(Long l : entity.getParticipants()) {
            aux.putIfAbsent(l, l);
        }
        if(entity.getName().equals(""))
            s=s.concat("Name is null");
        if(aux.size()!=entity.getParticipants().size())
            s=s.concat("There cannot be two equal id");
        if(!s.equals(""))
            throw new ValidationException(s);
    }
}
