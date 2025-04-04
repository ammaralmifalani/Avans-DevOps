package com.avans.domain.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; // Keep this import for verify/mock calls

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.domain.member.Developer;
import com.avans.domain.member.ProductOwner;
import com.avans.domain.member.TeamMember;

@ExtendWith(MockitoExtension.class)
class ProjectTest {

    private Project project;
    private String projectName = "Test Project";

    @Mock
    private Sprint mockSprint;

    @Mock
    private TeamMember mockTeamMember;

    @Mock
    private ProductOwner mockProductOwner;

    @BeforeEach
    void setUp() {
        project = new Project(projectName);
    }

    @Test
    @DisplayName("Project should initialize with correct name and empty lists")
    void projectInitialization() {
        assertEquals(projectName, project.getProjectName());
        assertNotNull(project.getTeamMembers());
        assertTrue(project.getTeamMembers().isEmpty());
        assertNotNull(project.getSprints());
        assertTrue(project.getSprints().isEmpty());
        assertNull(project.getProductOwner());
    }

    @Test
    @DisplayName("Should add Sprint to the project")
    void addSprint() {
        project.addSprint(mockSprint);
        assertEquals(1, project.getSprints().size());
        assertTrue(project.getSprints().contains(mockSprint));
    }

    @Test
    @DisplayName("Should add TeamMember to the project")
    void addTeamMember() {
        project.addTeamMember(mockTeamMember);
        assertEquals(1, project.getTeamMembers().size());
        assertTrue(project.getTeamMembers().contains(mockTeamMember));
    }

     @Test
    @DisplayName("Setting ProductOwner should also add them to team members")
    void setProductOwner() {
        // Arrange
        // String poName = "Owner";
        // when(mockProductOwner.getName()).thenReturn(poName); // REMOVED UNNECESSARY STUBBING

        // Act
        project.setProductOwner(mockProductOwner);

        // Assert
        assertEquals(mockProductOwner, project.getProductOwner());
        assertEquals(1, project.getTeamMembers().size(), "PO should be added to team members list");
        assertTrue(project.getTeamMembers().contains(mockProductOwner), "Team members list should contain the PO");
    }

    @Test
    @DisplayName("Should retrieve added sprints")
    void getSprints() {
        project.addSprint(mockSprint);
        Sprint anotherSprint = mock(Sprint.class);
        project.addSprint(anotherSprint);

        assertEquals(2, project.getSprints().size());
        assertTrue(project.getSprints().contains(mockSprint));
        assertTrue(project.getSprints().contains(anotherSprint));
    }

    @Test
    @DisplayName("Should retrieve added team members")
    void getTeamMembers() {
        project.addTeamMember(mockTeamMember);
        TeamMember anotherMember = new Developer("Dev2");
        project.addTeamMember(anotherMember);

        assertEquals(2, project.getTeamMembers().size());
        assertTrue(project.getTeamMembers().contains(mockTeamMember));
        assertTrue(project.getTeamMembers().contains(anotherMember));
    }
}
