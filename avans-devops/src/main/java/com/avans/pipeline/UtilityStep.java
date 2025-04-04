package com.avans.pipeline;

public class UtilityStep extends PipelineStep {
    public UtilityStep() {
        super("Utility");
    }
    
    @Override
    protected void initialize() {
        // Initialize utility step
        System.out.println("Initializing utility step...");
    }
    
    @Override
    protected void execute() {
        // Voer utility taken uit (bv. batch-scripts, file operations)
        System.out.println("Executing utility tasks...");
    }
    
    @Override
    protected void publishResults() {
        // Publiceer utility resultaten
        System.out.println("Publishing utility results...");
    }
}