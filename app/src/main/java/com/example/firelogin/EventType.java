package com.example.firelogin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
