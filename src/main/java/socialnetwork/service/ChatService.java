package socialnetwork.service;

import socialnetwork.domain.Chat;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ChatService implements Observable<ChangeEvent> {
    private PagingRepository<Long, Chat> chatRepository;
    private PagingRepository<Long, User> userRepo;
    private Long FreeId=0L;

    public ChatService(PagingRepository<Long, Chat> caht,PagingRepository<Long,User> userrepo) {
        this.chatRepository = caht;
        this.userRepo = userrepo;
    }

    /**
     * Set the id used for save a user with the firs id possible
     */
    private void checkId(){
        FreeId=0L;
        ArrayList<Long> list = new ArrayList<>();
        for(Chat chat: chatRepository.findAll())
            list.add(chat.getId());
        Collections.sort(list);
        for(Long x : list) {
            FreeId++;
            if (!x.equals(FreeId))
                return;
        }
        FreeId++;
    }

    /**
     * Create an user with the parameters and save it
     * @param name - The name of the chat
     * @param participants - The arrayList of the participants of the chat
     */
    public void AddChat(String name, ArrayList<Long> participants){
        Chat chat=new Chat(name,participants);
        checkId();
        chat.setId(FreeId);
        Optional<Chat> c = chatRepository.save(chat);
        if(c.isPresent()) {
            notifyObservers(new ChangeEvent(ChangeEventType.ADD, c.get()));
        }
    }

    public void delete(Long id){
        Optional<Chat> c = chatRepository.delete(id);
        if(c.isPresent()) {
            notifyObservers(new ChangeEvent(ChangeEventType.DELETE, c.get()));
        }
    }

    /**
     *
     * @return all the chats
     */
    public ArrayList<Chat> getAll(){
        return chatRepository.findAll();
    }

    public Chat getOne(Long id){
        return chatRepository.findOne(id).get();
    }

    /**
     *
     * @param id1 - the first id of the chat
     * @param id2 - the secind id of the chat
     * @return all the chat that have the only two users and the users have the ids specified
     */
    public List<Chat> getPairChatId(String id1,String id2){
        Long idUser1,idUser2;
        try{
            idUser1=Long.parseLong(id1);
            idUser2=Long.parseLong(id2);
        } catch (NumberFormatException e) {
            throw new ServiceException("Id must be number");
        }
        userRepo.findOne(idUser1);
        userRepo.findOne(idUser2);
        List<Chat> lista = getAll().stream().filter(c->c.getParticipants().size()==2).collect(Collectors.toList());

        return  lista.stream().filter(x->{
            ArrayList<Long> part = x.getParticipants();
            return (part.get(0).equals(idUser1) && part.get(1).equals(idUser2)) || (part.get(0).equals(idUser2) && part.get(1).equals(idUser1));
            }).collect(Collectors.toList());
    }

    /**
     *
     * Verify if a chat contains a user
     * @param id - the id of the user
     * @param id_chat - the id of the chat
     */
    public void verify(int id,String id_chat){
        if(!chatRepository.findOne(Long.parseLong(id_chat)).get().getParticipants().contains(Long.parseLong(Integer.toString(id))))
            throw new ServiceException("Id chat invalid");
    }

    public void modify(Chat c){
        try {
            chatRepository.update(c);
            notifyObservers(new ChangeEvent(ChangeEventType.UPDATE, c));
        } catch (Exception e) {
            throw new ServiceException("Can't update the name of the chat!");
        }
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

    private int page = 0;
    private int size = 100;

    private Pageable pageable;

    public void setPageSize(int size) {this.size = size;}

    public Set<Chat> getNextChats(Long id){
        this.page++;
        return getChatsOnPage(this.page,id);
    }

    public Set<Chat> getChatsOnPage(int page,Long id){
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Chat> ChatPage = chatRepository.findAll(pageable, id);
        return ChatPage.getContent().collect(Collectors.toSet());
    }

    public int getAllPage(){
        int x=0;
        Set<Chat> chat=getChatsOnPage(x,1L);
        while(chat.size()>0){
            x++;
            chat = getChatsOnPage(x,1L);
        }
        return x;
    }
}
