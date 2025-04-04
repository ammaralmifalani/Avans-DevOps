package com.avans.state.review;

import com.avans.domain.project.ReviewSprint;

public class ReviewFinishedState implements IReviewSprintState {
    private static final String NAME = "Finished";

    @Override
    public void start(ReviewSprint sprint) {
        // Cannot start a finished sprint
        throw new UnsupportedOperationException("Cannot start a finished sprint.");
    }

    @Override
    public void finish(ReviewSprint sprint) {
        // Already finished, do nothing
        sprint.setState(new ReviewClosedState());
    }

    @Override
    public void performReview(ReviewSprint sprint) {
        // Cannot perform review on a finished sprint
        throw new UnsupportedOperationException("Cannot perform review on a finished sprint.");
    }

    @Override
    public String getName() {
        return NAME;
    }
    
}
