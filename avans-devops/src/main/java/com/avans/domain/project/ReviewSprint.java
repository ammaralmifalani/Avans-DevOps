package com.avans.domain.project;

import java.time.LocalDate;

import com.avans.state.review.IReviewSprintState;
import com.avans.state.review.ReviewCreatedState;

public class ReviewSprint extends Sprint {
    private IReviewSprintState state;
    private Document reviewSummary;

    public ReviewSprint(String name, LocalDate startDate, LocalDate endDate) {
        super(name, startDate, endDate);
        this.state = new ReviewCreatedState();
    }

    public void uploadReviewSummary(Document doc) {
        this.reviewSummary = doc;
    }

    public Document getReviewSummary() {
        return reviewSummary;
    }

    public IReviewSprintState getState() {
        return state;
    }

    public void setState(IReviewSprintState newState) {
        this.state = newState;
    }
}