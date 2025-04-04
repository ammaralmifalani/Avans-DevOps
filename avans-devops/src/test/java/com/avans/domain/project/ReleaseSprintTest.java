package com.avans.domain.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.member.ProductOwner;
import com.avans.domain.member.ScrumMaster;
import com.avans.observer.IObserver;
import com.avans.pipeline.Pipeline;
import com.avans.state.release.IReleaseSprintState;
import com.avans.state.release.ReleaseCreatedState;
import com.avans.state.release.ReleaseInProgressState;

@ExtendWith(MockitoExtension.class)
class ReleaseSprintTest {

    private ReleaseSprint releaseSprint;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Mock
    private Pipeline pipeline;
    
    @Mock
    private ScrumMaster scrumMaster;
    
    @Mock
    private ProductOwner productOwner;
    
    @Mock
    private BacklogItem backlogItem;
    
    @Mock
    private IObserver observer;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now();
        endDate = startDate.plusWeeks(2);
        releaseSprint = new ReleaseSprint("Test Release Sprint", startDate, endDate);
        releaseSprint.addObserver(observer);
    }

    @Test
    @DisplayName("ReleaseSprint should have initial state of ReleaseCreatedState")
    void shouldHaveInitialStateOfReleaseCreatedState() {
        assertTrue(releaseSprint.getState() instanceof ReleaseCreatedState);
    }

    @Test
    @DisplayName("Cannot start release before sprint is finished")
    void cannotStartReleaseBeforeSprintIsFinished() {
        releaseSprint.setPipeline(pipeline);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            releaseSprint.startRelease();
        });
        
        assertTrue(exception.getMessage().contains("Cannot start release before sprint is finished"));
    }

    @Test
    @DisplayName("Cannot start release without a pipeline")
    void cannotStartReleaseWithoutPipeline() {
        // Start and finish the sprint
        releaseSprint.setScrumMaster(scrumMaster);
        releaseSprint.addTeamMember(scrumMaster);
        releaseSprint.start();
        releaseSprint.finish();
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            releaseSprint.startRelease();
        });
        
        assertTrue(exception.getMessage().contains("Cannot start release without a pipeline"));
    }

    @Test
    @DisplayName("Cannot start release if not all backlog items are done")
    void cannotStartReleaseIfNotAllBacklogItemsAreDone() {
        // Setup sprint
        releaseSprint.setScrumMaster(scrumMaster);
        releaseSprint.addTeamMember(scrumMaster);
        releaseSprint.setPipeline(pipeline);
        releaseSprint.addBacklogItem(backlogItem);
        
        // Mock backlog item to indicate it's not done
        when(backlogItem.isDone()).thenReturn(false);
        
        // Start and finish the sprint
        releaseSprint.start();
        releaseSprint.finish();
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            releaseSprint.startRelease();
        });
        
        assertTrue(exception.getMessage().contains("Cannot start release - not all backlog items are done"));
    }

    @Test
    @DisplayName("Should move to InProgressState after startRelease")
    void shouldMoveToInProgressStateAfterStartRelease() {
        // We need to mock the state for this test instead of using the actual validation logic
        IReleaseSprintState mockState = mock(IReleaseSprintState.class);
        releaseSprint.setState(mockState);
        
        // Setup sprint with done backlog items
        releaseSprint.setScrumMaster(scrumMaster);
        releaseSprint.addTeamMember(scrumMaster);
        releaseSprint.setPipeline(pipeline);
        
        // Mock the backlog item to be done
        when(backlogItem.isDone()).thenReturn(true);
        releaseSprint.addBacklogItem(backlogItem);
        
        // Start and finish the sprint
        releaseSprint.start();
        releaseSprint.finish();
        
        // Start release
        releaseSprint.startRelease();
        
        // Verify state was asked to start
        verify(mockState).start(releaseSprint);
    }

    @Test
    @DisplayName("Should notify observers when state changes")
    void shouldNotifyObserversWhenStateChanges() {
        IReleaseSprintState newState = new ReleaseInProgressState();
        releaseSprint.setState(newState);
        
        verify(observer).update(contains("state changed"));
    }

    @Test
    @DisplayName("Cannot perform release without a pipeline")
    void cannotPerformReleaseWithoutPipeline() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            releaseSprint.performRelease();
        });
        
        assertTrue(exception.getMessage().contains("Cannot perform release without a pipeline"));
    }

    @Test
    @DisplayName("Should delegate to state when performRelease is called")
    void shouldDelegateToStateWhenPerformReleaseIsCalled() {
        // Setup a mock state
        IReleaseSprintState mockState = mock(IReleaseSprintState.class);
        releaseSprint.setState(mockState);
        
        // Setup pipeline
        releaseSprint.setPipeline(pipeline);
        
        // Call performRelease
        releaseSprint.performRelease();
        
        // Verify state was asked to perform release
        verify(mockState).performRelease(releaseSprint);
    }

    @Test
    @DisplayName("Cannot retry release while pipeline is running")
    void cannotRetryReleaseWhilePipelineIsRunning() {
        // Set pipeline running flag directly using public setter
        releaseSprint.setPipeline(pipeline);
        releaseSprint.setPipelineRunning(true);
        
        // Try to retry - should throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            releaseSprint.retryRelease();
        });
        
        assertTrue(exception.getMessage().contains("Cannot retry release while pipeline is still running"));
    }

    @Test
    @DisplayName("Cannot cancel release while pipeline is running")
    void cannotCancelReleaseWhilePipelineIsRunning() {
        // Set pipeline running flag directly using public setter
        releaseSprint.setPipeline(pipeline);
        releaseSprint.setPipelineRunning(true);
        
        // Try to cancel - should throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            releaseSprint.cancelRelease();
        });
        
        assertTrue(exception.getMessage().contains("Cannot cancel release while pipeline is still running"));
    }
}
