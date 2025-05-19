package com.example.firelogin.urlRequest;

public class Activity<T> {
    private T activity;

    public Activity(T contenido) {
        this.activity = contenido;
    }

    public T getContenido() {
        return activity;
    }

    public void setContenido(T contenido) {
        this.activity = contenido;
    }
}
