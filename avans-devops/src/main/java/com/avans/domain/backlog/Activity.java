package com.avans.domain.backlog;

public class Activity {
    private String title;
    private boolean done;
    private int estimatedHours;

    public Activity(String title, int estimatedHours) {
        this.title = title;
        this.estimatedHours = estimatedHours;
        this.done = false;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getTotalEstimatedHours() {
        return estimatedHours;
    }

    public String getTitle() {
        return title;
    }
}