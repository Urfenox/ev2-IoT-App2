package com.crizacio.eva2_iot_app2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListaAsignatura extends ArrayAdapter<Asignatura> {
    private Activity context;
    List<Asignatura> asignaturas;

    public ListaAsignatura(Activity context, List<Asignatura> asignaturas) {
        super(context, R.layout.layout_alumno_lista, asignaturas);
        this.context = context;
        this.asignaturas = asignaturas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * RESUMEN:
         * Esto permite generar un objeto el cual tiene un Titulo y un Subtitulo
         * Este objeto es compatible para ser ingresado en un ListView
         * Como resultado podras ver un objeto con un texto (la Asignatura) y un subtexto (RUT)
         * Esto queda bonito.
         * */
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_alumno_lista, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.txtTitulo);
        TextView textViewRUT = (TextView) listViewItem.findViewById(R.id.txtSubtitulo);

        Asignatura asignatura = asignaturas.get(position);
        textViewName.setText(""+asignatura.getNombre());
        textViewRUT.setText(""+asignatura.getRUT());

        return listViewItem;
    }
}
