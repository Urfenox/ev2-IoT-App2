package com.crizacio.eva2_iot_app2;

import android.text.TextUtils;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


@IgnoreExtraProperties
public class Nota {
    private String id;
    private float nota;
    private String fechaEv;

    public Nota() {
    }

    public Nota(String id, float nota, String fecha) {
        this.id = id;
        this.nota = nota;
        if ((TextUtils.isEmpty(fecha)) || Objects.equals(fecha, "")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            this.fechaEv = formatter.format(date);
        } else {
            this.fechaEv = fecha;
        }
    }

    public String getId() {
        return id;
    }
    public float getNota() {
        return nota;
    }
    public String getFechaEv() {
        return fechaEv;
    }
}
