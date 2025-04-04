package com.avans.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.avans.pipeline.*; // Import all pipeline steps

class PipelineStepFactoryTest {

    private PipelineStepFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PipelineStepFactory();
    }

    @ParameterizedTest(name = "Should create {1} for type ''{0}''")
    @CsvSource({
        "source, com.avans.pipeline.SourceStep",
        "SOURCE, com.avans.pipeline.SourceStep", // Test case insensitivity
        "package, com.avans.pipeline.PackageStep",
        "build, com.avans.pipeline.BuildStep",
        "test, com.avans.pipeline.TestStep",
        "analyze, com.avans.pipeline.AnalyzeStep",
        "deploy, com.avans.pipeline.DeployStep",
        "utility, com.avans.pipeline.UtilityStep"
    })
    @DisplayName("Should create correct PipelineStep instance for valid types")
    void shouldCreateCorrectStepForValidTypes(String stepType, String expectedClassName) throws ClassNotFoundException {
        // Arrange
        Class<?> expectedClass = Class.forName(expectedClassName);

        // Act
        PipelineStep createdStep = factory.createStep(stepType);

        // Assert
        assertNotNull(createdStep, "Created step should not be null for type: " + stepType);
        assertInstanceOf(expectedClass, createdStep, "Created step should be an instance of " + expectedClassName);
    }

    @Test
    @DisplayName("Should throw exception for unknown step type")
    void shouldThrowExceptionForUnknownStepType() {
        // Arrange
        String unknownType = "unknown-step";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createStep(unknownType);
        }, "Should throw IllegalArgumentException for unknown type");

        assertTrue(exception.getMessage().contains("Unknown step type: " + unknownType),
                   "Exception message should indicate the unknown type");
    }

    @Test
    @DisplayName("Should throw exception for null step type")
    void shouldThrowExceptionForNullStepType() {
        // Arrange
        String nullType = null;

        // Act & Assert
        // Note: The switch statement will throw a NullPointerException before our custom IllegalArgumentException
        assertThrows(NullPointerException.class, () -> {
             factory.createStep(nullType);
        }, "Should throw NullPointerException for null type");
    }

     @Test
    @DisplayName("Should throw exception for empty step type")
    void shouldThrowExceptionForEmptyStepType() {
        // Arrange
        String emptyType = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.createStep(emptyType);
        }, "Should throw IllegalArgumentException for empty type");

         assertTrue(exception.getMessage().contains("Unknown step type: " + emptyType),
                   "Exception message should indicate the unknown type");
    }
}
