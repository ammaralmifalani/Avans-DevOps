package com.avans.strategy.pipeline;

import java.util.List;

import com.avans.pipeline.PipelineStep;

public interface PipelineRunStrategy {
    /**
     * Runs all pipeline steps according to the strategy
     * @param steps List of pipeline steps to run
     * @return true if all steps completed successfully, false otherwise
     */
    boolean runSteps(List<PipelineStep> steps);
    
    /**
     * Determines if the pipeline should abort on failure based on the strategy
     * @return true if pipeline should abort on first failure, false to continue
     */
    default boolean shouldAbortOnFailure() {
        return false; // Default behavior is to continue
    }
}
