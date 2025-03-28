package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;

public class ReleaseCreatedState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Starting the release process for sprint: " + sprint.getName());
        // Overgang naar InProgress state
        sprint.setState(new ReleaseInProgressState());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Cannot perform release while in Created state. Start the release first.");
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Cannot retry release while in Created state.");
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Cannot cancel release while in Created state.");
    }

    @Override
    public String getName() {
        return "ReleaseCreatedState";
    }
}