package com.avans.domain.discussions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avans.domain.member.TeamMember;


public class DiscussionMessage extends DiscussionComponent {
    private String content;
    private TeamMember author;
    private LocalDate timestamp;
    private boolean locked;
    private List<DiscussionComponent> replies;
    
    public DiscussionMessage(String content, TeamMember author) {
        this.content = content;
        this.author = author;
        this.timestamp = LocalDate.now();
        this.locked = false;
        this.replies = new ArrayList<>();
    }
    
    @Override
    public void add(DiscussionComponent component) {
        if (!locked) {
            replies.add(component);
        } else {
            throw new UnsupportedOperationException("Cannot add components to a locked message");
        }
    }
    
    @Override
    public void remove(DiscussionComponent component) {
        if (!locked) {
            replies.remove(component);
        } else {
            throw new UnsupportedOperationException("Cannot remove components from a locked message");
        }
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public TeamMember getAuthor() {
        return author;
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
    }
    
    @Override
    public void unlock() {
        this.locked = false;
    }
}