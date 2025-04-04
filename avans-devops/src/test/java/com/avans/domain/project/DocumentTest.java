package com.avans.domain.project;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    @DisplayName("Document should store filename and content")
    void documentShouldStoreFilenameAndContent() {
        // Arrange
        String filename = "test-document.pdf";
        byte[] content = "Test document content".getBytes(StandardCharsets.UTF_8);
        
        // Act
        Document document = new Document(filename, content);
        
        // Assert
        assertEquals(filename, document.getFilename());
        assertArrayEquals(content, document.getContent());
    }
    
    @Test
    @DisplayName("Document should record upload date as current date")
    void documentShouldRecordUploadDateAsCurrentDate() {
        // Arrange
        String filename = "test-document.pdf";
        byte[] content = "Test document content".getBytes(StandardCharsets.UTF_8);
        LocalDate today = LocalDate.now();
        
        // Act
        Document document = new Document(filename, content);
        
        // Assert
        assertEquals(today, document.getUploadDate());
    }
    
    @Test
    @DisplayName("Document with empty content should be allowed")
    void documentWithEmptyContentShouldBeAllowed() {
        // Arrange
        String filename = "empty-document.pdf";
        byte[] emptyContent = new byte[0];
        
        // Act
        Document document = new Document(filename, emptyContent);
        
        // Assert
        assertEquals(filename, document.getFilename());
        assertArrayEquals(emptyContent, document.getContent());
        assertEquals(0, document.getContent().length);
    }
}
