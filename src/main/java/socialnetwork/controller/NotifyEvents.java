package socialnetwork.controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import socialnetwork.domain.CalendarEvent;


public class NotifyEvents{
    private Timeline timeline;
    private ObservableList<CalendarEvent> events;
    private ControllerUser controller;

    public NotifyEvents(ObservableList<CalendarEvent> events,ControllerUser co) {
        this.controller=co;
        this.events = events;
        this.timeline = new Timeline(new KeyFrame(Duration.minutes(1),this::PopUp));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void PopUp(ActionEvent actionEvent) {
        controller.GetTimeNotification();
    }

    public void start(){
        this.timeline.play();
    }

    public void stop(){
        this.timeline.stop();
    }
}