package com.crizacio.eva2_iot_app2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ListaNotas extends ArrayAdapter<Nota> {
    private Activity context;
    List<Nota> notas;

    public ListaNotas(Activity context, List<Nota> notas) {
        super(context, R.layout.layout_alumno_lista, notas);
        this.context = context;
        this.notas = notas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * RESUMEN:
         * Esto permite generar un objeto el cual tiene un Titulo y un Subtitulo
         * Este objeto es compatible para ser ingresado en un ListView
         * Como resultado podras ver un objeto con un texto (la Nota) y un subtexto (la Fecha)
         * Esto queda bonito.
         * */
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_alumno_lista, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.txtTitulo);
        TextView textViewDate= (TextView) listViewItem.findViewById(R.id.txtSubtitulo);

        Nota nota = notas.get(position);
        textViewName.setText(""+nota.getNota());
        textViewDate.setText(nota.getFechaEv());

        return listViewItem;
    }
}
