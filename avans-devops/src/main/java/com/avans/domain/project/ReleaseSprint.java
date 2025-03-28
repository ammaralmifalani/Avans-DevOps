package com.avans.domain.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avans.domain.member.ProductOwner;
import com.avans.domain.member.TeamMember;
import com.avans.pipeline.Pipeline;
import com.avans.state.release.IReleaseSprintState;
import com.avans.state.release.ReleaseClosedState;
import com.avans.state.release.ReleaseCreatedState;

public class ReleaseSprint extends Sprint {
    private IReleaseSprintState state;
    private Pipeline pipeline;
    private boolean pipelineRunning;

    public ReleaseSprint(String name, LocalDate startDate, LocalDate endDate) {
        super(name, startDate, endDate);
        this.state = new ReleaseCreatedState();
        this.pipelineRunning = false;
    }

    public IReleaseSprintState getState() {
        return state;
    }

    public void setState(IReleaseSprintState newState) {
        IReleaseSprintState oldState = this.state;
        this.state = newState;
        
        // Notify observers of the state change
        notifyObservers("Release sprint '" + getName() + "' state changed from " 
                + oldState.getName() + " to " + newState.getName());
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
        
        // Set the relationship both ways for proper notification flow
        if (pipeline != null) {
            pipeline.setReleaseSprint(this);
        }
    }
    
    public boolean isPipelineRunning() {
        return pipelineRunning;
    }
    
    private void setPipelineRunning(boolean running) {
        this.pipelineRunning = running;
    }

    public void startRelease() {
        if (pipeline == null) {
            throw new IllegalStateException("Cannot start release without a pipeline");
        }
        
        if (!isFinished()) {
            throw new IllegalStateException("Cannot start release before sprint is finished");
        }
        
        if (!areAllBacklogItemsDone()) {
            throw new IllegalStateException("Cannot start release - not all backlog items are done");
        }
        
        state.start(this);
    }

    public void performRelease() {
        if (pipeline == null) {
            throw new IllegalStateException("Cannot perform release without a pipeline");
        }
        
        setPipelineRunning(true);
        state.performRelease(this);
        
        // Start pipeline execution in a separate thread
        new Thread(() -> {
            try {
                pipeline.runAllSteps();
            } catch (Exception e) {
                notifyObservers("Release pipeline failed: " + e.getMessage());
                notifyProductOwnerAndScrumMaster("Release pipeline failed: " + e.getMessage());
            }
        }).start();
    }
    
    public void finishRelease(boolean success) {
        setPipelineRunning(false);
        if (success) {
            notifyObservers("Release for sprint '" + getName() + "' completed successfully");
            notifyProductOwnerAndScrumMaster("Release for sprint '" + getName() + "' completed successfully");
        } else {
            notifyObservers("Release for sprint '" + getName() + "' failed");
            notifyProductOwnerAndScrumMaster("Release for sprint '" + getName() + "' failed");
        }
    }

    public void retryRelease() {
        if (pipelineRunning) {
            throw new IllegalStateException("Cannot retry release while pipeline is still running");
        }
        
        state.retryRelease(this);
    }

    public void cancelRelease() {
        if (pipelineRunning) {
            throw new IllegalStateException("Cannot cancel release while pipeline is still running");
        }
        
        state.cancelRelease(this);
        
        // Notify both product owner and scrum master about cancellation
        notifyProductOwnerAndScrumMaster("Release for sprint '" + getName() + "' has been cancelled");
    }
    
    public void closeRelease() {
        System.out.println("Closing release for sprint: " + this.getName());
        setState(new ReleaseClosedState());
    }
    
    private void notifyProductOwnerAndScrumMaster(String message) {
        // Notify scrum master
        if (scrumMaster != null) {
            scrumMaster.update(message);
        }
        
        // Find product owners in the team and notify them
        for (TeamMember member : getTeamMembers()) {
            if (member instanceof ProductOwner) {
                member.update(message);
            }
        }
    }
}
