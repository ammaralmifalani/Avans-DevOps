package com.avans.decorator;


public class FooterDecorator extends ReportDecorator {
    private String footer;

    public FooterDecorator(IReport wrappedReport, String footer) {
        super(wrappedReport);
        this.footer = footer;
    }

    @Override
    public String getContent() {
        return super.getContent() + "\n" + footer;
    }
}
