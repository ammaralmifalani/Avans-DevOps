package com.avans.strategy.report;

import com.avans.decorator.ConcreteReport;
import com.avans.decorator.IReport;
import com.avans.domain.project.Sprint;



public class PdfReportStrategy implements IReportStrategy {
    @Override
    public IReport generate(Sprint sprint) {
        String content = "PDF Report for sprint: " + sprint.getName();
        return new ConcreteReport(content);
    }
}