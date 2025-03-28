package com.avans.state.review;

import com.avans.domain.project.ReviewSprint;

public class ReviewedState implements IReviewSprintState {
    private static final String STATE_NAME = "Reviewed";

    @Override
    public void start(ReviewSprint sprint) {
        // Cannot start a sprint that is already reviewed
        System.out.println("Cannot start a sprint that is already reviewed.");
    }

    @Override
    public void finish(ReviewSprint sprint) {
        // Cannot finish a sprint that is already reviewed
        System.out.println("Cannot finish a sprint that is already reviewed.");
    }

    @Override
    public void performReview(ReviewSprint sprint) {
        // Already in the reviewed state, no action needed
        System.out.println("Sprint is already in the reviewed state.");
    }

    @Override
    public String getName() {
        return STATE_NAME;
    }
}
