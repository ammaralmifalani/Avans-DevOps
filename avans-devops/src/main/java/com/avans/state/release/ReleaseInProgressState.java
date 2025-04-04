package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;




public class ReleaseInProgressState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Release already in progress for sprint: " + sprint.getName());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Initiating release actions for sprint: " + sprint.getName());
        // Overgang naar ReleasingState, waarin de pipeline actief wordt uitgevoerd.
        sprint.setState(new ReleasingState());
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Retrying release in InProgress state for sprint: " + sprint.getName());
        performRelease(sprint);
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Cancelling release in InProgress state for sprint: " + sprint.getName());
        sprint.setState(new ReleaseCancelledState());
    }

    @Override
    public String getName() {
        return "ReleaseInProgressState";
    }
}