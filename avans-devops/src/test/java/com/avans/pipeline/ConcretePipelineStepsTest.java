package com.avans.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ConcretePipelineStepsTest {

    // Helper method to run a step and check basic success
    private void assertStepRunsSuccessfully(PipelineStep step) {
        assertNotNull(step, "Step should not be null");
        boolean success = step.runStep();
        assertTrue(success, step.getName() + " should run successfully.");
        assertTrue(step.isSuccessful(), step.getName() + " status should be successful after run.");
    }

    @Test
    @DisplayName("SourceStep should run successfully")
    void sourceStepShouldRunSuccessfully() {
        assertStepRunsSuccessfully(new SourceStep());
    }

    @Test
    @DisplayName("PackageStep should run successfully")
    void packageStepShouldRunSuccessfully() {
        assertStepRunsSuccessfully(new PackageStep());
    }

    @Test
    @DisplayName("BuildStep should run successfully")
    void buildStepShouldRunSuccessfully() {
        assertStepRunsSuccessfully(new BuildStep());
    }

    @Test
    @DisplayName("TestStep should run successfully")
    void testStepShouldRunSuccessfully() {
        assertStepRunsSuccessfully(new TestStep());
    }

    @Test
    @DisplayName("AnalyzeStep should run successfully")
    void analyzeStepShouldRunSuccessfully() {
        assertStepRunsSuccessfully(new AnalyzeStep());
    }

    @Test
    @DisplayName("DeployStep should run successfully and print messages")
    void deployStepShouldRunSuccessfully() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            DeployStep step = new DeployStep();
            assertStepRunsSuccessfully(step);
            String output = outContent.toString();
            assertTrue(output.contains("Initializing Deploy Step..."));
            assertTrue(output.contains("Deploying application..."));
            assertTrue(output.contains("Deployment successful."));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("UtilityStep should run successfully and print messages")
    void utilityStepShouldRunSuccessfully() {
         PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            UtilityStep step = new UtilityStep();
            assertStepRunsSuccessfully(step);
             String output = outContent.toString();
            assertTrue(output.contains("Initializing utility step..."));
            assertTrue(output.contains("Executing utility tasks..."));
            assertTrue(output.contains("Publishing utility results..."));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("PipelineStep should handle execution exceptions")
    void pipelineStepShouldHandleExecutionExceptions() {
        PipelineStep failingStep = new PipelineStep("FailingTestStep") {
            @Override protected void initialize() {}
            @Override protected void execute() { throw new RuntimeException("Simulated execute failure"); }
            @Override protected void publishResults() {}
        };

        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        try {
            boolean success = failingStep.runStep();
            assertFalse(success, "Step should report failure on exception.");
            assertFalse(failingStep.isSuccessful(), "Step status should be unsuccessful after failure.");
            assertTrue(errContent.toString().contains("Error in step FailingTestStep: Simulated execute failure"));
        } finally {
             System.setErr(originalErr);
        }
    }
}
