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
        
        // Build report sections
        addHeaderSection(reportContent, sprint);
        addTeamSection(reportContent, sprint);
        addBacklogSection(reportContent, sprint);
        addProgressSection(reportContent, sprint);
        addBurndownSection(reportContent, sprint);
        addMetadataSection(reportContent);
        
        return new ConcreteReport(reportContent.toString());
    }
    
    private void addHeaderSection(StringBuilder reportContent, Sprint sprint) {
        reportContent.append("=== PDF SPRINT REPORT ===\n\n");
        reportContent.append("Sprint: ").append(sprint.getName()).append("\n");
        reportContent.append("Duration: ").append(formatDate(sprint.getStartDate()))
                     .append(" to ").append(formatDate(sprint.getEndDate())).append("\n");
        reportContent.append("Duration (days): ").append(calculateDuration(sprint.getStartDate(), sprint.getEndDate())).append("\n\n");
    }
    
    private void addTeamSection(StringBuilder reportContent, Sprint sprint) {
        reportContent.append("== TEAM COMPOSITION ==\n");
        reportContent.append("Scrum Master: ").append(getScrumMasterName(sprint)).append("\n");
        reportContent.append("Team Members:\n");
        
        for (TeamMember member : sprint.getTeamMembers()) {
            reportContent.append("- ").append(member.getName())
                         .append(" (").append(member.getClass().getSimpleName()).append(")\n");
        }
    }
    
    private void addBacklogSection(StringBuilder reportContent, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        
        reportContent.append("\n== BACKLOG ITEMS ==\n");
        reportContent.append("Total Items: ").append(items.size()).append("\n");
        
        // Count items by state
        addItemStatusCounts(reportContent, items);
        
        // List items
        addItemDetails(reportContent, items);
    }
    
    private void addItemStatusCounts(StringBuilder reportContent, List<BacklogItem> items) {
        Map<String, Long> stateCount = items.stream()
            .collect(Collectors.groupingBy(item -> item.getState().getName(), Collectors.counting()));
        
        reportContent.append("\nItems by Status:\n");
        stateCount.forEach((state, count) -> 
            reportContent.append("- ").append(state).append(": ").append(count).append("\n")
        );
    }
    
    private void addItemDetails(StringBuilder reportContent, List<BacklogItem> items) {
        reportContent.append("\nItem Details:\n");
        for (BacklogItem item : items) {
            reportContent.append("- ").append(item.getTitle())
                         .append(" [").append(item.getState().getName()).append("] ");
            
            if (item.getAssignedDeveloper() != null) {
                reportContent.append("- Assigned to: ").append(item.getAssignedDeveloper().getName());
            }
            reportContent.append("\n");
            
            addActivityDetails(reportContent, item);
        }
    }
    
    private void addActivityDetails(StringBuilder reportContent, BacklogItem item) {
        if (item.getActivityCount() > 0) {
            reportContent.append("  Activities:\n");
            for (var activity : item.getActivities()) {
                reportContent.append("  * ").append(activity.getTitle())
                             .append(" (").append(activity.getTotalEstimatedHours()).append("h) ");
                reportContent.append(activity.isDone() ? "[Completed]" : "[In Progress]");
                reportContent.append("\n");
            }
        }
    }
    
    private void addProgressSection(StringBuilder reportContent, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        int totalItems = items.size();
        int completedItems = (int) items.stream()
                .filter(item -> item.getState() instanceof DoneState)
                .count();
        
        reportContent.append("\n== SPRINT PROGRESS ==\n");
        
        if (totalItems > 0) {
            double completionPercentage = (double) completedItems / totalItems * 100;
            reportContent.append("Completion: ").append(String.format("%.1f%%", completionPercentage)).append("\n");
        } else {
            reportContent.append("No items in sprint.\n");
        }
    }
    
    private void addBurndownSection(StringBuilder reportContent, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        int totalItems = items.size();
        
        if (totalItems == 0) {
            reportContent.append("\n== BURNDOWN CHART ==\n");
            reportContent.append("No items to display in burndown chart.\n");
            return;
        }
        
        int completedItems = (int) items.stream()
                .filter(item -> item.getState() instanceof DoneState)
                .count();
        int totalDays = calculateDuration(sprint.getStartDate(), sprint.getEndDate());
        int daysElapsed = calculateDaysElapsed(sprint.getStartDate(), LocalDate.now());
        
        reportContent.append("\n== BURNDOWN CHART ==\n");
        
        // Add chart details
        addBurndownChartHeader(reportContent, totalDays);
        addIdealBurndownLine(reportContent, totalItems, totalDays);
        addActualBurndownLine(reportContent, totalItems, completedItems, daysElapsed, totalDays);
        
        // Current progress
        reportContent.append("Current completed work: ").append(completedItems)
                     .append(" of ").append(totalItems).append(" items\n");
    }
    
    private void addBurndownChartHeader(StringBuilder reportContent, int totalDays) {
        reportContent.append("Days: ");
        for (int i = 0; i <= totalDays; i++) {
            reportContent.append(String.format("%3d", i));
        }
        reportContent.append("\n");
    }
    
    private void addIdealBurndownLine(StringBuilder reportContent, int totalItems, int totalDays) {
        reportContent.append("Ideal: ");
        
        if (totalDays > 0) {
            for (int i = 0; i <= totalDays; i++) {
                int remaining = Math.max(0, totalItems - (int)((double)i / totalDays * totalItems));
                reportContent.append(String.format("%3d", remaining));
            }
        } else {
            reportContent.append(String.format("%3d", totalItems));
        }
        
        reportContent.append("\n");
    }
    
    private void addActualBurndownLine(StringBuilder reportContent, int totalItems, 
                                      int completedItems, int daysElapsed, int totalDays) {
        reportContent.append("Actual: ");
        
        for (int i = 0; i <= totalDays; i++) {
            if (i <= daysElapsed) {
                int actualRemaining = totalItems;
                
                if (daysElapsed > 0) {
                    actualRemaining = totalItems - (int)((double)i / daysElapsed * completedItems);
                }
                
                if (i == daysElapsed) {
                    actualRemaining = totalItems - completedItems;
                }
                
                reportContent.append(String.format("%3d", actualRemaining));
            } else {
                reportContent.append("  ?");
            }
        }
        
        reportContent.append("\n");
    }
    
    private void addMetadataSection(StringBuilder reportContent) {
        reportContent.append("\n== DOCUMENT METADATA ==\n");
        reportContent.append("Generated on: ").append(formatDate(LocalDate.now())).append("\n");
        reportContent.append("Format: PDF Report\n");
        reportContent.append("Version: 1.0\n");
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
