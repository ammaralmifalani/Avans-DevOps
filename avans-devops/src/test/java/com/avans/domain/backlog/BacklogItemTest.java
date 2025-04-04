package com.avans.domain.backlog;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.domain.backlog.state.DoingState;
import com.avans.domain.backlog.state.DoneState;
import com.avans.domain.backlog.state.ReadyForTestingState;
import com.avans.domain.backlog.state.TestedState;
import com.avans.domain.backlog.state.TestingState;
import com.avans.domain.backlog.state.TodoState;
import com.avans.domain.member.Developer;
import com.avans.domain.member.TeamMember;
import com.avans.observer.IObserver;

@ExtendWith(MockitoExtension.class)
class BacklogItemTest {

    private BacklogItem backlogItem;
    
    @Mock
    private IObserver observer;
    
    @Mock
    private Developer developer;

    @BeforeEach
    void setUp() {
        backlogItem = new BacklogItem("Test Item");
        backlogItem.addObserver(observer);
        
        // Removed unnecessary stubbing from setUp - will add where needed
    }

    @Test
    @DisplayName("BacklogItem initial state should be Todo")
    void initialStateShouldBeTodo() {
        assertTrue(backlogItem.getState() instanceof TodoState);
        assertEquals("Todo", backlogItem.getState().getName());
    }

    @Test
    @DisplayName("BacklogItem should move through all states correctly")
    void shouldMoveToNextState() {
        // Initial state: Todo
        assertTrue(backlogItem.getState() instanceof TodoState);
        
        // Move to Doing
        backlogItem.moveToNextState();
        assertTrue(backlogItem.getState() instanceof DoingState);
        
        // Move to ReadyForTesting - should notify observers
        backlogItem.moveToNextState();
        assertTrue(backlogItem.getState() instanceof ReadyForTestingState);
        verify(observer).update(contains("ready for testing"));
        
        // Move to Testing
        backlogItem.moveToNextState();
        assertTrue(backlogItem.getState() instanceof TestingState);
        
        // Move to Tested
        backlogItem.moveToNextState();
        assertTrue(backlogItem.getState() instanceof TestedState);
        
        // Add activity and mark it as done before moving to Done
        Activity activity = new Activity("Test Activity", 2);
        activity.setDone(true);
        backlogItem.addActivity(activity);
        
        // Move to Done
        backlogItem.moveToNextState();
        assertTrue(backlogItem.getState() instanceof DoneState);
    }

    @Test
    @DisplayName("BacklogItem should fail to move to Done if activities are not done")
    void shouldFailToMoveToDoneIfActivitiesNotDone() {
        // Move to Tested state
        backlogItem.moveToNextState(); // Todo -> Doing
        backlogItem.moveToNextState(); // Doing -> ReadyForTesting
        backlogItem.moveToNextState(); // ReadyForTesting -> Testing
        backlogItem.moveToNextState(); // Testing -> Tested
        
        // Add activity that is not done
        Activity activity = new Activity("Incomplete Activity", 2);
        backlogItem.addActivity(activity);
        
        // Try to move to Done - should throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            backlogItem.moveToNextState();
        });
        
        assertTrue(exception.getMessage().contains("Cannot move to Done state"));
        assertTrue(backlogItem.getState() instanceof TestedState); // State should not change
    }

    @Test
    @DisplayName("BacklogItem should revert to Todo from any state")
    void shouldRevertToTodo() {
        // Move to Doing
        backlogItem.moveToNextState();
        assertTrue(backlogItem.getState() instanceof DoingState);
        
        // Revert to Todo
        backlogItem.revertToTodo();
        assertTrue(backlogItem.getState() instanceof TodoState);
        
        // Move to Testing
        backlogItem.moveToNextState(); // Todo -> Doing
        backlogItem.moveToNextState(); // Doing -> ReadyForTesting
        backlogItem.moveToNextState(); // ReadyForTesting -> Testing
        assertTrue(backlogItem.getState() instanceof TestingState);
        
        // Revert to Todo
        backlogItem.revertToTodo();
        assertTrue(backlogItem.getState() instanceof TodoState);
        verify(observer, atLeastOnce()).update(contains("moved back to Todo"));
    }

    @Test
    @DisplayName("BacklogItem should only allow one developer to be assigned")
    void shouldOnlyAllowOneDeveloper() {
        // Setup the mock developer only in this test where it's needed
        when(developer.getName()).thenReturn("Test Developer");
        
        backlogItem.assignDeveloper(developer);
        assertEquals(developer, backlogItem.getAssignedDeveloper());
        verify(observer).update(contains("Developer Test Developer assigned"));
        
        // Try to assign a second developer
        TeamMember anotherDeveloper = mock(Developer.class);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            backlogItem.assignDeveloper(anotherDeveloper);
        });
        
        assertTrue(exception.getMessage().contains("already has an assigned developer"));
        assertEquals(developer, backlogItem.getAssignedDeveloper()); // Original developer still assigned
    }

    @Test
    @DisplayName("BacklogItem should track completion of all activities")
    void shouldTrackActivityCompletion() {
        Activity activity1 = new Activity("Activity 1", 2);
        Activity activity2 = new Activity("Activity 2", 3);
        
        backlogItem.addActivity(activity1);
        backlogItem.addActivity(activity2);
        
        assertFalse(backlogItem.areAllActivitiesDone());
        
        activity1.setDone(true);
        assertFalse(backlogItem.areAllActivitiesDone());
        
        activity2.setDone(true);
        assertTrue(backlogItem.areAllActivitiesDone());
    }

    @Test
    @DisplayName("BacklogItem is not done unless in DoneState and all activities are done")
    void isDoneShouldCheckStateAndActivities() {
        // Add activities
        Activity activity1 = new Activity("Activity 1", 2);
        activity1.setDone(true);
        Activity activity2 = new Activity("Activity 2", 3);
        activity2.setDone(true);
        
        backlogItem.addActivity(activity1);
        backlogItem.addActivity(activity2);
        
        // Not in Done state yet
        assertFalse(backlogItem.isDone());
        
        // Move to Done state
        backlogItem.moveToNextState(); // Todo -> Doing
        backlogItem.moveToNextState(); // Doing -> ReadyForTesting
        backlogItem.moveToNextState(); // ReadyForTesting -> Testing
        backlogItem.moveToNextState(); // Testing -> Tested
        backlogItem.moveToNextState(); // Tested -> Done
        
        // Now should be done
        assertTrue(backlogItem.isDone());
        
        // Create a new BacklogItem in Done state but with incomplete activities
        BacklogItem anotherItem = new BacklogItem("Another Item");
        Activity incompleteActivity = new Activity("Incomplete", 1);
        anotherItem.addActivity(incompleteActivity);
        
        // Move to Done state
        anotherItem.moveToNextState(); // Todo -> Doing
        anotherItem.moveToNextState(); // Doing -> ReadyForTesting
        anotherItem.moveToNextState(); // ReadyForTesting -> Testing
        anotherItem.moveToNextState(); // Testing -> Tested
        
        // Should fail to move to Done state
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            anotherItem.moveToNextState();
        });
        
        assertTrue(exception.getMessage().contains("Cannot move to Done state"));
    }
}
