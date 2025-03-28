package com.avans.domain.member;

import java.util.ArrayList;
import java.util.List;

import com.avans.observer.IObserver;
import com.avans.strategy.notification.NotificationStrategy;

public abstract class TeamMember implements IObserver  {
     protected String name;
    protected List<NotificationStrategy> notificationMethods;

    public TeamMember(String name) {
        this.name = name;
        this.notificationMethods = new ArrayList<>();
    }

    @Override
    public void update(String message) {
        // Verstuur naar alle ingestelde notificatiemethodes
        for (NotificationStrategy strategy : notificationMethods) {
            strategy.sendNotification("[" + name + "] " + message);
        }
    }

    public void addNotificationMethod(NotificationStrategy strategy) {
        notificationMethods.add(strategy);
    }

    public String getName() {
        return name;
    }
}
