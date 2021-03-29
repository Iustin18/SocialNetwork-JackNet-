package socialnetwork.utils.events;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Notification;

public class ChangeEvent implements Event {
    private ChangeEventType type;
    private Entity data,oldData;

    public ChangeEvent(ChangeEventType type, Entity data) {
        this.type = type;
        this.data = data;
    }

    public ChangeEvent(ChangeEventType type, Notification data, Entity oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Entity getData() {
        return data;
    }

    public Entity getOldData() {
        return oldData;
    }
}