package com.avans.state.release;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.avans.domain.project.ReleaseSprint;

@ExtendWith(MockitoExtension.class)
class ReleaseSprintStatesTest {

    @Mock
    private ReleaseSprint mockSprint;

    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private final String sprintTestName = "TestSprint"; // Define name once

    @BeforeEach
    void setUp() {
        // Capture System.out for states that print messages
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        // REMOVED STUBBING FROM HERE
    }

    @AfterEach
    void tearDown() {
        // Restore System.out
        System.setOut(originalOut);
    }

    // --- Test ReleaseCreatedState ---
    @Test
    @DisplayName("CreatedState: start() should transition to InProgressState")
    void createdStateStartTransitionsToInProgress() {
        when(mockSprint.getName()).thenReturn(sprintTestName); // Stubbing added here
        ReleaseCreatedState state = new ReleaseCreatedState();
        state.start(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleaseInProgressState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Starting the release process"));
    }

    @Test
    @DisplayName("CreatedState: other actions should print message")
    void createdStateInvalidActions() {
        ReleaseCreatedState state = new ReleaseCreatedState();
        state.performRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot perform release while in Created state"));
        outContent.reset();
        state.retryRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot retry release while in Created state"));
         outContent.reset();
        state.cancelRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot cancel release while in Created state"));
    }

    @Test @DisplayName("CreatedState: getName()") void createdStateGetName() { assertEquals("ReleaseCreatedState", new ReleaseCreatedState().getName()); }

    // --- Test ReleaseInProgressState ---
     @Test
    @DisplayName("InProgressState: performRelease() should transition to ReleasingState")
    void inProgressStatePerformReleaseTransitionsToReleasing() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseInProgressState state = new ReleaseInProgressState();
        state.performRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleasingState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Initiating release actions"));
    }

     @Test
    @DisplayName("InProgressState: retryRelease() should transition to ReleasingState")
    void inProgressStateRetryReleaseTransitionsToReleasing() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseInProgressState state = new ReleaseInProgressState();
        state.retryRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleasingState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Retrying release"));
        assertTrue(outContent.toString().contains("Initiating release actions"));
    }

     @Test
    @DisplayName("InProgressState: cancelRelease() should transition to CancelledState")
    void inProgressStateCancelReleaseTransitionsToCancelled() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseInProgressState state = new ReleaseInProgressState();
        state.cancelRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleaseCancelledState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Cancelling release in InProgress state"));
    }

    @Test
    @DisplayName("InProgressState: start() should print message")
    void inProgressStateInvalidActions() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseInProgressState state = new ReleaseInProgressState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Release already in progress"));
    }

    @Test @DisplayName("InProgressState: getName()") void inProgressStateGetName() { assertEquals("ReleaseInProgressState", new ReleaseInProgressState().getName()); }


    // --- Test ReleasingState ---
     @Test
    @DisplayName("ReleasingState: performRelease() should transition to FinishedState")
    void releasingStatePerformReleaseTransitionsToFinished() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleasingState state = new ReleasingState();
        state.performRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleaseFinishedState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Executing release pipeline"));
    }

     @Test
    @DisplayName("ReleasingState: cancelRelease() should transition to CancelledState")
    void releasingStateCancelReleaseTransitionsToCancelled() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleasingState state = new ReleasingState();
        state.cancelRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleaseCancelledState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Cancelling release during releasing phase"));
    }

    @Test
    @DisplayName("ReleasingState: start() and retry() should print message")
    void releasingStateInvalidActions() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleasingState state = new ReleasingState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("already in the releasing phase"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.retryRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot retry while release is in releasing phase"));
    }

     @Test @DisplayName("ReleasingState: getName()") void releasingStateGetName() { assertEquals("ReleasingState", new ReleasingState().getName()); }


    // --- Test ReleaseFinishedState ---
     @Test
    @DisplayName("FinishedState: performRelease() should transition to ReleasedState")
    void finishedStatePerformReleaseTransitionsToReleased() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseFinishedState state = new ReleaseFinishedState();
        state.performRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleasedState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Finalizing release"));
    }

     @Test
    @DisplayName("FinishedState: cancelRelease() should transition to CancelledState")
    void finishedStateCancelReleaseTransitionsToCancelled() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseFinishedState state = new ReleaseFinishedState();
        state.cancelRelease(mockSprint);
        ArgumentCaptor<IReleaseSprintState> stateCaptor = ArgumentCaptor.forClass(IReleaseSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReleaseCancelledState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Cancelling release in Finished state"));
    }

    @Test
    @DisplayName("FinishedState: start() and retry() should print message")
    void finishedStateInvalidActions() {
        when(mockSprint.getName()).thenReturn(sprintTestName); // Needed for start() message
        ReleaseFinishedState state = new ReleaseFinishedState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Release is already finished"));
        outContent.reset();
        // when(mockSprint.getName()).thenReturn(sprintTestName); // REMOVED - Not needed for retry() message
        state.retryRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot retry in Finished state"));
    }

    @Test @DisplayName("FinishedState: getName()") void finishedStateGetName() { assertEquals("ReleaseFinishedState", new ReleaseFinishedState().getName()); }


    // --- Test ReleasedState ---
    @Test
    @DisplayName("ReleasedState: all actions should print message")
    void releasedStateInvalidActions() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleasedState state = new ReleasedState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Sprint already released"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.performRelease(mockSprint);
        assertTrue(outContent.toString().contains("Sprint already released"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.retryRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot retry release; sprint already released"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.cancelRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot cancel release; sprint already released"));
    }

    @Test @DisplayName("ReleasedState: getName()") void releasedStateGetName() { assertEquals("ReleasedState", new ReleasedState().getName()); }


    // --- Test ReleaseCancelledState ---
     @Test
    @DisplayName("CancelledState: all actions should print message")
    void cancelledStateInvalidActions() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseCancelledState state = new ReleaseCancelledState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Release has been cancelled"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.performRelease(mockSprint);
        assertTrue(outContent.toString().contains("Release has been cancelled"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.retryRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot retry; release has been cancelled"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.cancelRelease(mockSprint);
        assertTrue(outContent.toString().contains("Release is already cancelled"));
    }

    @Test @DisplayName("CancelledState: getName()") void cancelledStateGetName() { assertEquals("ReleaseCancelledState", new ReleaseCancelledState().getName()); }


    // --- Test ReleaseClosedState ---
     @Test
    @DisplayName("ClosedState: all actions should print message")
    void closedStateInvalidActions() {
        when(mockSprint.getName()).thenReturn(sprintTestName);
        ReleaseClosedState state = new ReleaseClosedState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Release is closed"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.performRelease(mockSprint);
        assertTrue(outContent.toString().contains("Release is closed"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.retryRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot retry; release is closed"));
        outContent.reset();
        when(mockSprint.getName()).thenReturn(sprintTestName);
        state.cancelRelease(mockSprint);
        assertTrue(outContent.toString().contains("Cannot cancel; release is closed"));
    }

     @Test @DisplayName("ClosedState: getName()") void closedStateGetName() { assertEquals("ReleaseClosedState", new ReleaseClosedState().getName()); }
}
