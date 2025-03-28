package com.avans.strategy.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.avans.decorator.ConcreteReport;
import com.avans.decorator.IReport;
import com.avans.domain.backlog.BacklogItem;
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
                     .append(" to ").append(formatDate(sprint.getEndDate())).append("\n\n");
        
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
        int todoCount = 0, doingCount = 0, testingCount = 0, doneCount = 0;
        
        for (BacklogItem item : items) {
            String state = item.getState().getName();
            if (state.equals("Todo")) todoCount++;
            else if (state.equals("Doing")) doingCount++;
            else if (state.contains("Test")) testingCount++;
            else if (state.equals("Done")) doneCount++;
            
            reportContent.append("- ").append(item.getTitle())
                         .append(" [").append(state).append("] ");
            
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
        int completedItems = doneCount;
        double completionPercentage = totalItems > 0 ? (double) completedItems / totalItems * 100 : 0;
        
        reportContent.append("Completion: ").append(String.format("%.1f%%", completionPercentage)).append("\n");
        reportContent.append("Items by Status:\n");
        reportContent.append("- Todo: ").append(todoCount).append("\n");
        reportContent.append("- Doing: ").append(doingCount).append("\n");
        reportContent.append("- Testing: ").append(testingCount).append("\n");
        reportContent.append("- Done: ").append(doneCount).append("\n");
        
        // Simple burndown chart (ASCII art)
        reportContent.append("\n== BURNDOWN CHART ==\n");
        reportContent.append("TODO: Generate visual burndown chart\n");
        reportContent.append("Current completed work: ").append(completedItems).append(" of ").append(totalItems).append(" items\n");
        
        return new ConcreteReport(reportContent.toString());
    }
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    
    private String getScrumMasterName(Sprint sprint) {
        return sprint.getScrumMaster() != null ? sprint.getScrumMaster().getName() : "Not assigned";
    }
}
