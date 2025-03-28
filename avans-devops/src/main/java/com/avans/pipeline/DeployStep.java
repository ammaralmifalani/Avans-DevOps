package com.avans.pipeline;

public class DeployStep extends PipelineStep {
    public DeployStep() {
        super("Deploy");
    }

    @Override
    protected void initialize() {
        System.out.println("Initializing Deploy Step...");
    }

    @Override
    protected void execute() {
        System.out.println("Deploying application...");
    }

    @Override
    protected void publishResults() {
        System.out.println("Deployment successful.");
    }
}