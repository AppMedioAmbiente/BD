package com.example.firelogin.ui.groups;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Date;

public class MensajeRecibir extends Mensaje {
    private Timestamp hora;


    public MensajeRecibir() {
    }

    public MensajeRecibir(String mensaje, String nombre, String type_mensaje, Timestamp hora) {
        super(mensaje, nombre, type_mensaje);
        this.hora = hora;
    }

    public Timestamp getHora() {
        return hora;
    }

    public void setHora(Timestamp hora) {
        this.hora = hora;
    }
}
