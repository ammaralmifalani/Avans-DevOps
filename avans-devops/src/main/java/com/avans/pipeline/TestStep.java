package com.avans.pipeline;

public class TestStep extends PipelineStep {
    public TestStep() {
        super("Test");
    }
    
    @Override
    protected void initialize() {
        // Initialize test step
    }
    
    @Override
    protected void execute() {
        // Run tests
    }
    
    @Override
    protected void publishResults() {
        // Publish test results
    }
}