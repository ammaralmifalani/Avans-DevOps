package com.avans.strategy.pipeline;

import java.util.List;

import com.avans.pipeline.PipelineStep;

public class AlwaysContinueStrategy implements PipelineRunStrategy {
    @Override
    public boolean runSteps(List<PipelineStep> steps) {
        boolean allSuccessful = true;
        
        for (PipelineStep step : steps) {
            boolean success = step.runStep();
            if (!success) {
                System.out.println("Step " + step.getName() + " failed. Continuing with next steps.");
                allSuccessful = false;
            }
        }
        
        return allSuccessful;
    }
    
    @Override
    public boolean shouldAbortOnFailure() {
        return false; // This strategy never aborts on failure
    }
}
