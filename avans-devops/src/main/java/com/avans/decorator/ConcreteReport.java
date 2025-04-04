package com.avans.decorator;

public class ConcreteReport implements IReport {
    private String content;

    public ConcreteReport(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}