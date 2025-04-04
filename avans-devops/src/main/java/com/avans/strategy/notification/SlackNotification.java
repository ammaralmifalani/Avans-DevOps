package com.avans.strategy.notification;

public class SlackNotification implements NotificationStrategy {
    @Override
    public void sendNotification(String message) {
        System.out.println("[Slack] " + message);
    }

}
