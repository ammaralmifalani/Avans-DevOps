package com.avans.strategy.notification;

public class EmailNotification implements NotificationStrategy {
    @Override
    public void sendNotification(String message) {
        System.out.println("[Email] " + message);
    }
}