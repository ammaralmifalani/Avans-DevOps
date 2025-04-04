package com.avans.state.release;

import com.avans.domain.project.ReleaseSprint;


public class ReleasingState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Release is already in the releasing phase for sprint: " + sprint.getName());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Executing release pipeline for sprint: " + sprint.getName());
        // Simuleer pipeline-uitvoering; in een echte implementatie controleer je de uitkomst.
        // Na succesvolle uitvoering gaan we over naar Finished.
        sprint.setState(new ReleaseFinishedState());
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Cannot retry while release is in releasing phase for sprint: " + sprint.getName());
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Cancelling release during releasing phase for sprint: " + sprint.getName());
        sprint.setState(new ReleaseCancelledState());
    }

    @Override
    public String getName() {
        return "ReleasingState";
    }
}