package socialnetwork.service;

import socialnetwork.domain.Chat;
import socialnetwork.domain.Entity;
import socialnetwork.domain.Message;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MessageService implements Observable<ChangeEvent> {
    private final PagingRepository<Long, Message> repoMessage;
    private final PagingRepository<Long, Chat> repoChat;
    private Long FreeId;

    public MessageService(PagingRepository<Long, Message> repoMessage, PagingRepository<Long, Chat> repoChat) {
        this.repoMessage = repoMessage;
        this.repoChat = repoChat;
    }

    /**
     * Set the id used for save a user with the firs id possible
     */
    private void checkId(){
        FreeId=0L;
        ArrayList<Long> list = new ArrayList<>();
        for(Message message: repoMessage.findAll())
            list.add(message.getId());
        Collections.sort(list);
        for(Long x : list) {
            FreeId++;
            if (!x.equals(FreeId))
                return;
        }
        FreeId++;
    }

    /**
     * Create a message with the parameters and save it
     * @param id - The id of the chat
     * @param message - The message
     * @param id_sender - The id of the sender
     */
    public void addMessage(String id,String message,String id_sender){
        checkId();
        Optional<Chat> c = repoChat.findOne(Long.parseLong(id));
        if(c.isEmpty())
            throw new ServiceException("Id chat invalid!");
        Message m = new Message(Long.parseLong(id),message,Long.parseLong(id_sender));
        m.setId(FreeId);
        Optional<Message> mes = repoMessage.save(m);
        if(mes.isPresent())
            notifyObservers(new ChangeEvent(ChangeEventType.ADD,mes.get()));
    }

    /**
     * Create an user with the parameters and save it
     * @param id - The id of the chat
     * @param message - The message
     * @param replay - The id of the message that is replayed
     * @param id_sender - The id of the sender
     */
    public void addMessage(String id,String message,String replay,String id_sender){
        checkId();
        Optional<Chat> c = repoChat.findOne(Long.parseLong(id));
        if(c.isEmpty())
            throw new ServiceException("Id chat invalid!");
        Message m = new Message(Long.parseLong(id),message,Long.parseLong(replay),Long.parseLong(id_sender));
        System.out.println(m.getReplay());
        m.setId(FreeId);
        Optional<Message> mes = repoMessage.save(m);
        if(mes.isPresent())
            notifyObservers(new ChangeEvent(ChangeEventType.ADD,mes.get()));
    }

    /**
     *
     * @return all the users
     */
    public ArrayList<Message> getAll(){
        return repoMessage.findAll();
    }

    private List<Observer<ChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) {

    }

    @Override
    public void notifyObservers(ChangeEvent t) {
        observers.stream().forEach(x-> {
            try {
                x.update(t);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> PeriodMessage(Long id, String sta, String fin) throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdformat.parse(sta);
        Date finish = sdformat.parse(fin);
        List<Long> chats = repoChat.findAll().stream()
                .filter(x-> x.getParticipants().contains(id))
                .map(Entity::getId)
                .collect(Collectors.toList());
        return repoMessage.findAll().stream()
                .filter(x->{
                    String aux = x.getDate().split(" ")[0];
                    aux = aux.split("-")[2] + "-" + aux.split("-")[1] + "-" + aux.split("-")[0];
                    Date date=null;
                    try {
                        date = sdformat.parse(aux);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return date.compareTo(start) > 0 && date.compareTo(finish) < 0 && chats.contains(x.getId_chat());
                })
                .map(f->{
                    String aux = "Sender: " + f.getId_sender().toString() + ", Message: " + f.getMessage() + ", Date: " + f.getDate();
                    if(f.getReplay()!=null)
                        aux = aux + ", Replay: " + f.getReplay().toString();
                    return aux;
                })
                .collect(Collectors.toList());
    }

    public List<String> PeriodMessage(Long id,Long id_friend, String sta, String fin) throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdformat.parse(sta);
        Date finish = sdformat.parse(fin);
        List<Long> chat = repoChat.findAll().stream()
                .filter(x-> x.getParticipants().contains(id) && x.getParticipants().contains(id_friend) && x.getParticipants().size()==2)
                .map(Entity::getId)
                .collect(Collectors.toList());
        System.out.println(chat);
        List<String> list = repoMessage.findAll().stream()
                .filter(x->{
                    String aux = x.getDate().split(" ")[0];
                    aux = aux.split("-")[2] + "-" + aux.split("-")[1] + "-" + aux.split("-")[0];
                    Date date=null;
                    try {
                        date = sdformat.parse(aux);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(date + "| " + start + "| "  + finish);
                    return date.compareTo(start) > 0 && date.compareTo(finish) < 0 && chat.contains(x.getId_chat());
                })
                .map(f->{
                    String aux = "Sender: " + f.getId_sender().toString() + ", Message: " + f.getMessage() + ", Date: " + f.getDate();
                    if(f.getReplay()!=null)
                        aux = aux + ", Replay: " + f.getReplay().toString();
                    return aux;
                })
                .collect(Collectors.toList());
        System.out.println(list);
        return list;
    }

    //-----------Paginating--------------

    private int page = 0;
    private int size = 5;

    private Pageable pageable;

    public void setPageSize(int size) {this.size = size;}

    public Set<Message> getNextMessages(Long id){
        this.page++;
        return getMessageOnPage(this.page,id);
    }

    public Set<Message> getMessageOnPage(int page,Long id){
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        return repoMessage.findAll(pageable,id).getContent()
                .collect(Collectors.toSet());
    }

    public int getAllPage(Long id){
        int x=0;
        Set<Message> messages=getMessageOnPage(x,id);
        while(messages.size()>0){
            x++;
            messages = getMessageOnPage(x,id);
        }
        return x;
    }

}
