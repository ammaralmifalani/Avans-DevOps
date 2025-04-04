package com.avans.domain.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.avans.strategy.notification.NotificationStrategy;

@ExtendWith(MockitoExtension.class) // Needed if we use @Mock, even for simple tests
class TeamMemberRolesTest {

    @Mock
    private NotificationStrategy mockNotificationStrategy;

    @Test
    @DisplayName("LeadDeveloper should instantiate and have correct toString")
    void leadDeveloperTest() {
        String name = "Lead Dev";
        LeadDeveloper member = new LeadDeveloper(name);
        assertEquals(name, member.getName());
        assertTrue(member.toString().contains(name));
        assertTrue(member.toString().startsWith("LeadDeveloper"));
        // Test notification integration briefly
        member.addNotificationMethod(mockNotificationStrategy);
        member.update("Test Update");
        verify(mockNotificationStrategy).sendNotification(contains(name));
    }

    @Test
    @DisplayName("ProductOwner should instantiate and have correct toString")
    void productOwnerTest() {
        String name = "Prod Owner";
        ProductOwner member = new ProductOwner(name);
        assertEquals(name, member.getName());
        assertTrue(member.toString().contains(name));
        assertTrue(member.toString().startsWith("ProductOwner"));
         // Test notification integration briefly
        member.addNotificationMethod(mockNotificationStrategy);
        member.update("Test Update");
        verify(mockNotificationStrategy).sendNotification(contains(name));
    }

    @Test
    @DisplayName("ScrumMaster should instantiate and have correct toString")
    void scrumMasterTest() {
        String name = "Scrum Master";
        ScrumMaster member = new ScrumMaster(name);
        assertEquals(name, member.getName());
        assertTrue(member.toString().contains(name));
        assertTrue(member.toString().startsWith("ScrumMaster"));
         // Test notification integration briefly
        member.addNotificationMethod(mockNotificationStrategy);
        member.update("Test Update");
        verify(mockNotificationStrategy).sendNotification(contains(name));
    }

    @Test
    @DisplayName("Tester should instantiate and have correct toString")
    void testerTest() {
        String name = "Testy Tester";
        Tester member = new Tester(name);
        assertEquals(name, member.getName());
        assertTrue(member.toString().contains(name));
        assertTrue(member.toString().startsWith("Tester"));
         // Test notification integration briefly
        member.addNotificationMethod(mockNotificationStrategy);
        member.update("Test Update");
        verify(mockNotificationStrategy).sendNotification(contains(name));
    }

     @Test
    @DisplayName("Developer should instantiate correctly")
    void developerTest() {
        // Developer doesn't override toString, but let's ensure it constructs
        String name = "Dev Eloper";
        Developer member = new Developer(name);
        assertEquals(name, member.getName());
         // Test notification integration briefly
        member.addNotificationMethod(mockNotificationStrategy);
        member.update("Test Update");
        verify(mockNotificationStrategy).sendNotification(contains(name));
    }

    @Test
    @DisplayName("TeamMember should handle multiple notification strategies")
    void teamMemberMultiNotificationTest() {
        Developer member = new Developer("Multi Notif");
        NotificationStrategy strategy1 = mock(NotificationStrategy.class);
        NotificationStrategy strategy2 = mock(NotificationStrategy.class);

        member.addNotificationMethod(strategy1);
        member.addNotificationMethod(strategy2);

        String message = "Important update";
        member.update(message);

        verify(strategy1).sendNotification(contains(message));
        verify(strategy2).sendNotification(contains(message));
    }
}
