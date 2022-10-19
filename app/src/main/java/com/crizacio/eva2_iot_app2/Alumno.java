package com.crizacio.eva2_iot_app2;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Alumno {
    private String rut;
    private String nombre;
    private String correo;
    private String asignatura;

    public Alumno() {}

    public Alumno(String rut, String nombre, String correo, String asignatura) {
        this.rut = rut;
        this.nombre = nombre;
        this.correo = correo;
        this.asignatura = asignatura;
    }

    public String getRUT() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getAsignatura() {
        return asignatura;
    }
}
