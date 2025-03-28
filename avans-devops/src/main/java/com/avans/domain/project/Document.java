package com.avans.domain.project;

import java.time.LocalDate;

public class Document {
    private String filename;
    private byte[] content;
    private LocalDate uploadDate;

    public Document(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
        this.uploadDate = LocalDate.now();
    }

    public byte[] getContent() {
        return content;
    }

    public String getFilename() {
        return filename;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }
}
