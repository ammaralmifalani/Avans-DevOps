package com.avans.state.review;

import com.avans.domain.project.ReviewSprint;

public class ReviewInProgressState implements IReviewSprintState {
    private static final String NAME = "In Progress";

    @Override
    public void start(ReviewSprint sprint) {
        // Cannot start an already started sprint
        System.out.println("Cannot start a sprint that is already in progress.");
    }

    @Override
    public void finish(ReviewSprint sprint) {
        sprint.setState(new ReviewedState());
        System.out.println("Sprint finished. Moving to finished state.");
    }

    @Override
    public void performReview(ReviewSprint sprint) {
        // Perform review logic here
        System.out.println("Performing review for the sprint.");
    }

    @Override
    public String getName() {
        return NAME;
    }

}
