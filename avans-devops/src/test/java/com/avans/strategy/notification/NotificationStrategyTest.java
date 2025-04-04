package com.avans.strategy.notification;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationStrategyTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("EmailNotification should send email-formatted message")
    void emailNotificationShouldSendEmailFormattedMessage() {
        // Arrange
        NotificationStrategy emailStrategy = new EmailNotification();
        String message = "Test notification message";
        
        // Act
        emailStrategy.sendNotification(message);
        
        // Assert
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("[Email]"));
        assertTrue(output.contains(message));
        assertEquals("[Email] " + message, output);
    }
    
    @Test
    @DisplayName("SlackNotification should send slack-formatted message")
    void slackNotificationShouldSendSlackFormattedMessage() {
        // Arrange
        NotificationStrategy slackStrategy = new SlackNotification();
        String message = "Test notification message";
        
        // Act
        slackStrategy.sendNotification(message);
        
        // Assert
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("[Slack]"));
        assertTrue(output.contains(message));
        assertEquals("[Slack] " + message, output);
    }
    
    @Test
    @DisplayName("Different notification strategies should produce different outputs")
    void differentNotificationStrategiesShouldProduceDifferentOutputs() {
        // Arrange
        NotificationStrategy emailStrategy = new EmailNotification();
        NotificationStrategy slackStrategy = new SlackNotification();
        String message = "Test notification message";
        
        // Act & Assert
        emailStrategy.sendNotification(message);
        String emailOutput = outputStreamCaptor.toString().trim();
        
        // Reset the output stream
        outputStreamCaptor.reset();
        
        slackStrategy.sendNotification(message);
        String slackOutput = outputStreamCaptor.toString().trim();
        
        // Different prefixes should result in different outputs
        assertNotEquals(emailOutput, slackOutput);
    }
}
