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
        
        // Build different sections of the report
        appendHeaderSection(reportContent, sprint);
        appendTeamSection(reportContent, sprint);
        appendBacklogSection(reportContent, sprint);
        appendProgressSection(reportContent, sprint);
        appendMetadataSection(reportContent);
        
        return new ConcreteReport(reportContent.toString());
    }
    
    private void appendHeaderSection(StringBuilder builder, Sprint sprint) {
        builder.append("=== PDF SPRINT REPORT ===\n\n");
        builder.append("Sprint: ").append(sprint.getName()).append("\n");
        builder.append("Duration: ").append(formatDate(sprint.getStartDate()))
               .append(" to ").append(formatDate(sprint.getEndDate())).append("\n");
        builder.append("Duration (days): ").append(calculateDuration(sprint.getStartDate(), sprint.getEndDate())).append("\n\n");
    }
    
    private void appendTeamSection(StringBuilder builder, Sprint sprint) {
        builder.append("== TEAM COMPOSITION ==\n");
        builder.append("Scrum Master: ").append(getScrumMasterName(sprint)).append("\n");
        builder.append("Team Members:\n");
        
        for (TeamMember member : sprint.getTeamMembers()) {
            builder.append("- ").append(member.getName())
                   .append(" (").append(member.getClass().getSimpleName()).append(")\n");
        }
    }
    
    private void appendBacklogSection(StringBuilder builder, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        
        builder.append("\n== BACKLOG ITEMS ==\n");
        builder.append("Total Items: ").append(items.size()).append("\n");
        
        // Count items by state
        Map<String, Long> stateCount = items.stream()
            .collect(Collectors.groupingBy(item -> item.getState().getName(), Collectors.counting()));
        
        builder.append("\nItems by Status:\n");
        stateCount.forEach((state, count) -> 
            builder.append("- ").append(state).append(": ").append(count).append("\n")
        );
        
        appendItemDetails(builder, items);
    }
    
    private void appendItemDetails(StringBuilder builder, List<BacklogItem> items) {
        builder.append("\nItem Details:\n");
        
        for (BacklogItem item : items) {
            builder.append("- ").append(item.getTitle())
                   .append(" [").append(item.getState().getName()).append("] ");
            
            if (item.getAssignedDeveloper() != null) {
                builder.append("- Assigned to: ").append(item.getAssignedDeveloper().getName());
            }
            builder.append("\n");
            
            appendActivities(builder, item);
        }
    }
    
    private void appendActivities(StringBuilder builder, BacklogItem item) {
        if (item.getActivityCount() <= 0) {
            return;
        }
        
        builder.append("  Activities:\n");
        for (var activity : item.getActivities()) {
            builder.append("  * ").append(activity.getTitle())
                   .append(" (").append(activity.getTotalEstimatedHours()).append("h) ");
            builder.append(activity.isDone() ? "[Completed]" : "[In Progress]");
            builder.append("\n");
        }
    }
    
    private void appendProgressSection(StringBuilder builder, Sprint sprint) {
        List<BacklogItem> items = sprint.getBacklogItems();
        int totalItems = items.size();
        int completedItems = countDoneItems(items);
        
        builder.append("\n== SPRINT PROGRESS ==\n");
        double completionPercentage = totalItems > 0 ? (double) completedItems / totalItems * 100 : 0;
        builder.append("Completion: ").append(String.format("%.1f%%", completionPercentage)).append("\n");
        
        appendBurndownChart(builder, sprint, totalItems, completedItems);
    }
    
    private int countDoneItems(List<BacklogItem> items) {
        return (int) items.stream()
                .filter(item -> item.getState() instanceof DoneState)
                .count();
    }
    
    private void appendBurndownChart(StringBuilder builder, Sprint sprint, int totalItems, int completedItems) {
        int totalDays = calculateDuration(sprint.getStartDate(), sprint.getEndDate());
        int daysElapsed = calculateDaysElapsed(sprint.getStartDate(), LocalDate.now());
        
        builder.append("\n== BURNDOWN CHART ==\n");
        
        // Print days header
        builder.append("Days: ");
        for (int i = 0; i <= totalDays; i++) {
            builder.append(String.format("%3d", i));
        }
        builder.append("\n");
        
        appendIdealBurndown(builder, totalItems, totalDays);
        appendActualBurndown(builder, totalItems, completedItems, daysElapsed, totalDays);
        
        builder.append("Current completed work: ").append(completedItems)
               .append(" of ").append(totalItems).append(" items\n");
    }
    
    private void appendIdealBurndown(StringBuilder builder, int totalItems, int totalDays) {
        builder.append("Ideal: ");
        for (int i = 0; i <= totalDays; i++) {
            int remaining = totalItems;
            if (totalDays > 0) { // Avoid division by zero
                remaining = Math.max(0, totalItems - (int)((double)i / totalDays * totalItems));
            }
            builder.append(String.format("%3d", remaining));
        }
        builder.append("\n");
    }
    
    private void appendActualBurndown(StringBuilder builder, int totalItems, int completedItems, 
                                    int daysElapsed, int totalDays) {
        builder.append("Actual: ");
        for (int i = 0; i <= totalDays; i++) {
            if (i <= daysElapsed) {
                int actualRemaining = totalItems;
                if (daysElapsed > 0) { // Fixed division by zero issue
                    actualRemaining = totalItems - (int)((double)i / daysElapsed * completedItems);
                }
                if (i == daysElapsed) {
                    actualRemaining = totalItems - completedItems;
                }
                builder.append(String.format("%3d", actualRemaining));
            } else {
                builder.append("  ?");
            }
        }
        builder.append("\n");
    }
    
    private void appendMetadataSection(StringBuilder builder) {
        builder.append("\n== DOCUMENT METADATA ==\n");
        builder.append("Generated on: ").append(formatDate(LocalDate.now())).append("\n");
        builder.append("Format: PDF Report\n");
        builder.append("Version: 1.0\n");
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
