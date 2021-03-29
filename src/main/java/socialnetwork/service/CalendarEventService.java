package socialnetwork.service;

import socialnetwork.domain.CalendarEvent;
import socialnetwork.domain.User;
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

public class CalendarEventService implements Observable<ChangeEvent> {
    private PagingRepository<Long, User> user;
    private final PagingRepository<Long, CalendarEvent> calendarEvent;
    private Long FreeId;

    public CalendarEventService(PagingRepository<Long, User> user, PagingRepository<Long, CalendarEvent> calendarEvent) {
        this.user = user;
        this.calendarEvent = calendarEvent;
    }

    private void checkId(){
        FreeId=0L;
        ArrayList<Long> list = new ArrayList<>();
        for(CalendarEvent event: calendarEvent.findAll())
            list.add(event.getId());
        Collections.sort(list);
        for(Long x : list) {
            FreeId++;
            if (!x.equals(FreeId))
                return;
        }
        FreeId++;
    }

    public void AddCalendarEvent(String name, String date, String time, ArrayList<Long> participants, Long creator){
        CalendarEvent event=new CalendarEvent(name,date,time,participants,creator);
        checkId();
        event.setId(FreeId);
        Optional<CalendarEvent> c = calendarEvent.save(event);
        c.ifPresent(value -> notifyObservers(new ChangeEvent(ChangeEventType.ADD, value)));
    }

    public void delete(Long id){
        Optional<CalendarEvent> c = calendarEvent.delete(id);
        c.ifPresent(event -> notifyObservers(new ChangeEvent(ChangeEventType.DELETE, event)));
    }

    public void update(CalendarEvent c, Long id){
        ArrayList<Long> aux = new ArrayList<>();
        if(c.getParticipants()!=null)
            aux.addAll(c.getParticipants());
        aux.add(id);
        c.setParticipants(aux);
        Optional<CalendarEvent> cal = calendarEvent.update(c);
        cal.ifPresent(event -> notifyObservers(new ChangeEvent(ChangeEventType.UPDATE,event)));
    }

    public void updateRevers(CalendarEvent c, Long id){
        ArrayList<Long> aux = new ArrayList<>();
        aux.addAll(c.getParticipants());
        aux.remove(id);
        c.setParticipants(aux);
        Optional<CalendarEvent> cal = calendarEvent.update(c);
        cal.ifPresent(event -> notifyObservers(new ChangeEvent(ChangeEventType.UPDATE,event)));
    }

    public ArrayList<CalendarEvent> getAll(){
        return calendarEvent.findAll();
    }

    public  List<CalendarEvent> getAllUser(Long id){
        return calendarEvent.findAll().stream()
                .filter(x->{
                    if(x.getParticipants()!=null)
                        return x.getParticipants().contains(id);
                    else
                        return false;
                })
                .collect(Collectors.toList());
    }

    public CalendarEvent getOne(Long id){
        return calendarEvent.findOne(id).get();
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
        observers.forEach(x-> {
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

    public Set<CalendarEvent> getNextCalendarEvents(Long id){
        this.page++;
        return getCalendarEventsOnPage(this.page,id);
    }

    public Set<CalendarEvent> getCalendarEventsOnPage(int page,Long id){
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<CalendarEvent> CalendarEventsPage = calendarEvent.findAll(pageable,id);
        return CalendarEventsPage.getContent().collect(Collectors.toSet());
    }

    public int getAllPage(Long id){
        int x=0;
        Set<CalendarEvent> calendarEvents=getCalendarEventsOnPage(x,id);
        while(calendarEvents.size()>0){
            x++;
            calendarEvents = getCalendarEventsOnPage(x,id);
        }
        return x;
    }

}
