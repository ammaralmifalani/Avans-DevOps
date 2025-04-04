package com.avans.state.release;
import com.avans.domain.project.ReleaseSprint;



public class ReleasedState implements IReleaseSprintState {

    @Override
    public void start(ReleaseSprint sprint) {
        System.out.println("Sprint already released: " + sprint.getName());
    }

    @Override
    public void performRelease(ReleaseSprint sprint) {
        System.out.println("Sprint already released: " + sprint.getName());
    }

    @Override
    public void retryRelease(ReleaseSprint sprint) {
        System.out.println("Cannot retry release; sprint already released: " + sprint.getName());
    }

    @Override
    public void cancelRelease(ReleaseSprint sprint) {
        System.out.println("Cannot cancel release; sprint already released: " + sprint.getName());
    }

    @Override
    public String getName() {
        return "ReleasedState";
    }
}