package com.crizacio.eva2_iot_app2;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;


@IgnoreExtraProperties
public class Nota {
    private String id;
    private float nota;
    private String fechaEv;

    public Nota() {
    }

    public Nota(String id, float nota) {
        this.id = id;
        this.nota = nota;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.fechaEv = formatter.format(date);
    }

    public float getNota() {
        return nota;
    }
    public String getFechaEv() {
        return fechaEv;
    }
}
