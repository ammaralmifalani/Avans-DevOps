package com.avans.decorator;

public class 
HeaderDecorator extends ReportDecorator {
    private String header;

    public HeaderDecorator(IReport wrappedReport, String header) {
        super(wrappedReport);
        this.header = header;
    }

    @Override
    public String getContent() {
        return header + "\n" + super.getContent();
    }
}
