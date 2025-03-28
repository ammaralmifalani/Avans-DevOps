package com.avans.domain.discussions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.member.TeamMember;

public class DiscussionThread extends DiscussionComponent {
    private String title;
    private LocalDate timestamp;
    private boolean locked;
    private List<DiscussionComponent> children;
    private BacklogItem backlogItem;
    
    public DiscussionThread(String title) {
        this.title = title;
        this.timestamp = LocalDate.now();
        this.locked = false;
        this.children = new ArrayList<>();
    }
    
    @Override
    public void add(DiscussionComponent component) {
        if (!isLocked()) {
            children.add(component);
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
    }
    
    public BacklogItem getBacklogItem() {
        return backlogItem;
    }
    
    public String getTitle() {
        return title;
    }
}