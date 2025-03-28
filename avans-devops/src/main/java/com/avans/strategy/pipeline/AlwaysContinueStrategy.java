package com.avans.strategy.pipeline;


import java.util.List;

import com.avans.pipeline.PipelineStep;

public class AlwaysContinueStrategy implements PipelineRunStrategy {
    @Override
    public void runSteps(List<PipelineStep> steps) {
        for (PipelineStep step : steps) {
            step.runStep();
            if (!step.isSuccessful()) {
                System.out.println("Step " + step.getName() + " failed. Continuing with next steps.");
            }
        }
    }
}
