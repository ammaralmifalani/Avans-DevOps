package com.avans.pipeline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.domain.project.ReleaseSprint;
import com.avans.strategy.pipeline.AlwaysContinueStrategy;
import com.avans.strategy.pipeline.FailFastStrategy;
import com.avans.strategy.pipeline.PipelineRunStrategy;

@ExtendWith(MockitoExtension.class)
class EnhancedPipelineTest {

    private Pipeline pipeline;
    
    // Test-specific PipelineStep implementations from PipelineTest
    private TestSuccessStep successStep;
    private TestFailureStep failureStep;
    
    @Mock
    private ReleaseSprint releaseSprint;

    @BeforeEach
    void setUp() {
        // Create pipeline steps without overriding the final runStep() method
        successStep = new TestSuccessStep();
        failureStep = new TestFailureStep();
    }
    
    @Test
    @DisplayName("Pipeline should track execution logs")
    void pipelineShouldTrackExecutionLogs() {
        // Arrange
        pipeline = new Pipeline("Test Pipeline", new AlwaysContinueStrategy());
        pipeline.addStep(successStep);
        
        // Act
        pipeline.runAllSteps();
        
        // Assert
        assertFalse(pipeline.getExecutionLogs().isEmpty());
        assertTrue(pipeline.getExecutionLogs().stream()
                .anyMatch(log -> log.contains("Starting pipeline")));
        assertTrue(pipeline.getExecutionLogs().stream()
                .anyMatch(log -> log.contains("Pipeline completed with status: SUCCESS")));
    }
    
    @Test
    @DisplayName("Pipeline should record execution time")
    void pipelineShouldRecordExecutionTime() {
        // Arrange
        pipeline = new Pipeline("Test Pipeline", new AlwaysContinueStrategy());
        pipeline.addStep(successStep);
        
        // Act
        LocalDateTime beforeRun = LocalDateTime.now();
        pipeline.runAllSteps();
        LocalDateTime afterRun = LocalDateTime.now();
        
        // Assert
        assertNotNull(pipeline.getLastRunTime());
        assertTrue(pipeline.getLastRunTime().isAfter(beforeRun) || pipeline.getLastRunTime().isEqual(beforeRun));
        assertTrue(pipeline.getLastRunTime().isBefore(afterRun) || pipeline.getLastRunTime().isEqual(afterRun));
        assertTrue(pipeline.getExecutionDurationSeconds() >= 0);
    }
    
    @Test
    @DisplayName("Pipeline should track step counts")
    void pipelineShouldTrackStepCounts() {
        // Arrange
        pipeline = new Pipeline("Test Pipeline", new AlwaysContinueStrategy());
        pipeline.addStep(successStep);
        pipeline.addStep(failureStep);
        pipeline.addStep(new TestSuccessStep());
        
        // Act
        pipeline.runAllSteps();
        
        // Assert
        assertEquals(3, pipeline.getStepCount());
        assertEquals(2, pipeline.getSuccessfulStepCount());
        assertTrue(pipeline.hasStepOfType("SuccessStep"));
        assertTrue(pipeline.hasStepOfType("FailureStep"));
    }
    
    @Test
    @DisplayName("Pipeline should expose run strategy")
    void pipelineShouldExposeRunStrategy() {
        // Arrange
        PipelineRunStrategy strategy = new FailFastStrategy();
        pipeline = new Pipeline("Test Pipeline", strategy);
        
        // Act & Assert
        assertSame(strategy, pipeline.getRunStrategy());
    }
    
    // Reuse the test classes from PipelineTest
    private static class TestSuccessStep extends PipelineStep {
        public boolean initializeCalled = false;
        public boolean executeCalled = false;
        public boolean publishResultsCalled = false;
        
        public TestSuccessStep() {
            super("SuccessStep");
        }
        
        @Override
        protected void initialize() {
            initializeCalled = true;
        }
        
        @Override
        protected void execute() {
            executeCalled = true;
        }
        
        @Override
        protected void publishResults() {
            publishResultsCalled = true;
        }
    }
    
    private static class TestFailureStep extends PipelineStep {
        public boolean initializeCalled = false;
        public boolean executeCalled = false;
        public boolean publishResultsCalled = false;
        
        public TestFailureStep() {
            super("FailureStep");
        }
        
        @Override
        protected void initialize() {
            initializeCalled = true;
        }
        
        @Override
        protected void execute() {
            executeCalled = true;
            // Simulate a failure by throwing an exception
            throw new RuntimeException("Simulated failure in pipeline step");
        }
        
        @Override
        protected void publishResults() {
            publishResultsCalled = true;
        }
    }
}
