package com.avans.strategy.report;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.decorator.IReport;
import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.member.Developer;
import com.avans.domain.member.ScrumMaster;
import com.avans.domain.member.TeamMember;
import com.avans.domain.project.Sprint;

@ExtendWith(MockitoExtension.class)
class ReportStrategyTest {

    @Mock
    private Sprint mockSprint;
    
    @Mock 
    private ScrumMaster mockScrumMaster;
    
    @Mock
    private BacklogItem mockBacklogItem;
    
    private List<TeamMember> teamMembers;
    private List<BacklogItem> backlogItems;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sprintName;

    @BeforeEach
    void setUp() {
        sprintName = "Test Sprint";
        startDate = LocalDate.of(2025, 4, 1);
        endDate = LocalDate.of(2025, 4, 15);
        
        teamMembers = new ArrayList<>();
        teamMembers.add(new Developer("John Developer"));
        teamMembers.add(mockScrumMaster);
        
        backlogItems = new ArrayList<>();
        backlogItems.add(mockBacklogItem);
        
        // Setup mock sprint
        when(mockSprint.getName()).thenReturn(sprintName);
        when(mockSprint.getStartDate()).thenReturn(startDate);
        when(mockSprint.getEndDate()).thenReturn(endDate);
        when(mockSprint.getTeamMembers()).thenReturn(teamMembers);
        when(mockSprint.getBacklogItems()).thenReturn(backlogItems);
        when(mockSprint.getScrumMaster()).thenReturn(mockScrumMaster);
        when(mockScrumMaster.getName()).thenReturn("Jane ScrumMaster");
    }

    @Test
    @DisplayName("PdfReportStrategy should generate PDF-specific report content")
    void pdfReportStrategyShouldGeneratePdfSpecificContent() {
        // Arrange
        IReportStrategy pdfStrategy = new PdfReportStrategy();
        
        // Act
        IReport report = pdfStrategy.generate(mockSprint);
        String content = report.getContent();
        
        // Assert
        assertNotNull(content);
        assertTrue(content.contains("PDF"));
        assertTrue(content.contains(sprintName));
        assertTrue(content.contains(startDate.toString()));
        assertTrue(content.contains(endDate.toString()));
    }
    
    @Test
    @DisplayName("PngReportStrategy should generate PNG-specific report content")
    void pngReportStrategyShouldGeneratePngSpecificContent() {
        // Arrange
        IReportStrategy pngStrategy = new PngReportStrategy();
        
        // Act
        IReport report = pngStrategy.generate(mockSprint);
        String content = report.getContent();
        
        // Assert
        assertNotNull(content);
        assertTrue(content.contains("PNG"));
        assertTrue(content.contains(sprintName));
        assertTrue(content.contains(startDate.toString()));
        assertTrue(content.contains(endDate.toString()));
    }
    
    @Test
    @DisplayName("Different report strategies should produce different outputs")
    void differentReportStrategiesShouldProduceDifferentOutputs() {
        // Arrange
        IReportStrategy pdfStrategy = new PdfReportStrategy();
        IReportStrategy pngStrategy = new PngReportStrategy();
        
        // Act
        IReport pdfReport = pdfStrategy.generate(mockSprint);
        IReport pngReport = pngStrategy.generate(mockSprint);
        
        // Assert
        assertNotEquals(pdfReport.getContent(), pngReport.getContent());
    }
}
