package com.avans.domain.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avans.decorator.IReport;
import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.member.ScrumMaster;
import com.avans.domain.member.TeamMember;
import com.avans.observer.Subject;
import com.avans.strategy.report.IReportStrategy;

public abstract class Sprint extends Subject {
    protected String name;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected List<BacklogItem> backlogItems;
    protected List<TeamMember> sprintTeam;
    protected ScrumMaster scrumMaster;
    protected boolean isStarted;
    protected boolean isFinished;

    public Sprint(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.backlogItems = new ArrayList<>();
        this.sprintTeam = new ArrayList<>();
        this.isStarted = false;
        this.isFinished = false;
    }

    public void addBacklogItem(BacklogItem item) {
        if (isStarted) {
            throw new IllegalStateException("Cannot add backlog items after sprint has started");
        }
        backlogItems.add(item);
    }

    public void removeBacklogItem(BacklogItem item) {
        if (isStarted) {
            throw new IllegalStateException("Cannot remove backlog items after sprint has started");
        }
        backlogItems.remove(item);
    }

    public void addTeamMember(TeamMember m) {
        if (isStarted) {
            throw new IllegalStateException("Cannot add team members after sprint has started");
        }
        sprintTeam.add(m);
    }

    public void setScrumMaster(ScrumMaster master) {
        if (isStarted) {
            throw new IllegalStateException("Cannot change scrum master after sprint has started");
        }
        this.scrumMaster = master;
        // Register scrum master as an observer
        addObserver(master);
    }
    
    public ScrumMaster getScrumMaster() {
        return scrumMaster;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (isStarted) {
            throw new IllegalStateException("Cannot change sprint name after sprint has started");
        }
        this.name = name;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        if (isStarted) {
            throw new IllegalStateException("Cannot change start date after sprint has started");
        }
        this.startDate = startDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        if (isStarted) {
            throw new IllegalStateException("Cannot change end date after sprint has started");
        }
        this.endDate = endDate;
    }

    public void start() {
        if (scrumMaster == null) {
            throw new IllegalStateException("Cannot start sprint without a Scrum Master");
        }
        
        if (sprintTeam.isEmpty()) {
            throw new IllegalStateException("Cannot start sprint without team members");
        }
        
        this.isStarted = true;
        notifyObservers("Sprint '" + name + "' has started.");
    }

    public void finish() {
        if (!isStarted) {
            throw new IllegalStateException("Cannot finish a sprint that hasn't started");
        }
        
        this.isFinished = true;
        notifyObservers("Sprint '" + name + "' has finished.");
    }
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public boolean isFinished() {
        return isFinished;
    }
    
    // Check if the sprint deadline has passed and finish the sprint if needed
    public void checkDeadline() {
        if (isStarted && !isFinished && LocalDate.now().isAfter(endDate)) {
            finish();
        }
    }

    public IReport generateReport(IReportStrategy strategy) {
        return strategy.generate(this);
    }

    public List<BacklogItem> getBacklogItems() {
        return backlogItems;
    }
    
    public List<TeamMember> getTeamMembers() {
        return sprintTeam;
    }
    
    // Check if all backlog items in the sprint are done
    public boolean areAllBacklogItemsDone() {
        if (backlogItems.isEmpty()) {
            return false; // No items means nothing is done
        }
        
        for (BacklogItem item : backlogItems) {
            if (!item.isDone()) {
                return false;
            }
        }
        return true;
    }
}
