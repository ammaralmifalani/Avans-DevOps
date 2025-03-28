package com.avans.domain.project;

import java.time.LocalDate;

import com.avans.state.review.IReviewSprintState;
import com.avans.state.review.ReviewClosedState;
import com.avans.state.review.ReviewCreatedState;

public class ReviewSprint extends Sprint {
    private IReviewSprintState state;
    private Document reviewSummary;
    private boolean reviewCompleted;

    public ReviewSprint(String name, LocalDate startDate, LocalDate endDate) {
        super(name, startDate, endDate);
        this.state = new ReviewCreatedState();
        this.reviewCompleted = false;
    }

    public void uploadReviewSummary(Document doc) {
        if (!isFinished()) {
            throw new IllegalStateException("Cannot upload review summary before sprint is finished");
        }
        
        this.reviewSummary = doc;
        notifyObservers("Review summary uploaded for sprint '" + getName() + "'");
    }

    public Document getReviewSummary() {
        return reviewSummary;
    }

    public IReviewSprintState getState() {
        return state;
    }

    public void setState(IReviewSprintState newState) {
        IReviewSprintState oldState = this.state;
        this.state = newState;
        
        notifyObservers("Review sprint '" + getName() + "' state changed from " 
                + oldState.getName() + " to " + newState.getName());
    }
    
    public void startReview() {
        if (!isFinished()) {
            throw new IllegalStateException("Cannot start review before sprint is finished");
        }
        
        state.start(this);
    }
    
    public void performReview() {
        state.performReview(this);
    }
    
    public void finishReview() {
        if (reviewSummary == null) {
            throw new IllegalStateException("Cannot finish review without uploaded summary document");
        }
        
        state.finish(this);
        this.reviewCompleted = true;
        
        notifyObservers("Review for sprint '" + getName() + "' has been completed");
    }
    
    public boolean isReviewCompleted() {
        return reviewCompleted;
    }
    
    public void closeReview() {
        if (!reviewCompleted) {
            throw new IllegalStateException("Cannot close review sprint before review is completed");
        }
        
        // Close the sprint (change state to closed)
        setState(new ReviewClosedState());
        notifyObservers("Review sprint '" + getName() + "' has been closed");
    }
}
