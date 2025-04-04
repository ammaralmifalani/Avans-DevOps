package com.avans.state.review;

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

import com.avans.domain.project.ReviewSprint;

@ExtendWith(MockitoExtension.class)
class ReviewSprintStatesTest {

    @Mock
    private ReviewSprint mockSprint;

    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    // No sprint name needed as messages don't use it

    @BeforeEach
    void setUp() {
        // Capture System.out for states that print messages
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Restore System.out
        System.setOut(originalOut);
    }

    // --- Test ReviewCreatedState ---
    @Test
    @DisplayName("CreatedState: start() should transition to InProgressState")
    void createdStateStartTransitionsToInProgress() {
        ReviewCreatedState state = new ReviewCreatedState();
        state.start(mockSprint);
        ArgumentCaptor<IReviewSprintState> stateCaptor = ArgumentCaptor.forClass(IReviewSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReviewInProgressState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Review sprint started"));
    }

    @Test
    @DisplayName("CreatedState: finish() and performReview() should print message")
    void createdStateInvalidActions() {
        ReviewCreatedState state = new ReviewCreatedState();
        state.finish(mockSprint);
        assertTrue(outContent.toString().contains("Cannot finish a review sprint that has not started yet"));
        outContent.reset(); // Clear output
        state.performReview(mockSprint);
        assertTrue(outContent.toString().contains("Cannot perform review in a created state"));
    }

    @Test @DisplayName("CreatedState: getName()") void createdStateGetName() { assertEquals("Created", new ReviewCreatedState().getName()); }

    // --- Test ReviewInProgressState ---
    @Test
    @DisplayName("InProgressState: finish() should transition to ReviewedState")
    void inProgressStateFinishTransitionsToReviewed() {
        ReviewInProgressState state = new ReviewInProgressState();
        state.finish(mockSprint);
        ArgumentCaptor<IReviewSprintState> stateCaptor = ArgumentCaptor.forClass(IReviewSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReviewedState.class, stateCaptor.getValue());
        assertTrue(outContent.toString().contains("Sprint finished. Moving to finished state")); // Message from original code
    }

     @Test
    @DisplayName("InProgressState: performReview() should print message")
    void inProgressStatePerformReview() {
        ReviewInProgressState state = new ReviewInProgressState();
        state.performReview(mockSprint);
        assertTrue(outContent.toString().contains("Performing review for the sprint"));
        // No state transition occurs here in the current implementation
        verify(mockSprint, never()).setState(any(IReviewSprintState.class));
    }


    @Test
    @DisplayName("InProgressState: start() should print message")
    void inProgressStateInvalidActions() {
        ReviewInProgressState state = new ReviewInProgressState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Cannot start a sprint that is already in progress"));
    }

    @Test @DisplayName("InProgressState: getName()") void inProgressStateGetName() { assertEquals("In Progress", new ReviewInProgressState().getName()); }


    // --- Test ReviewedState ---
    @Test
    @DisplayName("ReviewedState: start(), finish(), performReview() should print message")
    void reviewedStateInvalidActions() {
        ReviewedState state = new ReviewedState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Cannot start a sprint that is already reviewed"));
        outContent.reset();
        state.finish(mockSprint);
         assertTrue(outContent.toString().contains("Cannot finish a sprint that is already reviewed"));
        outContent.reset();
        state.performReview(mockSprint);
         assertTrue(outContent.toString().contains("Sprint is already in the reviewed state"));
        // No state transitions should occur
         verify(mockSprint, never()).setState(any(IReviewSprintState.class));
    }

    @Test @DisplayName("ReviewedState: getName()") void reviewedStateGetName() { assertEquals("Reviewed", new ReviewedState().getName()); }


    // --- Test ReviewFinishedState ---
    @Test
    @DisplayName("FinishedState: finish() should transition to ClosedState")
    void finishedStateFinishTransitionsToClosed() {
        // Note: The original code for ReviewFinishedState.finish() sets state to Closed.
        ReviewFinishedState state = new ReviewFinishedState();
        state.finish(mockSprint);
        ArgumentCaptor<IReviewSprintState> stateCaptor = ArgumentCaptor.forClass(IReviewSprintState.class);
        verify(mockSprint).setState(stateCaptor.capture());
        assertInstanceOf(ReviewClosedState.class, stateCaptor.getValue());
    }

    @Test
    @DisplayName("FinishedState: start() and performReview() should throw exception")
    void finishedStateInvalidActionsThrowException() {
        ReviewFinishedState state = new ReviewFinishedState();
        assertThrows(UnsupportedOperationException.class, () -> state.start(mockSprint));
        assertThrows(UnsupportedOperationException.class, () -> state.performReview(mockSprint));
    }

    @Test @DisplayName("FinishedState: getName()") void finishedStateGetName() { assertEquals("Finished", new ReviewFinishedState().getName()); }


    // --- Test ReviewClosedState ---
    @Test
    @DisplayName("ClosedState: start(), finish(), performReview() should print message")
    void closedStateInvalidActions() {
        ReviewClosedState state = new ReviewClosedState();
        state.start(mockSprint);
        assertTrue(outContent.toString().contains("Cannot start a review sprint that is already closed"));
        outContent.reset();
        state.finish(mockSprint);
        assertTrue(outContent.toString().contains("Cannot finish a review sprint that is already closed"));
        outContent.reset();
        state.performReview(mockSprint);
        assertTrue(outContent.toString().contains("Cannot perform review on a review sprint that is already closed"));
         // No state transitions should occur
         verify(mockSprint, never()).setState(any(IReviewSprintState.class));
    }

    @Test @DisplayName("ClosedState: getName()") void closedStateGetName() { assertEquals("Closed", new ReviewClosedState().getName()); }
}
