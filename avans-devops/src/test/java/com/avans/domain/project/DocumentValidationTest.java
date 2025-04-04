package com.avans.domain.project;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentValidationTest {

    @Test
    @DisplayName("Document should reject null filename")
    void documentShouldRejectNullFilename() {
        // Arrange
        byte[] content = "Test content".getBytes();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Document(null, content);
        });
        
        assertTrue(exception.getMessage().contains("Filename cannot be null"));
    }
    
    @Test
    @DisplayName("Document should reject empty filename")
    void documentShouldRejectEmptyFilename() {
        // Arrange
        byte[] content = "Test content".getBytes();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Document("", content);
        });
        
        assertTrue(exception.getMessage().contains("Filename cannot be null or empty"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {".pdf", ".doc", ".docx", ".txt", ".jpg", ".png"})
    @DisplayName("Document should accept files with allowed extensions")
    void documentShouldAcceptFilesWithAllowedExtensions(String extension) {
        // Arrange
        String filename = "document" + extension;
        byte[] content = "Test content".getBytes();
        
        // Act
        Document document = new Document(filename, content);
        
        // Assert
        assertEquals(filename, document.getFilename());
        assertArrayEquals(content, document.getContent());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {".exe", ".bat", ".sh", ".php", ".html", ".js"})
    @DisplayName("Document should reject files with disallowed extensions")
    void documentShouldRejectFilesWithDisallowedExtensions(String extension) {
        // Arrange
        String filename = "document" + extension;
        byte[] content = "Test content".getBytes();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Document(filename, content);
        });
        
        assertTrue(exception.getMessage().contains("File has invalid extension"));
    }
    
    @Test
    @DisplayName("Document should reject null content")
    void documentShouldRejectNullContent() {
        // Arrange
        String filename = "document.pdf";
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Document(filename, null);
        });
        
        assertTrue(exception.getMessage().contains("Document content cannot be null"));
    }
    
    @Test
    @DisplayName("Document should reject content exceeding maximum size")
    void documentShouldRejectContentExceedingMaximumSize() {
        // Arrange
        String filename = "large-document.pdf";
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11 MB (exceeds the 10 MB limit)
        new Random().nextBytes(largeContent); // Fill with random bytes
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Document(filename, largeContent);
        });
        
        assertTrue(exception.getMessage().contains("Document exceeds maximum size"));
    }
    
    @Test
    @DisplayName("Document should return correct extension")
    void documentShouldReturnCorrectExtension() {
        // Arrange
        String filename = "document.PDF"; // Mixed case
        byte[] content = "Test content".getBytes();
        
        // Act
        Document document = new Document(filename, content);
        
        // Assert
        assertEquals(".pdf", document.getExtension()); // Should be lowercase
    }
    
    @Test
    @DisplayName("Document should return correct content size")
    void documentShouldReturnCorrectContentSize() {
        // Arrange
        String filename = "document.pdf";
        byte[] content = "Test content".getBytes();
        
        // Act
        Document document = new Document(filename, content);
        
        // Assert
        assertEquals(content.length, document.getContentSizeInBytes());
    }
}
