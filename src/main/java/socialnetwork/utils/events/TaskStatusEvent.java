package socialnetwork.utils.events;


import socialnetwork.domain.Entity;

public class TaskStatusEvent implements Event {
    private TaskExecutionStatusEventType type;
    private Entity entity;
    public TaskStatusEvent(TaskExecutionStatusEventType type, Entity entity) {
        this.entity=entity;
        this.type=type;
    }

    public Entity getTask() {
        return entity;
    }

    public void setTask(Entity task) {
        this.entity = task;
    }

    public TaskExecutionStatusEventType getType() {
        return type;
    }

    public void setType(TaskExecutionStatusEventType type) {
        this.type = type;
    }
}
