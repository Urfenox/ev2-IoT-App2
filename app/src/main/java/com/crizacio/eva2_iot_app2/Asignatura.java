package com.crizacio.eva2_iot_app2;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Asignatura {
    private String nombre;
    private String rut;

    public Asignatura() {}

    public Asignatura(String nombre, String rut) {
        this.nombre = nombre;
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRUT() {
        return rut;
    }
}
