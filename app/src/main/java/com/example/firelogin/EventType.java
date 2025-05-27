package com.example.firelogin;

import androidx.annotation.NonNull;

public class EventType {
    private Integer id;
    private String name;

    public EventType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
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
