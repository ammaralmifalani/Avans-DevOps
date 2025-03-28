package com.avans.pipeline;
import java.util.ArrayList;
import java.util.List;

import com.avans.domain.project.ReleaseSprint;
import com.avans.factory.PipelineStepFactory;
import com.avans.strategy.pipeline.PipelineRunStrategy;

public class Pipeline {
    private String name;
    private List<PipelineStep> steps;
    private PipelineStepFactory stepFactory;
    private PipelineRunStrategy runStrategy;
    private ReleaseSprint releaseSprint;
    private boolean isRunning;
    private boolean lastRunSuccessful;

    public Pipeline(String name, PipelineRunStrategy runStrategy) {
        this.name = name;
        this.steps = new ArrayList<>();
        this.stepFactory = new PipelineStepFactory();
        this.runStrategy = runStrategy;
        this.isRunning = false;
        this.lastRunSuccessful = false;
    }

    public void addStep(PipelineStep step) {
        if (isRunning) {
            throw new IllegalStateException("Cannot add steps while pipeline is running");
        }
        steps.add(step);
    }

    public PipelineStep createAndAddStep(String stepType) {
        if (isRunning) {
            throw new IllegalStateException("Cannot add steps while pipeline is running");
        }
        PipelineStep step = stepFactory.createStep(stepType);
        if (step != null) {
            steps.add(step);
        }
        return step;
    }

    public void runAllSteps() {
        if (isRunning) {
            throw new IllegalStateException("Pipeline is already running");
        }
        
        isRunning = true;
        System.out.println("Starting pipeline: " + name);
        
        try {
            boolean successful = runStrategy.runSteps(steps);
            lastRunSuccessful = successful;
        } finally {
            isRunning = false;
            System.out.println("Pipeline completed with status: " + (lastRunSuccessful ? "SUCCESS" : "FAILURE"));
            
            // Notify the release sprint about completion
            if (releaseSprint != null) {
                releaseSprint.finishRelease(lastRunSuccessful);
            }
        }
    }
    
    public void setReleaseSprint(ReleaseSprint sprint) {
        this.releaseSprint = sprint;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean wasLastRunSuccessful() {
        return lastRunSuccessful;
    }

    public String getName() {
        return name;
    }
    
    public List<PipelineStep> getSteps() {
        return new ArrayList<>(steps); // Return a copy to preserve encapsulation
    }
}
