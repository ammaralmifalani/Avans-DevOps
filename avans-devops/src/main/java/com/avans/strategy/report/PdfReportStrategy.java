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

public class PdfReportStrategy implements IReportStrategy {
    @Override
    public IReport generate(Sprint sprint) {
        StringBuilder reportContent = new StringBuilder();
        
        // Build a professional PDF-style report
        reportContent.append("=== PDF SPRINT REPORT ===\n\n");
        reportContent.append("Sprint: ").append(sprint.getName()).append("\n");
        reportContent.append("Duration: ").append(formatDate(sprint.getStartDate()))
                     .append(" to ").append(formatDate(sprint.getEndDate())).append("\n");
        reportContent.append("Duration (days): ").append(calculateDuration(sprint.getStartDate(), sprint.getEndDate())).append("\n\n");
        
        // Team members section
        reportContent.append("== TEAM COMPOSITION ==\n");
        reportContent.append("Scrum Master: ").append(getScrumMasterName(sprint)).append("\n");
        reportContent.append("Team Members:\n");
        for (TeamMember member : sprint.getTeamMembers()) {
            reportContent.append("- ").append(member.getName())
                         .append(" (").append(member.getClass().getSimpleName()).append(")\n");
        }
        
        // Backlog items section
        reportContent.append("\n== BACKLOG ITEMS ==\n");
        List<BacklogItem> items = sprint.getBacklogItems();
        reportContent.append("Total Items: ").append(items.size()).append("\n");
        
        // Count items by state
        Map<String, Long> stateCount = items.stream()
            .collect(Collectors.groupingBy(item -> item.getState().getName(), Collectors.counting()));
        
        reportContent.append("\nItems by Status:\n");
        stateCount.forEach((state, count) -> 
            reportContent.append("- ").append(state).append(": ").append(count).append("\n")
        );
        
        // List items
        reportContent.append("\nItem Details:\n");
        for (BacklogItem item : items) {
            reportContent.append("- ").append(item.getTitle())
                         .append(" [").append(item.getState().getName()).append("] ");
            
            if (item.getAssignedDeveloper() != null) {
                reportContent.append("- Assigned to: ").append(item.getAssignedDeveloper().getName());
            }
            reportContent.append("\n");
            
            // List activities
            if (item.getActivityCount() > 0) {
                reportContent.append("  Activities:\n");
                for (var activity : item.getActivities()) {
                    reportContent.append("  * ").append(activity.getTitle())
                                 .append(" (").append(activity.getTotalEstimatedHours()).append("h) ");
                    if (activity.isDone()) {
                        reportContent.append("[Completed]");
                    } else {
                        reportContent.append("[In Progress]");
                    }
                    reportContent.append("\n");
                }
            }
        }
        
        // Sprint progress
        reportContent.append("\n== SPRINT PROGRESS ==\n");
        int totalItems = items.size();
        int completedItems = (int) items.stream().filter(item -> item.getState() instanceof DoneState).count();
        double completionPercentage = totalItems > 0 ? (double) completedItems / totalItems * 100 : 0;
        
        reportContent.append("Completion: ").append(String.format("%.1f%%", completionPercentage)).append("\n");
        
        // Burndown chart (ASCII art)
        reportContent.append("\n== BURNDOWN CHART ==\n");
        int totalDays = calculateDuration(sprint.getStartDate(), sprint.getEndDate());
        int daysElapsed = calculateDaysElapsed(sprint.getStartDate(), LocalDate.now());
        
        // Header
        reportContent.append("Days: ");
        for (int i = 0; i <= totalDays; i++) {
            reportContent.append(String.format("%3d", i));
        }
        reportContent.append("\n");
        
        // Ideal burndown
        reportContent.append("Ideal: ");
        for (int i = 0; i <= totalDays; i++) {
            int remaining = totalItems;
            if (totalDays > 0) { // Avoid division by zero
                remaining = Math.max(0, totalItems - (int)((double)i / totalDays * totalItems));
            }
            reportContent.append(String.format("%3d", remaining));
        }
        reportContent.append("\n");
        
        // Actual burndown
        reportContent.append("Actual: ");
        for (int i = 0; i <= totalDays; i++) {
            if (i <= daysElapsed) {
                // For past days, show a simulated actual value (just for demonstration)
                int actualRemaining = totalItems;
                if (daysElapsed > 0) { // Fixed division by zero issue
                    actualRemaining = totalItems - (int)((double)i / daysElapsed * completedItems);
                }
                if (i == daysElapsed) actualRemaining = totalItems - completedItems;
                reportContent.append(String.format("%3d", actualRemaining));
            } else {
                reportContent.append("  ?");
            }
        }
        reportContent.append("\n");
        
        // Current progress
        reportContent.append("Current completed work: ").append(completedItems).append(" of ").append(totalItems).append(" items\n");
        
        // Additional metadata
        reportContent.append("\n== DOCUMENT METADATA ==\n");
        reportContent.append("Generated on: ").append(formatDate(LocalDate.now())).append("\n");
        reportContent.append("Format: PDF Report\n");
        reportContent.append("Version: 1.0\n");
        
        return new ConcreteReport(reportContent.toString());
    }
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    
    private String getScrumMasterName(Sprint sprint) {
        return sprint.getScrumMaster() != null ? sprint.getScrumMaster().getName() : "Not assigned";
    }
    
    private int calculateDuration(LocalDate start, LocalDate end) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }
    
    private int calculateDaysElapsed(LocalDate start, LocalDate current) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, 
                current.isAfter(start) ? current : start);
    }
}
