package com.avans.pipeline;

public class SourceStep extends PipelineStep {
    public SourceStep() {
        super("Source");
    }
    
    @Override
    protected void initialize() {
        // Initialize source step
    }
    
    @Override
    protected void execute() {
        // Fetch source code
    }
    
    @Override
    protected void publishResults() {
        // Publish source step results
    }
}