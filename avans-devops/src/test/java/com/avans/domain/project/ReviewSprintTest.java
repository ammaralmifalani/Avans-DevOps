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

import com.avans.domain.member.ScrumMaster;
import com.avans.observer.IObserver;
import com.avans.state.review.IReviewSprintState;
import com.avans.state.review.ReviewCreatedState;
import com.avans.state.review.ReviewInProgressState;

@ExtendWith(MockitoExtension.class)
class ReviewSprintTest {

    private ReviewSprint reviewSprint;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Mock
    private Document mockDocument;
    
    @Mock
    private ScrumMaster mockScrumMaster;
    
    @Mock
    private IObserver observer;
    
    @BeforeEach
    void setUp() {
        startDate = LocalDate.now();
        endDate = startDate.plusWeeks(2);
        reviewSprint = new ReviewSprint("Test Review Sprint", startDate, endDate);
        reviewSprint.addObserver(observer);
    }
    
    @Test
    @DisplayName("ReviewSprint should have initial state of ReviewCreatedState")
    void shouldHaveInitialStateOfReviewCreatedState() {
        assertTrue(reviewSprint.getState() instanceof ReviewCreatedState);
    }
    
    @Test
    @DisplayName("Cannot upload review summary before sprint is finished")
    void cannotUploadReviewSummaryBeforeSprintIsFinished() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            reviewSprint.uploadReviewSummary(mockDocument);
        });
        
        assertTrue(exception.getMessage().contains("Cannot upload review summary before sprint is finished"));
    }
    
    @Test
    @DisplayName("Should notify observers when state changes")
    void shouldNotifyObserversWhenStateChanges() {
        IReviewSprintState newState = new ReviewInProgressState();
        reviewSprint.setState(newState);
        
        verify(observer).update(contains("state changed"));
    }
    
    @Test
    @DisplayName("Cannot finish review without uploaded summary document")
    void cannotFinishReviewWithoutUploadedSummaryDocument() {
        // Start the sprint
        reviewSprint.setScrumMaster(mockScrumMaster);
        reviewSprint.addTeamMember(mockScrumMaster);
        reviewSprint.start();
        reviewSprint.finish();
        
        reviewSprint.startReview();
        reviewSprint.performReview();
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            reviewSprint.finishReview();
        });
        
        assertTrue(exception.getMessage().contains("Cannot finish review without uploaded summary document"));
    }
    
    @Test
    @DisplayName("Cannot close review before review is completed")
    void cannotCloseReviewBeforeReviewIsCompleted() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            reviewSprint.closeReview();
        });
        
        assertTrue(exception.getMessage().contains("Cannot close review sprint before review is completed"));
    }
    
    @Test
    @DisplayName("Should be able to complete full review lifecycle")
    void shouldBeAbleToCompleteFullReviewLifecycle() {
        // Setup
        reviewSprint.setScrumMaster(mockScrumMaster);
        reviewSprint.addTeamMember(mockScrumMaster);
        
        // 1. Start the sprint
        reviewSprint.start();
        assertTrue(reviewSprint.isStarted());
        
        // 2. Finish the sprint
        reviewSprint.finish();
        assertTrue(reviewSprint.isFinished());
        
        // 3. Start the review
        reviewSprint.startReview();
        
        // 4. Perform the review
        reviewSprint.performReview();
        
        // 5. Upload review summary
        reviewSprint.uploadReviewSummary(mockDocument);
        assertSame(mockDocument, reviewSprint.getReviewSummary());
        
        // 6. Finish the review
        reviewSprint.finishReview();
        assertTrue(reviewSprint.isReviewCompleted());
        
        // 7. Close the review sprint
        reviewSprint.closeReview();
        
        // Observers should be notified multiple times
        verify(observer, atLeast(3)).update(anyString());
    }
}
