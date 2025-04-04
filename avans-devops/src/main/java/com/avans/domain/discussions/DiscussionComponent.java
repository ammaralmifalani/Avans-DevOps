package com.avans.domain.discussions;

import java.time.LocalDate;

import com.avans.domain.member.TeamMember;



public abstract class DiscussionComponent {
    public abstract void add(DiscussionComponent component);
    public abstract void remove(DiscussionComponent component);
    public abstract String getContent();
    public abstract TeamMember getAuthor();
    public abstract LocalDate getTimestamp();
    public abstract boolean isLocked();
    public abstract void lock();
    public abstract void unlock();
}
