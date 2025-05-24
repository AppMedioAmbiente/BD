package com.example.firelogin;

public class EventType {
    private Integer id;
    private String name;

    public EventType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
