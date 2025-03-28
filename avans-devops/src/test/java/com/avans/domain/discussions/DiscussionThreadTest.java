package com.avans.domain.discussions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.backlog.state.DoneState;
import com.avans.domain.backlog.state.TodoState;
import com.avans.domain.member.Developer;
import com.avans.observer.IObserver;

@ExtendWith(MockitoExtension.class)
class DiscussionThreadTest {

    private DiscussionThread discussionThread;
    
    @Mock
    private BacklogItem backlogItem;
    
    @Mock
    private Developer author;
    
    @Mock
    private IObserver observer;

    @BeforeEach
    void setUp() {
        discussionThread = new DiscussionThread("Test Discussion");
        discussionThread.addObserver(observer);
    }

    @Test
    @DisplayName("Discussion thread should allow adding messages when not locked")
    void shouldAllowAddingMessagesWhenUnlocked() {
        DiscussionMessage message = new DiscussionMessage("Test message", author);
        discussionThread.add(message);
        
        assertTrue(discussionThread.getContent().contains("Test message"));
        verify(observer).update(contains("New message added"));
    }

    @Test
    @DisplayName("Discussion thread should not allow adding messages when locked")
    void shouldNotAllowAddingMessagesWhenLocked() {
        discussionThread.lock();
        
        DiscussionMessage message = new DiscussionMessage("Test message", author);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            discussionThread.add(message);
        });
        
        assertTrue(exception.getMessage().contains("Cannot add messages to a locked discussion thread"));
    }

    @Test
    @DisplayName("Discussion thread should be locked when backlog item is done")
    void shouldBeLockWhenBacklogItemIsDone() {
        // Setup backlog item with Done state
        when(backlogItem.getState()).thenReturn(new DoneState());
        
        discussionThread.setBacklogItem(backlogItem);
        discussionThread.updateLockStatusBasedOnBacklogItem();
        
        assertTrue(discussionThread.isLocked());
    }

    @Test
    @DisplayName("Discussion thread should be unlocked when backlog item is not done")
    void shouldBeUnlockedWhenBacklogItemIsNotDone() {
        // Setup backlog item with non-Done state
        when(backlogItem.getState()).thenReturn(new TodoState());
        
        // First lock it
        discussionThread.lock();
        assertTrue(discussionThread.isLocked());
        
        // Then attach backlog item and update
        discussionThread.setBacklogItem(backlogItem);
        discussionThread.updateLockStatusBasedOnBacklogItem();
        
        assertFalse(discussionThread.isLocked());
    }

    @Test
    @DisplayName("Discussion thread locking should cascade to all children")
    void lockingShouldCascadeToChildren() {
        DiscussionMessage message1 = new DiscussionMessage("Message 1", author);
        DiscussionMessage message2 = new DiscussionMessage("Message 2", author);
        
        discussionThread.add(message1);
        discussionThread.add(message2);
        
        discussionThread.lock();
        
        assertTrue(discussionThread.isLocked());
        assertTrue(message1.isLocked());
        assertTrue(message2.isLocked());
    }

    @Test
    @DisplayName("Discussion thread unlocking should cascade to all children")
    void unlockingShouldCascadeToChildren() {
        DiscussionMessage message1 = new DiscussionMessage("Message 1", author);
        DiscussionMessage message2 = new DiscussionMessage("Message 2", author);
        
        discussionThread.add(message1);
        discussionThread.add(message2);
        
        // First lock everything
        discussionThread.lock();
        assertTrue(message1.isLocked());
        assertTrue(message2.isLocked());
        
        // Then unlock
        discussionThread.unlock();
        
        assertFalse(discussionThread.isLocked());
        assertFalse(message1.isLocked());
        assertFalse(message2.isLocked());
    }
}
