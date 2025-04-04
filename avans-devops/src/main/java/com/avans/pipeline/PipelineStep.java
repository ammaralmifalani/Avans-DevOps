package com.avans.pipeline;

public abstract class PipelineStep {
    private String name;
    private boolean successful;
    
    public PipelineStep(String name) {
        this.name = name;
        this.successful = false;
    }
    
    // Template Method
    public final boolean runStep() {
        try {
            System.out.println("Running step: " + name);
            initialize();
            execute();
            publishResults();
            successful = true;
            return true;
        } catch (Exception e) {
            System.err.println("Error in step " + name + ": " + e.getMessage());
            successful = false;
            return false;
        }
    }
    
    protected abstract void initialize();
    protected abstract void execute();
    protected abstract void publishResults();
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public String getName() {
        return name;
    }
}