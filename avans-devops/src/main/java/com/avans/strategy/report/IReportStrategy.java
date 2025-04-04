package com.avans.strategy.report;

import com.avans.decorator.IReport;
import com.avans.domain.project.Sprint;

public interface IReportStrategy {
    IReport generate(Sprint sprint);
}