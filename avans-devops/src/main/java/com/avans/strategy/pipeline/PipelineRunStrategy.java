package com.avans.strategy.pipeline;

import java.util.List;

import com.avans.pipeline.PipelineStep;

public interface PipelineRunStrategy {
    void runSteps(List<PipelineStep> steps);
}
