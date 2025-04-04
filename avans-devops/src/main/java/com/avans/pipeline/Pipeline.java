package com.avans.pipeline;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.avans.domain.project.ReleaseSprint;
import com.avans.factory.PipelineStepFactory;
import com.avans.strategy.pipeline.PipelineRunStrategy;

public class Pipeline {
    private static final Logger LOGGER = Logger.getLogger(Pipeline.class.getName());
    
    private String name;
    private List<PipelineStep> steps;
    private PipelineStepFactory stepFactory;
    private PipelineRunStrategy runStrategy;
    private ReleaseSprint releaseSprint;
    private boolean isRunning;
    private boolean lastRunSuccessful;
    private LocalDateTime lastRunTime;
    private int executionDurationSeconds;
    private List<String> executionLogs;

    public Pipeline(String name, PipelineRunStrategy runStrategy) {
        this.name = name;
        this.steps = new ArrayList<>();
        this.stepFactory = new PipelineStepFactory();
        this.runStrategy = runStrategy;
        this.isRunning = false;
        this.lastRunSuccessful = false;
        this.executionLogs = new ArrayList<>();
    }

    public void addStep(PipelineStep step) {
        if (isRunning) {
            throw new IllegalStateException("Cannot add steps while pipeline is running");
        }
        steps.add(step);
    }

    public PipelineStep createAndAddStep(String stepType) {
        if (isRunning) {
            throw new IllegalStateException("Cannot add steps while pipeline is running");
        }
        PipelineStep step = stepFactory.createStep(stepType);
        if (step != null) {
            steps.add(step);
        }
        return step;
    }

    public void runAllSteps() {
        if (isRunning) {
            throw new IllegalStateException("Pipeline is already running");
        }
        
        isRunning = true;
        lastRunTime = LocalDateTime.now();
        executionLogs.clear(); // Clear logs BEFORE adding the first log message
        
        logMessage("Starting pipeline: " + name); // Now this message will be kept
        
        try {
            LocalDateTime startTime = LocalDateTime.now();
            boolean successful = runStrategy.runSteps(steps);
            LocalDateTime endTime = LocalDateTime.now();
            
            executionDurationSeconds = (int) java.time.Duration.between(startTime, endTime).getSeconds();
            lastRunSuccessful = successful;
            
            logMessage("Pipeline execution finished in " + executionDurationSeconds + " seconds");
        } finally {
            isRunning = false;
            logMessage("Pipeline completed with status: " + (lastRunSuccessful ? "SUCCESS" : "FAILURE"));
            
            // Notify the release sprint about completion
            if (releaseSprint != null) {
                releaseSprint.finishRelease(lastRunSuccessful);
            }
        }
    }
    
    public void setReleaseSprint(ReleaseSprint sprint) {
        this.releaseSprint = sprint;
    }
    
    private void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = timestamp + " - " + message;
        LOGGER.log(Level.INFO, logEntry);
        executionLogs.add(logEntry);
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean wasLastRunSuccessful() {
        return lastRunSuccessful;
    }
    
    public LocalDateTime getLastRunTime() {
        return lastRunTime;
    }
    
    public int getExecutionDurationSeconds() {
        return executionDurationSeconds;
    }
    
    public List<String> getExecutionLogs() {
        return Collections.unmodifiableList(executionLogs);
    }

    public String getName() {
        return name;
    }
    
    public List<PipelineStep> getSteps() {
        return new ArrayList<>(steps); // Return a copy to preserve encapsulation
    }
    
    public PipelineRunStrategy getRunStrategy() {
        return runStrategy;
    }
    
    public int getStepCount() {
        return steps.size();
    }
    
    public boolean hasStepOfType(String stepType) {
        return steps.stream()
                .anyMatch(step -> step.getName().equalsIgnoreCase(stepType));
    }
    
    public int getSuccessfulStepCount() {
        return (int) steps.stream()
                .filter(PipelineStep::isSuccessful)
                .count();
    }
}
