package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;

public class ReleaseClosedState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Release is closed for sprint: " + sprint.getName());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Release is closed for sprint: " + sprint.getName());
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Cannot retry; release is closed for sprint: " + sprint.getName());
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Cannot cancel; release is closed for sprint: " + sprint.getName());
    }

    @Override
    public String getName() {
        return "ReleaseClosedState";
    }
}
