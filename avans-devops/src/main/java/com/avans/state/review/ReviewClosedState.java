package com.avans.state.review;

import com.avans.domain.project.ReviewSprint;
public class ReviewClosedState implements IReviewSprintState {
    private static final String NAME = "Closed";
    @Override
    public void start(ReviewSprint sprint) {
        System.out.println("Cannot start a review sprint that is already closed.");
    }

    @Override
    public void finish(ReviewSprint sprint) {
        System.out.println("Cannot finish a review sprint that is already closed.");
    }

    @Override
    public void performReview(ReviewSprint sprint) {
        System.out.println("Cannot perform review on a review sprint that is already closed.");
    }

    @Override
    public String getName() {
        return NAME;
    }
    
}
