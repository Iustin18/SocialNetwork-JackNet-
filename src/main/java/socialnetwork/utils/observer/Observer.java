package socialnetwork.utils.observer;


import socialnetwork.utils.events.Event;

import java.io.FileNotFoundException;

public interface Observer<E extends Event> {
    void update(E e) throws FileNotFoundException;
}