package com.avans.domain.backlog;
import  java.util.ArrayList;
import  java.util.List;

import com.avans.domain.backlog.state.DoingState;
import com.avans.domain.backlog.state.DoneState;
import com.avans.domain.backlog.state.IBacklogState;
import com.avans.domain.backlog.state.ReadyForTestingState;
import com.avans.domain.backlog.state.TodoState;
import com.avans.domain.member.TeamMember;
import com.avans.observer.Subject;


public class BacklogItem extends Subject {
    private String title;
    private IBacklogState state;
    private TeamMember assignedDeveloper;
    private List<Activity> activities;
    

    public BacklogItem(String title) {
        this.title = title;
        this.state = new TodoState(); // start in TODO
        this.activities = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public IBacklogState getState() {
        return state;
    }

    public void setState(IBacklogState newState) {
        IBacklogState oldState = this.state;
        this.state = newState;
         // Notify observers based on state transitions
        if (oldState instanceof DoingState && state instanceof ReadyForTestingState) {
            notifyObservers("Backlog item '" + title + "' is ready for testing.");
        } else if (state instanceof TodoState && !(oldState instanceof TodoState)) {
            notifyObservers("Backlog item '" + title + "' has been moved back to Todo.");
        }
    }

    public void moveToNextState() {
        state.moveToNext(this);
    }

    public void revertToTodo() {
        state.revertToTodo(this);
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    
    public int getActivityCount() {
        return activities.size();
    }

    public boolean isDone() {
        if (!(state instanceof DoneState)) {
            return false;
        }
        for (Activity activity : activities) {
            if (!activity.isDone()) {
                return false;
            }
        }
        return true;
    }
}