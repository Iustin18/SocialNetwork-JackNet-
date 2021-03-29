package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.utils.Graph;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class FriendshipService implements Observable<ChangeEvent> {
    private final PagingRepository<Long, Friendship> repo;
    private final PagingRepository<Long, User> repoUser;
    private Long FreeId;

    public FriendshipService(PagingRepository<Long, Friendship> repo, PagingRepository<Long, User> repoUser) {
        this.repo = repo;
        this.repoUser = repoUser;
    }

    /**
     * Set the id used for save a user with the firs id possible
     */
    private void checkId(){
        FreeId=0L;
        ArrayList<Long> list = new ArrayList<>();
        for(Friendship f: repo.findAll())
            list.add(f.getId());
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
     * @return all the friendships
     */
    public ArrayList<Friendship> getAll(){
        return repo.findAll();
    }

    /**
     * Add a friendship with the specified id
     * @param id1 - id of the first user
     * @param id2 - id of the second user
     */
    public void addFriendship(String id1, String id2){
        checkId();
        Long first = FriendshipValidator.validateId(id1);
        Long second = FriendshipValidator.validateId(id2);
        if(first>second){
            Long aux=first;
            first = second;
            second = aux;
        }
        repoUser.findOne(first);
        repoUser.findOne(second);
        Optional<Friendship> f = repo.save(new Friendship(FreeId,new Tuple<>(first, second)));
        if(f.isPresent()){
            notifyObservers(new ChangeEvent(ChangeEventType.ADD,f.get()));
        }
    }

    /**
     * Delete a friendship that have the specified ids
     * @param id1 - id of the first user
     * @param id2 - id of the second user
     */
    public void deleteFriendshipUser(String id1,String id2){
        List<Friendship> list = getAll().stream().filter(x->(x.getPair().getRight().toString().equals(id1) && x.getPair().getLeft().toString().equals(id2)) ||
                                                        (x.getPair().getRight().toString().equals(id2) && x.getPair().getLeft().toString().equals(id1))).collect(Collectors.toList());
        Long first = list.get(0).getId();
        Optional<Friendship> f = repo.delete(first);
        if(f.isPresent()){
            notifyObservers(new ChangeEvent(ChangeEventType.DELETE,f.get()));
        }
    }

    /**
     * Delete a friendship that have the specified id
     * @param id - id of the friendship
     */
    public void deleteFriendship(String id){
        Long first = FriendshipValidator.validateId(id);
        Optional<Friendship> f = repo.delete(first);
        if(f.isPresent()){
            notifyObservers(new ChangeEvent(ChangeEventType.DELETE,f.get()));
        }
    }

    /**
     *
     * @return the community number
     */
    public int numberCommunities(){
        int nod=0;
        for(User x : repoUser.findAll()) {
            if (nod < x.getId().intValue()) {
                nod = x.getId().intValue();
            }
        }
        nod++;
        Graph graph = new Graph(nod);
        repo.findAll().forEach(x->graph.addEdge(x.getId().intValue(),x.getId().intValue()));
        return  graph.connectedComponents();
    }

    /**
     *
     * @return an array of integers with the id of the community users
     */
    public ArrayList<Integer> longestCommunity(){
        int nod=0;
        for(User x : repoUser.findAll())
            if(nod<x.getId().intValue()){
                nod=x.getId().intValue();
            }
        nod++;
        Graph graph = new Graph(nod);
        repo.findAll().forEach(x-> graph.addEdge(x.getId().intValue(),x.getId().intValue()));
        return graph.longestPath();
    }

    /**
     *
     * @param id - the id of the user
     * @return a list of Strings with the first name, the last name and the date of the user's friends
     */
    public List<String> showFriendships(Long id) {
        return repo.findAll().stream()
                .filter(f->{return f.getPair().getLeft().equals(id) || f.getPair().getRight().equals(id);})
                .map(f->{
                    Long idAux;
                    if(f.getPair().getRight().equals(id)) idAux=f.getPair().getLeft();
                    else idAux=f.getPair().getRight();
                    User u = repoUser.findOne(idAux).get();
                    return "First Name: " + u.getFirstName() + " Last Name: " + u.getLastName() + " Date: " + f.getDate();
                })
                .collect(Collectors.toList());
    }

    /**
     *
     * @param id - the id of the user
     * @return a list of Strings with the id, first name, last name and date of the user's friends
     */
    public List<String> showFriendshipsWithId(Long id) {
        return repo.findAll().stream()
                .filter(f->{return f.getPair().getLeft().equals(id) || f.getPair().getRight().equals(id);})
                .map(f->{
                    Long idAux;
                    if(f.getPair().getRight().equals(id)) idAux=f.getPair().getLeft();
                    else idAux=f.getPair().getRight();
                    User u = repoUser.findOne(idAux).get();
                    return "Id= " + u.getId() + " First Name: " + u.getFirstName() + " Last Name: " + u.getLastName() + " Date: " + f.getDate();
                })
                .collect(Collectors.toList());
    }

    /**
     *
     * @param id - the id of the user
     * @param month - the month
     * @return a list of Strings with the first name, the last name and the date of the user's friends that became friends in that month
     */
    public List<String> showFriendshipsMonth(Long id, String month){
        return repo.findAll().stream()
                .filter(f->{
                    String[] date = f.getDate().split("-");
                    return (f.getPair().getLeft().equals(id) || f.getPair().getRight().equals(id)) && date[1].equals(month);
                })
                .map(f->{
                    Long idAux;
                    if(f.getPair().getRight().equals(id)) idAux=f.getPair().getLeft();
                    else idAux=f.getPair().getRight();
                    User u = repoUser.findOne(idAux).get();
                    return "First Name: " + u.getFirstName() + " Last Name: " + u.getLastName() + " Date: " + f.getDate();
                })
                .collect(Collectors.toList());
    }

    private List<Observer<ChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) { }

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


    public List<String> PeriodFriends(Long id, String sta, String fin) throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdformat.parse(sta);
        Date finish = sdformat.parse(fin);
        return repo.findAll().stream()
                .filter(x->{
                    Date aux = null;
                    try {
                        aux = sdformat.parse(x.getDate().split(" ")[0]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return aux.compareTo(start) >= 0 && aux.compareTo(finish) <= 0 && (x.getPair().getLeft().equals(id) || x.getPair().getRight().equals(id));
                })
                .map(f->{
                    Long idAux;
                    if(f.getPair().getRight().equals(id)) idAux=f.getPair().getLeft();
                    else idAux=f.getPair().getRight();
                    User u = repoUser.findOne(idAux).get();
                    return "First Name: " + u.getFirstName() + ", Last Name: " + u.getLastName() + ", Date: " + f.getDate();
                })
                .collect(Collectors.toList());
    }


    //-----------Paginating--------------

    private int page = 0;
    private int size = 100;

    private Pageable pageable;

    public void setPageSize(int size) {this.size = size;}

    public Set<Friendship> getNextFriendships(Long id){
        this.page++;
        return getFriendshipsOnPage(this.page,id);
    }

    public Set<Friendship> getFriendshipsOnPage(int page,Long id){
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Friendship> FriendshipPage = repo.findAll(pageable,id);
        return FriendshipPage.getContent().collect(Collectors.toSet());
    }

    public int getAllPage(){
        int x=0;
        Set<Friendship> friendships=getFriendshipsOnPage(x,1L);
        while(friendships.size()>0){
            x++;
            friendships = getFriendshipsOnPage(x,1L);
        }
        return x;
    }
}
