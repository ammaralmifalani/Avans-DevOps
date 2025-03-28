package com.avans.strategy.pipeline;

import java.util.List;

import com.avans.pipeline.PipelineStep;

public class FailFastStrategy implements PipelineRunStrategy {
    @Override
    public boolean runSteps(List<PipelineStep> steps) {
        for (PipelineStep step : steps) {
            boolean success = step.runStep();
            if (!success) {
                System.out.println("Step " + step.getName() + " failed. Aborting pipeline execution.");
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean shouldAbortOnFailure() {
        return true; // This strategy aborts on first failure
    }
}
