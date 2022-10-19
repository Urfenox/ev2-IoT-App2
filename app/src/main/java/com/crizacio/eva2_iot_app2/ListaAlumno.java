package com.crizacio.eva2_iot_app2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ListaAlumno extends ArrayAdapter<Alumno> {
    private Activity context;
    List<Alumno> alumnos;

    public ListaAlumno(Activity context, List<Alumno> alumnos) {
        super(context, R.layout.layout_alumno_lista, alumnos);
        this.context = context;
        this.alumnos = alumnos;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * RESUMEN:
         * Esto permite generar un objeto el cual tiene un Titulo y un Subtitulo
         * Este objeto es compatible para ser ingresado en un ListView
         * Como resultado podras ver un objeto con un texto (el Nombre) y un subtexto (la Asignatura)
         * Esto queda bonito.
         * */
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_alumno_lista, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.txtTitulo);
        TextView textViewAsign = (TextView) listViewItem.findViewById(R.id.txtSubtitulo);

        Alumno alumno = alumnos.get(position);
        textViewName.setText(alumno.getNombre());
        textViewAsign.setText(alumno.getAsignatura());

        return listViewItem;
    }
}
