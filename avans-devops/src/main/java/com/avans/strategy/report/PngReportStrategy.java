package com.avans.strategy.report;

import com.avans.decorator.ConcreteReport;
import com.avans.decorator.IReport;
import com.avans.domain.project.Sprint;

public class PngReportStrategy implements IReportStrategy {
    @Override
    public IReport generate(Sprint sprint) {
        String content = "PNG Report for sprint: " + sprint.getName();
        return new ConcreteReport(content);
    }
}