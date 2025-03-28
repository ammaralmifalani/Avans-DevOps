package com.avans.domain.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avans.decorator.IReport;
import com.avans.domain.backlog.BacklogItem;
import com.avans.domain.member.ScrumMaster;
import com.avans.domain.member.TeamMember;
import com.avans.strategy.report.IReportStrategy;

public abstract class Sprint {
    protected String name;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected List<BacklogItem> backlogItems;
    protected List<TeamMember> sprintTeam;
    protected ScrumMaster scrumMaster;

    public Sprint(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.backlogItems = new ArrayList<>();
        this.sprintTeam = new ArrayList<>();
    }

    public void addBacklogItem(BacklogItem item) {
        backlogItems.add(item);
    }

    public void removeBacklogItem(BacklogItem item) {
        backlogItems.remove(item);
    }

    public void addTeamMember(TeamMember m) {
        sprintTeam.add(m);
    }

    public void setScrumMaster(ScrumMaster master) {
        this.scrumMaster = master;
    }
    public ScrumMaster getScrumMaster() {
        return scrumMaster;
    }

    public String getName() {
        return name;
    }

    public void start() {
        // start logic
    }

    public void finish() {
        // finish logic
    }

    public IReport generateReport(IReportStrategy strategy) {
        return strategy.generate(this);
    }

    public List<BacklogItem> getBacklogItems() {
        return backlogItems;
    }
}