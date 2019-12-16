package com.example.database;

public interface Subject {
    void register(Observer o);
    void unregister(Observer o);
    void notifyObservers();
}
