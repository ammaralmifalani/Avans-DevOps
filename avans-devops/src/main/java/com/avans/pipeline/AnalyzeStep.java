package com.avans.pipeline;

public class AnalyzeStep extends PipelineStep {
    public AnalyzeStep() {
        super("Analyze");
    }
    
    @Override
    protected void initialize() {
        // Initialize analyze step
    }
    
    @Override
    protected void execute() {
        // Analyze the code
    }
    
    @Override
    protected void publishResults() {
        // Publish analyze results
    }
    
}
