package com.avans.pipeline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
class PipelineTest {

    private Pipeline pipeline;
    
    // Test-specific PipelineStep implementations
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
    @DisplayName("Pipeline should add steps correctly")
    void shouldAddStepsCorrectly() {
        pipeline = new Pipeline("Test Pipeline", new AlwaysContinueStrategy());
        
        pipeline.addStep(successStep);
        pipeline.addStep(failureStep);
        
        assertEquals(2, pipeline.getSteps().size());
        assertSame(successStep, pipeline.getSteps().get(0));
        assertSame(failureStep, pipeline.getSteps().get(1));
    }

    @Test
    @DisplayName("Pipeline with FailFast strategy should stop on first failure")
    void failFastStrategyShouldStopOnFirstFailure() {
        PipelineRunStrategy strategy = new FailFastStrategy();
        pipeline = new Pipeline("Test Pipeline", strategy);
        
        // Add steps: success, failure, success
        pipeline.addStep(successStep);
        pipeline.addStep(failureStep);
        TestSuccessStep anotherSuccessStep = new TestSuccessStep();
        pipeline.addStep(anotherSuccessStep);
        
        // Run pipeline
        pipeline.runAllSteps();
        
        // Verify steps execution by checking initialization flags
        assertTrue(successStep.initializeCalled);
        assertTrue(failureStep.initializeCalled);
        // The third step should not have been executed due to FailFast strategy
        assertFalse(anotherSuccessStep.initializeCalled);
        
        // Check pipeline status
        assertFalse(pipeline.wasLastRunSuccessful());
    }

    @Test
    @DisplayName("Pipeline with AlwaysContinue strategy should run all steps")
    void alwaysContinueStrategyShouldRunAllSteps() {
        PipelineRunStrategy strategy = new AlwaysContinueStrategy();
        pipeline = new Pipeline("Test Pipeline", strategy);
        
        // Add steps: success, failure, success
        pipeline.addStep(successStep);
        pipeline.addStep(failureStep);
        TestSuccessStep anotherSuccessStep = new TestSuccessStep();
        pipeline.addStep(anotherSuccessStep);
        
        // Run pipeline
        pipeline.runAllSteps();
        
        // Verify all steps executed
        assertTrue(successStep.initializeCalled);
        assertTrue(failureStep.initializeCalled);
        assertTrue(anotherSuccessStep.initializeCalled);
        
        // Check pipeline status
        assertFalse(pipeline.wasLastRunSuccessful()); // Still false because one step failed
    }

    @Test
    @DisplayName("Pipeline should notify release sprint when finished")
    void shouldNotifyReleaseSprintWhenFinished() {
        pipeline = new Pipeline("Test Pipeline", new AlwaysContinueStrategy());
        pipeline.setReleaseSprint(releaseSprint);
        
        // Add only successful steps
        pipeline.addStep(successStep);
        
        // Run pipeline
        pipeline.runAllSteps();
        
        // Verify release sprint notification
        verify(releaseSprint).finishRelease(true);
        
        // Setup a new pipeline with failure
        Pipeline failingPipeline = new Pipeline("Failing Pipeline", new AlwaysContinueStrategy());
        failingPipeline.setReleaseSprint(releaseSprint);
        failingPipeline.addStep(failureStep);
        
        // Reset the mock to clear previous interactions
        reset(releaseSprint);
        
        // Run the failing pipeline
        failingPipeline.runAllSteps();
        
        // Verify release sprint notification with failure
        verify(releaseSprint).finishRelease(false);
    }

    @Test
    @DisplayName("Cannot add steps while pipeline is running")
    void cannotAddStepsWhilePipelineIsRunning() {
        // Create a mock Pipeline that appears to be running
        pipeline = spy(new Pipeline("Test Pipeline", new AlwaysContinueStrategy()));
        when(pipeline.isRunning()).thenReturn(true);
        
        // Try to add a step - should throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            pipeline.addStep(successStep);
        });
        
        assertTrue(exception.getMessage().contains("Cannot add steps while pipeline is running"));
    }

    @Test
    @DisplayName("Cannot run pipeline that is already running")
    void cannotRunPipelineThatIsAlreadyRunning() {
        // Create a mock Pipeline that appears to be running
        pipeline = spy(new Pipeline("Test Pipeline", new AlwaysContinueStrategy()));
        when(pipeline.isRunning()).thenReturn(true);
        
        // Try to run the pipeline again - should throw exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            pipeline.runAllSteps();
        });
        
        assertTrue(exception.getMessage().contains("Pipeline is already running"));
    }
    
    // Concrete subclass for successful steps
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
    
    // Concrete subclass for failing steps
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
