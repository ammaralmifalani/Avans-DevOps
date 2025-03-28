package com.avans.strategy.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.avans.decorator.ConcreteReport;
import com.avans.decorator.IReport;
import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.member.TeamMember;
import com.avans.domain.project.Sprint;

public class PngReportStrategy implements IReportStrategy {
    @Override
    public IReport generate(Sprint sprint) {
        StringBuilder reportContent = new StringBuilder();
        
        // Build a graphical style report (simulated here with text)
        reportContent.append("=== PNG SPRINT REPORT (Graphical Format) ===\n\n");
        reportContent.append("Sprint: ").append(sprint.getName()).append("\n");
        reportContent.append("Period: ").append(formatDate(sprint.getStartDate()))
                     .append(" - ").append(formatDate(sprint.getEndDate())).append("\n\n");
        
        // Team visualization section
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
        
        // Progress chart 
        List<BacklogItem> items = sprint.getBacklogItems();
        int todoCount = 0, doingCount = 0, testingCount = 0, doneCount = 0;
        
        for (BacklogItem item : items) {
            String state = item.getState().getName();
            if (state.equals("Todo")) todoCount++;
            else if (state.equals("Doing")) doingCount++;
            else if (state.contains("Test")) testingCount++;
            else if (state.equals("Done")) doneCount++;
        }
        
        reportContent.append("\n== STATUS DASHBOARD ==\n");
        int totalItems = items.size();
        reportContent.append("Total Items: ").append(totalItems).append("\n\n");
        
        // Draw a bar chart
        if (totalItems > 0) {
            int todoBarLength = (int) (20.0 * todoCount / totalItems);
            int doingBarLength = (int) (20.0 * doingCount / totalItems);
            int testingBarLength = (int) (20.0 * testingCount / totalItems);
            int doneBarLength = (int) (20.0 * doneCount / totalItems);
            
            reportContent.append("Todo    │").append("█".repeat(todoBarLength)).append(" ").append(todoCount).append("\n");
            reportContent.append("Doing   │").append("█".repeat(doingBarLength)).append(" ").append(doingCount).append("\n");
            reportContent.append("Testing │").append("█".repeat(testingBarLength)).append(" ").append(testingCount).append("\n");
            reportContent.append("Done    │").append("█".repeat(doneBarLength)).append(" ").append(doneCount).append("\n");
        }
        
        // Burndown chart
        reportContent.append("\n== BURNDOWN CHART ==\n");
        reportContent.append("Items remaining to complete:\n");
        
        int itemsRemaining = totalItems - doneCount;
        int sprintLength = calculateSprintDays(sprint.getStartDate(), sprint.getEndDate());
        int daysElapsed = calculateDaysElapsed(sprint.getStartDate(), LocalDate.now());
        
        if (sprintLength > 0) {
            reportContent.append("Day 0: ").append(totalItems).append("\n");
            
            // Simulate a burndown line (simplistic model)
            for (int day = 1; day <= sprintLength; day++) {
                int expectedRemaining = Math.max(0, (int)(totalItems * (1 - (double)day/sprintLength)));
                
                if (day <= daysElapsed) {
                    // For past days, show actual vs ideal
                    int actualRemaining = day == daysElapsed ? itemsRemaining : 
                                         Math.min(totalItems, itemsRemaining + (daysElapsed - day));
                    
                    reportContent.append("Day ").append(day).append(": ")
                                 .append(actualRemaining).append(" (actual), ")
                                 .append(expectedRemaining).append(" (ideal)\n");
                } else {
                    // For future days, show only ideal
                    reportContent.append("Day ").append(day).append(": ")
                                 .append("? (actual), ")
                                 .append(expectedRemaining).append(" (ideal)\n");
                }
            }
        }
        
        return new ConcreteReport(reportContent.toString());
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
    
    private int calculateSprintDays(LocalDate start, LocalDate end) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }
    
    private int calculateDaysElapsed(LocalDate start, LocalDate current) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, 
                current.isAfter(start) ? current : start);
    }
}
