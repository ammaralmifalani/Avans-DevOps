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
        
        // Burndown chart
        reportContent.append("\n== BURNDOWN CHART ==\n");
        reportContent.append("Items remaining to complete:\n");
        
        int completedItems = (int) items.stream().filter(item -> item.getState() instanceof DoneState).count();
        int itemsRemaining = totalItems - completedItems;
        int sprintLength = calculateDuration(sprint.getStartDate(), sprint.getEndDate());
        int daysElapsed = calculateDaysElapsed(sprint.getStartDate(), LocalDate.now());
        
        if (sprintLength > 0) {
            // Print day header
            reportContent.append("     ");
            for (int day = 0; day <= sprintLength; day++) {
                reportContent.append(String.format("%2d ", day));
            }
            reportContent.append("\n");
            
            // Ideal line
            reportContent.append("Ideal");
            for (int day = 0; day <= sprintLength; day++) {
                int expected = totalItems - (int)((double)day / sprintLength * totalItems);
                reportContent.append(String.format("%2d ", expected));
            }
            reportContent.append("\n");
            
            // Actual line (with question marks for future)
            reportContent.append("Actual");
            for (int day = 0; day <= sprintLength; day++) {
                if (day <= daysElapsed) {
                    // For past days, interpolate a simulated value
                    int actual = totalItems;
                    if (daysElapsed > 0) {
                        actual = totalItems - (int)((double)day / daysElapsed * completedItems);
                    }
                    if (day == daysElapsed) {
                        actual = itemsRemaining;
                    }
                    reportContent.append(String.format("%2d ", actual));
                } else {
                    reportContent.append(" ? ");
                }
            }
            reportContent.append("\n");
            
            // Visual representation
            for (int remainingItems = totalItems; remainingItems >= 0; remainingItems--) {
                reportContent.append(String.format("%3d |", remainingItems));
                
                for (int day = 0; day <= sprintLength; day++) {
                    int expected = totalItems - (int)((double)day / sprintLength * totalItems);
                    
                    if (day <= daysElapsed) {
                        int actual = totalItems;
                        if (daysElapsed > 0) {
                            actual = totalItems - (int)((double)day / daysElapsed * completedItems);
                        }
                        if (day == daysElapsed) {
                            actual = itemsRemaining;
                        }
                        
                        if (remainingItems == expected && remainingItems == actual) {
                            reportContent.append(" X "); // Both ideal and actual
                        } else if (remainingItems == expected) {
                            reportContent.append(" I "); // Ideal only
                        } else if (remainingItems == actual) {
                            reportContent.append(" A "); // Actual only
                        } else {
                            reportContent.append("   ");
                        }
                    } else {
                        if (remainingItems == expected) {
                            reportContent.append(" I "); // Ideal only
                        } else {
                            reportContent.append("   ");
                        }
                    }
                }
                reportContent.append("\n");
            }
            
            reportContent.append("    +");
            reportContent.append("-".repeat((sprintLength + 1) * 3));
            reportContent.append("\n");
            
            reportContent.append("     ");
            for (int day = 0; day <= sprintLength; day++) {
                reportContent.append(String.format("%2d ", day));
            }
            reportContent.append("\n");
        }
        
        // Additional metadata specific to PNG format
        reportContent.append("\n== IMAGE METADATA ==\n");
        reportContent.append("Generated on: ").append(formatDate(LocalDate.now())).append("\n");
        reportContent.append("Format: PNG Image\n");
        reportContent.append("Resolution: 1280x720px\n");
        reportContent.append("Color mode: RGB\n");
        
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
    
    private int calculateDuration(LocalDate start, LocalDate end) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }
    
    private int calculateDaysElapsed(LocalDate start, LocalDate current) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, 
                current.isAfter(start) ? current : start);
    }
}
