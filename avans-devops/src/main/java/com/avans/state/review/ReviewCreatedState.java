package com.avans.state.review;

import com.avans.domain.project.ReviewSprint;

public class ReviewCreatedState implements IReviewSprintState {
    public static final String NAME = "Created";
    @Override
    public void start(ReviewSprint sprint) {
        sprint.setState(new ReviewInProgressState());
        System.out.println("Review sprint started.");
    }

    @Override
    public void finish(ReviewSprint sprint) {
        System.out.println("Cannot finish a review sprint that has not started yet.");
    }

    @Override
    public void performReview(ReviewSprint sprint) {
        System.out.println("Cannot perform review in a created state. Please start the review sprint first.");
    }

    @Override
    public String getName() {
        return NAME;
    }

}
