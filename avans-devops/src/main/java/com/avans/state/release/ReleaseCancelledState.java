package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;

public class ReleaseCancelledState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Release has been cancelled for sprint: " + sprint.getName());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Release has been cancelled for sprint: " + sprint.getName());
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Cannot retry; release has been cancelled for sprint: " + sprint.getName());
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Release is already cancelled for sprint: " + sprint.getName());
    }

    @Override
    public String getName() {
        return "ReleaseCancelledState";
    }
}