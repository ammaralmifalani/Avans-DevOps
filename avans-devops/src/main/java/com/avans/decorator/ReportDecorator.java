package com.avans.decorator;

public abstract class ReportDecorator implements IReport {
    protected IReport wrappedReport;

    public ReportDecorator(IReport wrappedReport) {
        this.wrappedReport = wrappedReport;
    }

    @Override
    public String getContent() {
        return wrappedReport.getContent();
    }
}
