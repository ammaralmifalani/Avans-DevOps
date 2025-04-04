package com.avans.state.review;

import com.avans.domain.project.ReviewSprint;


public interface IReviewSprintState {
    void start(ReviewSprint sprint);
    void finish(ReviewSprint sprint);
    void performReview(ReviewSprint sprint);
    String getName();
}