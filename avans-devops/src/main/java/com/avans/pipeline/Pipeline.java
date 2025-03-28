package com.avans.pipeline;
import java.util.ArrayList;
import java.util.List;

import com.avans.factory.PipelineStepFactory;
import com.avans.strategy.pipeline.PipelineRunStrategy;

public class Pipeline {
    private String name;
    private List<PipelineStep> steps;
    private PipelineStepFactory stepFactory;
    private PipelineRunStrategy runStrategy;

    public Pipeline(String name, PipelineRunStrategy runStrategy) {
        this.name = name;
        this.steps = new ArrayList<>();
        this.stepFactory = new PipelineStepFactory();
        this.runStrategy = runStrategy;
    }

    public void addStep(PipelineStep step) {
        steps.add(step);
    }

    public PipelineStep createAndAddStep(String stepType) {
        PipelineStep step = stepFactory.createStep(stepType);
        if (step != null) {
            steps.add(step);
        }
        return step;
    }

    public void runAllSteps() {
        runStrategy.runSteps(steps);
    }

    public String getName() {
        return name;
    }
}
