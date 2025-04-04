package com.avans.strategy.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avans.decorator.ConcreteReport;
import com.avans.decorator.IReport;
import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.backlog.state.DoneState;
import com.avans.domain.member.TeamMember;
import com.avans.domain.project.Sprint;

public class PngReportStrategy implements IReportStrategy {
    @Override
    public IReport generate(Sprint sprint) {
        StringBuilder reportContent = new StringBuilder();
        
        // Build header
        buildReportHeader(reportContent, sprint);
        
        // Team visualization section
        buildTeamSection(reportContent, sprint);
        
        // Progress chart 
        buildStatusDashboard(reportContent, sprint);
        
        // Burndown chart
        buildBurndownChart(reportContent, sprint);
        
        // Additional metadata specific to PNG format
        buildMetadataSection(reportContent);
        
        return new ConcreteReport(reportContent.toString());
    }
    
    private void buildReportHeader(StringBuilder reportContent, Sprint sprint) {
        reportContent.append("=== PNG SPRINT REPORT (Graphical Format) ===\n\n");
        reportContent.append("Sprint: ").append(sprint.getName()).append("\n");
        reportContent.append("Period: ").append(formatDate(sprint.getStartDate()))
                     .append(" - ").append(formatDate(sprint.getEndDate())).append("\n\n");
    }
    
    private void buildTeamSection(StringBuilder reportContent, Sprint sprint) {
        reportContent.append("== TEAM VISUALIZATION ==\n");
        reportContent.append("┌────────────────────┐\n");
        reportContent.append("│ Scrum Master:      │\n");
        reportContent.append("│ ").append(padRight(getScrumMasterName(sprint), 16)).append(" │\n");
        reportContent.append("└────────────────────┘\n");
        
        reportContent.append("\nTeam Members:\n");
        for (TeamMember member : sprint.getTeamMembers()) {
            reportContent.append("┌────────────────────┐\n");
            reportContent.append("│ ").append(padRight(member.getName(), 16)).append(" │\n");
            reportContent.append("│ ").append(padRight(member.getClass().getSimpleName(), 16)).append(" │\n");
            reportContent.append("└────────────────────┘\n");
        }
    }
    
    private void buildStatusDashboard(StringBuilder reportContent, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        
        // Count items by state
        Map<String, Long> stateCount = items.stream()
            .collect(Collectors.groupingBy(item -> item.getState().getName(), Collectors.counting()));
        
        reportContent.append("\n== STATUS DASHBOARD ==\n");
        int totalItems = items.size();
        reportContent.append("Total Items: ").append(totalItems).append("\n\n");
        
        // Draw a bar chart
        if (totalItems > 0) {
            stateCount.forEach((state, count) -> {
                int barLength = (int) (20.0 * count / totalItems);
                reportContent.append(padRight(state, 10))
                             .append("│")
                             .append("█".repeat(barLength))
                             .append(" ")
                             .append(count)
                             .append("\n");
            });
        }
    }
    
    private void buildBurndownChart(StringBuilder reportContent, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        int totalItems = items.size();
        
        if (totalItems == 0) {
            reportContent.append("\n== BURNDOWN CHART ==\n");
            reportContent.append("No items to display in burndown chart.\n");
            return;
        }
        
        int completedItems = (int) items.stream().filter(item -> item.getState() instanceof DoneState).count();
        int sprintLength = calculateDuration(sprint.getStartDate(), sprint.getEndDate());
        int daysElapsed = calculateDaysElapsed(sprint.getStartDate(), LocalDate.now());
        
        BurndownChartData chartData = new BurndownChartData(
            totalItems, completedItems, sprintLength, daysElapsed);
        
        reportContent.append("\n== BURNDOWN CHART ==\n");
        reportContent.append("Items remaining to complete:\n");
        
        if (sprintLength <= 0) {
            reportContent.append("No valid sprint duration available.\n");
            return;
        }
        
        // Print day headers
        buildBurndownHeaders(reportContent, sprintLength);
        
        // Build ideal and actual lines
        buildBurndownLines(reportContent, chartData);
        
        // Build visual chart
        buildVisualBurndownChart(reportContent, chartData);
    }
    
    private void buildBurndownHeaders(StringBuilder reportContent, int sprintLength) {
        reportContent.append("     ");
        for (int day = 0; day <= sprintLength; day++) {
            reportContent.append(String.format("%2d ", day));
        }
        reportContent.append("\n");
    }
    
    private void buildBurndownLines(StringBuilder reportContent, BurndownChartData data) {
        int sprintLength = data.getSprintLength();
        
        // Ideal line
        reportContent.append("Ideal");
        for (int day = 0; day <= sprintLength; day++) {
            int expected = data.calculateExpectedItemsRemaining(day);
            reportContent.append(String.format("%2d ", expected));
        }
        reportContent.append("\n");
        
        // Actual line (with question marks for future)
        reportContent.append("Actual");
        for (int day = 0; day <= sprintLength; day++) {
            int actual = data.calculateActualItemsRemaining(day);
            if (actual >= 0) {
                reportContent.append(String.format("%2d ", actual));
            } else {
                reportContent.append(" ? ");
            }
        }
        reportContent.append("\n");
    }
    
    private void buildVisualBurndownChart(StringBuilder reportContent, BurndownChartData data) {
        // Visual representation
        buildChartRows(reportContent, data);
        
        // Chart footer
        buildChartFooter(reportContent, data.getSprintLength());
    }
    
    private void buildChartRows(StringBuilder reportContent, BurndownChartData data) {
        int totalItems = data.getTotalItems();
        int sprintLength = data.getSprintLength();
        
        for (int items = totalItems; items >= 0; items--) {
            reportContent.append(String.format("%3d |", items));
            
            for (int day = 0; day <= sprintLength; day++) {
                int expected = data.calculateExpectedItemsRemaining(day);
                int actual = data.calculateActualItemsRemaining(day);
                
                appendChartCharacter(reportContent, items, expected, actual);
            }
            reportContent.append("\n");
        }
    }
    
    private void appendChartCharacter(StringBuilder reportContent, int items, int expected, int actual) {
        if (actual >= 0) { // Not a future day
            if (items == expected && items == actual) {
                reportContent.append(" X "); // Both ideal and actual
            } else if (items == expected) {
                reportContent.append(" I "); // Ideal only
            } else if (items == actual) {
                reportContent.append(" A "); // Actual only
            } else {
                reportContent.append("   ");
            }
        } else { // Future day
            if (items == expected) {
                reportContent.append(" I "); // Ideal only
            } else {
                reportContent.append("   ");
            }
        }
    }
    
    private void buildChartFooter(StringBuilder reportContent, int sprintLength) {
        reportContent.append("    +");
        reportContent.append("-".repeat((sprintLength + 1) * 3));
        reportContent.append("\n");
        
        reportContent.append("     ");
        for (int day = 0; day <= sprintLength; day++) {
            reportContent.append(String.format("%2d ", day));
        }
        reportContent.append("\n");
    }
    
    private void buildMetadataSection(StringBuilder reportContent) {
        reportContent.append("\n== IMAGE METADATA ==\n");
        reportContent.append("Generated on: ").append(formatDate(LocalDate.now())).append("\n");
        reportContent.append("Format: PNG Image\n");
        reportContent.append("Resolution: 1280x720px\n");
        reportContent.append("Color mode: RGB\n");
    }
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
    
    private String getScrumMasterName(Sprint sprint) {
        return sprint.getScrumMaster() != null ? sprint.getScrumMaster().getName() : "Not assigned";
    }
    
    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    
    private int calculateDuration(LocalDate start, LocalDate end) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }
    
    private int calculateDaysElapsed(LocalDate start, LocalDate current) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, 
                current.isAfter(start) ? current : start);
    }
}
