package com.avans.pipeline;

public class BuildStep extends PipelineStep {
    public BuildStep() {
        super("Build");
    }
    
    @Override
    protected void initialize() {
        // Initialize build step
    }
    
    @Override
    protected void execute() {
        // Build the code
    }
    
    @Override
    protected void publishResults() {
        // Publish build results
    }
}
