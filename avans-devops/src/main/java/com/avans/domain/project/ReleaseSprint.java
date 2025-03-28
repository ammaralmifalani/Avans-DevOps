package com.avans.domain.project;

import java.time.LocalDate;

import com.avans.pipeline.Pipeline;
import com.avans.state.release.IReleaseSprintState;
import com.avans.state.release.ReleaseClosedState;
import com.avans.state.release.ReleaseCreatedState;

public class ReleaseSprint extends Sprint {
    private IReleaseSprintState state;
    private Pipeline pipeline;

    public ReleaseSprint(String name, LocalDate startDate, LocalDate endDate) {
        super(name, startDate, endDate);
        this.state = new ReleaseCreatedState();
    }

    public IReleaseSprintState getState() {
        return state;
    }

    public void setState(IReleaseSprintState newState) {
        this.state = newState;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void startRelease() {
        state.start(this);
    }

    public void performRelease() {
        state.performRelease(this);
    }

    public void retryRelease() {
        state.retryRelease(this);
    }

    public void cancelRelease() {
        state.cancelRelease(this);
    }
    public void closeRelease() {
        System.out.println("Closing release for sprint: " + this.getName());
        setState(new ReleaseClosedState());
    }
}
