package socialnetwork.service;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
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

public class UserService implements Observable<ChangeEvent> {
    private final PagingRepository<Long, User> repoUser;
    private Long FreeId;


    public UserService(PagingRepository<Long, User> repo) {
        this.repoUser = repo;
        FreeId=0L;
    }

    /**
     * Set the id used for save a user with the firs id possible
     */
    private void checkId(){
        FreeId=0L;
        ArrayList<Long> list = new ArrayList<>();
        for(User user: repoUser.findAll())
            list.add(user.getId());
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
     * @param firstName - The first name of the user
     * @param lastName - The last name of the user
     * @param date - The birthday of the user
     * @param gender - The gender of the user
     */
    public void addUser(String firstName, String lastName, String date, String gender, String email, String password, String image) {
        User user = new User(firstName, lastName, date, gender, email, password,image);
        checkId();
        user.setId(FreeId);
        Optional<User> u = repoUser.save(user);
        if(u.isPresent())
            notifyObservers(new ChangeEvent(ChangeEventType.ADD,u.get()));
    }

    /**
     *
     * @return all the users
     */
    public ArrayList<User> getAll(){
        return repoUser.findAll();
    }

    /**
     * Deletes the user with the provided id
     * @param id1 - id of the user to be deleted
     */
    public void deleteUser(String id1){
        Long id = FriendshipValidator.validateId(id1);
        Optional<User> u = repoUser.delete(id);
        if(u.isPresent())
                notifyObservers(new ChangeEvent(ChangeEventType.DELETE,u.get()));
        FreeId=0L;
    }


    public boolean modify(Long id,String firstName, String lastName, String date, String gender, String email, String password, String image){
        User user = new User(firstName, lastName, date, gender, email, password,image);
        user.setId(id);
        Optional<User> u = repoUser.update(user);
        if(u.isPresent()) {
            notifyObservers(new ChangeEvent(ChangeEventType.UPDATE, u.get()));
            return true;
        }
        return false;
    }

    /**
     *
     * @param id - the id of the user searched
     * @return one user
     */
    public User findOne(String id){
        Optional<User> user;
        user=repoUser.findOne(Long.parseLong(id));
        if(user.isEmpty())
            throw new ServiceException("User not found");
        else
            return user.get();
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
    private int size =100;

    private Pageable pageable;

    public void setPageSize(int size) {this.size = size;}

    public Set<User> getNextUsers(Long id){
        this.page++;
        return getUserOnPage(this.page, id);
    }

    public Set<User> getUserOnPage(int page,Long id){
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> UserPage = repoUser.findAll(pageable, id);
        return UserPage.getContent().collect(Collectors.toSet());
    }

    public int getAllPage(Long id){
        int x=0;
        Set<User> users=getUserOnPage(x, id);
        while(users.size()>0){
            x++;
            users = getUserOnPage(x, id);
        }
        return x;
    }
}
