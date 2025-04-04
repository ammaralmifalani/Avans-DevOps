package com.avans.domain.project;

import java.time.LocalDate;
import java.util.Arrays;

public class Document {
    private String filename;
    private byte[] content;
    private LocalDate uploadDate;
    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB
    private static final String[] ALLOWED_EXTENSIONS = {
        ".pdf", ".doc", ".docx", ".txt", ".jpg", ".png"
    };

    public Document(String filename, byte[] content) {
        validateFilename(filename);
        validateContent(content);
        
        this.filename = filename;
        this.content = Arrays.copyOf(content, content.length); // Defensive copy
        this.uploadDate = LocalDate.now();
    }
    
    private void validateFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        // Check if the file has an allowed extension
        boolean hasValidExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (filename.toLowerCase().endsWith(ext)) {
                hasValidExtension = true;
                break;
            }
        }
        
        if (!hasValidExtension) {
            throw new IllegalArgumentException("File has invalid extension. Allowed: " + 
                String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
    
    private void validateContent(byte[] content) {
        if (content == null) {
            throw new IllegalArgumentException("Document content cannot be null");
        }
        
        if (content.length > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("Document exceeds maximum size of " + 
                (MAX_SIZE_BYTES / (1024 * 1024)) + " MB");
        }
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length); // Return a defensive copy
    }

    public String getFilename() {
        return filename;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }
    
    public long getContentSizeInBytes() {
        return content.length;
    }
    
    public String getExtension() {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }
}
