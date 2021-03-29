package socialnetwork.service;

import socialnetwork.domain.Notification;
import socialnetwork.domain.User;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.utils.NotificationTypes;
import socialnetwork.utils.State;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationService implements Observable<ChangeEvent> {
    private PagingRepository<Long, Notification> repoFriendRequest;
    private PagingRepository<Long, User> repoUser;
    private Long FreeId;

    public NotificationService(PagingRepository<Long, Notification> repoFriendRequest, PagingRepository<Long, User> repoUser) {
        this.repoFriendRequest = repoFriendRequest;
        this.repoUser = repoUser;
    }

    /**
     * Set the id used for save a user with the firs id possible
     */
    private void checkId(){
        FreeId=0L;
        ArrayList<Long> list = new ArrayList<>();
        for(Notification notification : repoFriendRequest.findAll())
            list.add(notification.getId());
        Collections.sort(list);
        for(Long x : list) {
            FreeId++;
            if (!x.equals(FreeId))
                return;
        }
        FreeId++;
    }

    /**
     *
     * @return all the friends requests
     */
    public ArrayList<Notification> getAll(){
        return repoFriendRequest.findAll();
    }

    /**
     * Create a friendRequest with the parameters and save it
     * @param id_sender - The id of the user who sent the request
     * @param id_receiver - The id of the user who received the request
     */
    public void add(String id_sender, String id_receiver, NotificationTypes type, String message){
        Long id2=null;

        if(id_receiver!=null)
            id2 = Long.parseLong(id_receiver);

        checkId();
        Notification f;
        if(message==null)
            f = new Notification(Long.parseLong(id_sender),id2, State.pending, type);
        else
            f = new Notification(Long.parseLong(id_sender),id2, State.pending, type,message);
        f.setId(FreeId);
        Optional<Notification> fr = repoFriendRequest.save(f);
        if(fr.isPresent())
            notifyObservers(new ChangeEvent(ChangeEventType.ADD,fr.get()));
    }

    public void delte(Long id){
        Optional<Notification> f = repoFriendRequest.delete(id);
        if(f.isPresent())
            notifyObservers(new ChangeEvent(ChangeEventType.DELETE,f.get()));
    }

    /**
     *
     * @param id_not - the id of the friendRequest searched
     * @return one friend request
     */
    public Notification getOne(String id_not) {
        Optional<Notification> f;
        Long id;
        try{
            id=Long.parseLong(id_not);
        } catch (NumberFormatException e) {
            throw new ServiceException("Id must me a number!");
        }
        f=repoFriendRequest.findOne(id);
        if(f.isEmpty() || f.get().getState()==State.accepted || f.get().getState()==State.declined)
            throw new ServiceException("Id invalid");
        else
            return f.get();
    }

    /**
     *
     * @param f - the FriendRequest to modify
     */
    public void modify(Notification f){
        Optional<Notification> fr = repoFriendRequest.update(f);
        if(fr.isPresent())
            notifyObservers(new ChangeEvent(ChangeEventType.UPDATE,fr.get()));
    }

    /**
     *
     * @param id- the id of the user
     * @return all the friends requests of an user
     */
    public List<Notification> getAllUser(int id){
        return getAll().stream().filter(x->(x.getId_receiver().intValue()==id || x.getId_sender().intValue()==id) && x.getState()==State.pending).collect(Collectors.toList());
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

    //-----------Paginating--------------

    private int page = 0;
    private int size = 100;

    private Pageable pageable;

    public void setPageSize(int size) {this.size = size;}

    public Set<Notification> getNextFriendRequests(Long id){
        this.page++;
        return getFriendRequestsOnPage(this.page, id);
    }

    public Set<Notification> getFriendRequestsOnPage(int page, Long id){
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Notification> FriendRequestPage = repoFriendRequest.findAll(pageable, id);
        return FriendRequestPage.getContent().collect(Collectors.toSet());
    }

    public int getAllPage(Long id){
        int x=0;
        Set<Notification> notifications =getFriendRequestsOnPage(x, id);
        while(notifications.size()>0){
            x++;
            notifications = getFriendRequestsOnPage(x, id);
        }
        return x;
    }
}
