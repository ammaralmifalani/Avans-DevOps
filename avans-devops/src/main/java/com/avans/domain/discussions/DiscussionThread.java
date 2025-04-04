package com.avans.domain.discussions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.backlog.state.DoneState;
import com.avans.domain.member.TeamMember;
import com.avans.observer.Subject;

public class DiscussionThread extends DiscussionComponent {
    private String title;
    private LocalDate timestamp;
    private boolean locked;
    private List<DiscussionComponent> children;
    private BacklogItem backlogItem;
    private Subject notificationSubject;
    
    public DiscussionThread(String title) {
        this.title = title;
        this.timestamp = LocalDate.now();
        this.locked = false;
        this.children = new ArrayList<>();
        this.notificationSubject = new Subject() {}; // Anonymous subject for notifications
    }
    
    @Override
    public void add(DiscussionComponent component) {
        if (!isLocked()) {
            children.add(component);
            
            // Notify observers about the new message
            if (notificationSubject != null) {
                notificationSubject.notifyObservers("New message added to discussion: '" + title + "'");
            }
        } else {
            throw new IllegalStateException("Cannot add messages to a locked discussion thread");
        }
    }
    
    @Override
    public void remove(DiscussionComponent component) {
        if (!isLocked()) {
            children.remove(component);
        } else {
            throw new IllegalStateException("Cannot remove messages from a locked discussion thread");
        }
    }
    
    @Override
    public String getContent() {
        StringBuilder content = new StringBuilder(title);
        for (DiscussionComponent child : children) {
            content.append("\n").append(child.getContent());
        }
        return content.toString();
    }
    
    @Override
    public TeamMember getAuthor() {
        // A thread doesn't have a single author
        return null;
    }
    
    @Override
    public LocalDate getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean isLocked() {
        return locked;
    }
    
    @Override
    public void lock() {
        this.locked = true;
        // Lock all children too
        for (DiscussionComponent child : children) {
            child.lock();
        }
    }
    
    @Override
    public void unlock() {
        this.locked = false;
        // Unlock all children too
        for (DiscussionComponent child : children) {
            child.unlock();
        }
    }
    
    public void setBacklogItem(BacklogItem backlogItem) {
        this.backlogItem = backlogItem;
        
        // Update lock status based on backlog item state
        updateLockStatusBasedOnBacklogItem();
    }
    
    // Update lock status whenever backlog item state changes
    public void updateLockStatusBasedOnBacklogItem() {
        if (backlogItem != null && backlogItem.getState() instanceof DoneState) {
            lock();
        } else {
            unlock();
        }
    }
    
    public BacklogItem getBacklogItem() {
        return backlogItem;
    }
    
    public String getTitle() {
        return title;
    }
    
    // Methods to add/remove observers to the notification subject
    public void addObserver(com.avans.observer.IObserver observer) {
        notificationSubject.addObserver(observer);
    }
    
    public void removeObserver(com.avans.observer.IObserver observer) {
        notificationSubject.removeObserver(observer);
    }
}
