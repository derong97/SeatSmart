package com.example.seatsmart;

public abstract class Facility {
    String id;
    String status;

    Facility(String id, String status){
        this.id = id;
        this.status = status;
    }

    abstract void update();

    public String getId() {
        return this.id;
    }

    public String getStatus() {
        return this.status;
    }
}
