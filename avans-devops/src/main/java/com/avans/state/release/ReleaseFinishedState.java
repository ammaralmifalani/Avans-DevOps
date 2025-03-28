package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;



public class ReleaseFinishedState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Release is already finished for sprint: " + sprint.getName());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Finalizing release for sprint: " + sprint.getName());
        // Na finalisatie wordt de release officieel als afgerond beschouwd.
        sprint.setState(new ReleasedState());
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Cannot retry in Finished state. Please finalize the release.");
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Cancelling release in Finished state for sprint: " + sprint.getName());
        sprint.setState(new ReleaseCancelledState());
    }

    @Override
    public String getName() {
        return "ReleaseFinishedState";
    }
}