package com.avans.decorator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReportDecoratorTest {

    @Test
    @DisplayName("ConcreteReport should return content")
    void concreteReportShouldReturnContent() {
        // Arrange
        String content = "Test report content";
        IReport report = new ConcreteReport(content);
        
        // Act & Assert
        assertEquals(content, report.getContent());
    }
    
    @Test
    @DisplayName("HeaderDecorator should add header to content")
    void headerDecoratorShouldAddHeaderToContent() {
        // Arrange
        String content = "Test report content";
        String header = "REPORT HEADER";
        IReport baseReport = new ConcreteReport(content);
        IReport decoratedReport = new HeaderDecorator(baseReport, header);
        
        // Act
        String result = decoratedReport.getContent();
        
        // Assert
        assertTrue(result.startsWith(header));
        assertTrue(result.contains(content));
        assertEquals(header + "\n" + content, result);
    }
    
    @Test
    @DisplayName("FooterDecorator should add footer to content")
    void footerDecoratorShouldAddFooterToContent() {
        // Arrange
        String content = "Test report content";
        String footer = "REPORT FOOTER";
        IReport baseReport = new ConcreteReport(content);
        IReport decoratedReport = new FooterDecorator(baseReport, footer);
        
        // Act
        String result = decoratedReport.getContent();
        
        // Assert
        assertTrue(result.endsWith(footer));
        assertTrue(result.contains(content));
        assertEquals(content + "\n" + footer, result);
    }
    
    @Test
    @DisplayName("Multiple decorators should be applied in correct order")
    void multipleDecoratorsShouldBeAppliedInCorrectOrder() {
        // Arrange
        String content = "Test report content";
        String header = "REPORT HEADER";
        String footer = "REPORT FOOTER";
        
        IReport baseReport = new ConcreteReport(content);
        IReport reportWithHeader = new HeaderDecorator(baseReport, header);
        IReport reportWithHeaderAndFooter = new FooterDecorator(reportWithHeader, footer);
        
        // Act
        String result = reportWithHeaderAndFooter.getContent();
        
        // Assert
        assertTrue(result.startsWith(header));
        assertTrue(result.endsWith(footer));
        assertTrue(result.contains(content));
        assertEquals(header + "\n" + content + "\n" + footer, result);
        
        // Also test order reversal (footer then header)
        IReport reportWithFooter = new FooterDecorator(baseReport, footer);
        IReport reportWithFooterAndHeader = new HeaderDecorator(reportWithFooter, header);
        
        result = reportWithFooterAndHeader.getContent();
        
        assertTrue(result.startsWith(header));
        assertTrue(result.endsWith(footer));
        assertTrue(result.contains(content));
        assertEquals(header + "\n" + content + "\n" + footer, result);
    }
}
