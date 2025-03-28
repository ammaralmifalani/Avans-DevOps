package com.avans.domain.backlog;
import  java.util.ArrayList;
import  java.util.List;

import com.avans.domain.backlog.state.DoingState;
import com.avans.domain.backlog.state.DoneState;
import com.avans.domain.backlog.state.IBacklogState;
import com.avans.domain.backlog.state.ReadyForTestingState;
import com.avans.domain.backlog.state.TodoState;
import com.avans.domain.member.Developer;
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
        
        // Check if all activities are completed before considering the item done
        for (Activity activity : activities) {
            if (!activity.isDone()) {
                return false;
            }
        }
        return true;
    }
    
    // Check if all activities are completed
    public boolean areAllActivitiesDone() {
        if (activities.isEmpty()) {
            return true; // If no activities, consider them all done
        }
        
        for (Activity activity : activities) {
            if (!activity.isDone()) {
                return false;
            }
        }
        return true;
    }
    
    // Assign a developer to this backlog item
    public void assignDeveloper(TeamMember developer) {
        if (this.assignedDeveloper != null) {
            throw new IllegalStateException("This backlog item already has an assigned developer. Create activities for multiple developers.");
        }
        
        this.assignedDeveloper = developer;
        notifyObservers("Developer " + developer.getName() + " assigned to backlog item '" + title + "'.");
    }
    
    public TeamMember getAssignedDeveloper() {
        return assignedDeveloper;
    }
    
    public List<Activity> getActivities() {
        return new ArrayList<>(activities); // Return a copy to preserve encapsulation
    }
}
