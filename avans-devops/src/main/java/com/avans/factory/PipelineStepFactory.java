package com.avans.factory;
import com.avans.pipeline.AnalyzeStep;
import com.avans.pipeline.BuildStep;
import com.avans.pipeline.DeployStep;
import com.avans.pipeline.PackageStep;
import com.avans.pipeline.PipelineStep;
import com.avans.pipeline.SourceStep;
import com.avans.pipeline.TestStep;
import com.avans.pipeline.UtilityStep;

public class PipelineStepFactory {
    public PipelineStep createStep(String stepType) {
        switch (stepType.toLowerCase()) {
            case "source":
                return new SourceStep();
            case "package":
                return new PackageStep();
            case "build":
                return new BuildStep();
            case "test":
                return new TestStep();
            case "analyze":
                return new AnalyzeStep();
            case "deploy":
                return new DeployStep();
            case "utility":
                return new UtilityStep();
            default:
                throw new IllegalArgumentException("Unknown step type: " + stepType);
        }
    }
}