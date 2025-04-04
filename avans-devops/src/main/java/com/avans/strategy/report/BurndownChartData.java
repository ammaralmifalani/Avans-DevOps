package com.avans.strategy.report;

/**
 * Data class to hold burndown chart configuration and data
 */
public class BurndownChartData {
    private final int totalItems;
    private final int completedItems;
    private final int itemsRemaining;
    private final int sprintLength;
    private final int daysElapsed;
    
    public BurndownChartData(int totalItems, int completedItems, int sprintLength, int daysElapsed) {
        this.totalItems = totalItems;
        this.completedItems = completedItems;
        this.itemsRemaining = totalItems - completedItems;
        this.sprintLength = sprintLength;
        this.daysElapsed = daysElapsed;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
    
    public int getCompletedItems() {
        return completedItems;
    }
    
    public int getItemsRemaining() {
        return itemsRemaining;
    }
    
    public int getSprintLength() {
        return sprintLength;
    }
    
    public int getDaysElapsed() {
        return daysElapsed;
    }
    
    public int calculateExpectedItemsRemaining(int day) {
        if (sprintLength <= 0) {
            return totalItems;
        }
        return totalItems - (int)((double)day / sprintLength * totalItems);
    }
    
    public int calculateActualItemsRemaining(int day) {
        if (daysElapsed <= 0) {
            return totalItems;
        }
        
        if (day > daysElapsed) {
            return -1; // Indicate future day (for ? display)
        }
        
        if (day == daysElapsed) {
            return itemsRemaining;
        }
        
        return totalItems - (int)((double)day / daysElapsed * completedItems);
    }
}
