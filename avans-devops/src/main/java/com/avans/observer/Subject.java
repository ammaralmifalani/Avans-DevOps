package com.avans.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    private List<IObserver> observers = new ArrayList<>();

    public void addObserver(IObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(IObserver obs) {
        observers.remove(obs);
    }

    public void notifyObservers(String message) {
        for (IObserver obs : observers) {
            obs.update(message);
        }
    }
}
