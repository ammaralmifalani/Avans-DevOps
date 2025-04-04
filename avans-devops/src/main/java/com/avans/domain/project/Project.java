package com.avans.domain.project;

import java.util.ArrayList;
import java.util.List;

import com.avans.domain.member.ProductOwner;
import com.avans.domain.member.TeamMember;

public class Project  {
    private String projectName;
    private String projectDescription;
    private List <TeamMember> teamMembers;
    private List <Sprint> sprints;
    private ProductOwner productOwner;

    public Project (String projectName) {
        this.projectName = projectName;
        this.teamMembers = new ArrayList<>();
        this.sprints = new ArrayList<>();
    }
    public void addSprint(Sprint s) {
        sprints.add(s);
    }

    public void addTeamMember(TeamMember m) {
        teamMembers.add(m);
    }

    public String getProjectName() {
        return projectName;
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setProductOwner(ProductOwner owner) {
        this.productOwner = owner;
        addTeamMember(owner);
    }

    public ProductOwner  getProductOwner() {
        return productOwner;
    }
}
