package com.crizacio.eva2_iot_app2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AlumnosActivity extends AppCompatActivity {

    // Declarar objetos
    TextView txt_Nombre, txt_Asignatura, txt_Promedio;
    Button btn_Agregar;
    EditText edt_Nota;
    ListView lst_Notas;
    // Declara la DB y una lista para almacenar
    DatabaseReference databaseNotas;
    List<Nota> notas;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno);

        // Obtiene el intent
        intent = getIntent();

        // Define la DB. NOTAS>RUT>ASIGNATURA
        databaseNotas = FirebaseDatabase.getInstance().getReference("Notas").child(intent.getStringExtra(MainActivity.ALUMNO_RUT)).child(intent.getStringExtra(MainActivity.ALUMNO_ASIGNATURA)).child("Evaluaciones");

        // Define los objetos
        txt_Nombre = (TextView) findViewById(R.id.txtNombre);
        txt_Asignatura = (TextView) findViewById(R.id.txtAsignatura);
        txt_Promedio = (TextView) findViewById(R.id.txtPromedio);
        btn_Agregar = (Button) findViewById(R.id.btnAgregar);
        edt_Nota = (EditText) findViewById(R.id.edtNota);
        lst_Notas = (ListView) findViewById(R.id.lstNotas);

        notas = new ArrayList<>();

        // Pone el nombre del alumno y la asignatura. Desde los parametros Extra()
        txt_Nombre.setText(intent.getStringExtra(MainActivity.ALUMNO_NOMBRE));
        txt_Asignatura.setText(intent.getStringExtra(MainActivity.ALUMNO_ASIGNATURA));

        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNota();
            }
        });
        // Agrega un Listener al evento OnItemLongClick (al tocar y mantener un elemento del ListView)
        lst_Notas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtiene la nota de la lista
                Nota nota = notas.get(i);
                // Llama a crear un dialogo (para modificar/eliminar)
                showUpdateDeleteDialog(nota.getId(),nota.getNota(), nota.getFechaEv());
                return true;
            }
        });
    }
    private void showUpdateDeleteDialog(final String id, float Nota, String Fecha) {
        // Crea una instancia tipo AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Crea la instancia del layout
        LayoutInflater inflater = getLayoutInflater();
        // Pone el layout dentro de la instancia AlertDialog
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        // Declarar objetos y los define (tomando los controles del nuevo layout AlertDialog)
        final EditText _edt_Nota = (EditText) dialogView.findViewById(R.id.edtCampoUNO);
        final EditText _edt_Fecha = (EditText) dialogView.findViewById(R.id.edtCampoDOS);
        final Button _btn_Modificar = (Button) dialogView.findViewById(R.id.btnModificar);
        final Button _btn_Eliminar = (Button) dialogView.findViewById(R.id.btnEliminar);

        // Muestra la nota y la fecha en los EditText
        _edt_Nota.setText(""+Nota);
        _edt_Fecha.setText(Fecha);

        // Muestra el AlertDialog
        dialogBuilder.setTitle("myNota");
        final AlertDialog b = dialogBuilder.create();
        b.show();
        // Agrega un Listener al evento OnClick del boton _btn_Modificar dentro del AlertDialog
        _btn_Modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define variables y las habita con los valores de los controles correspondientes
                String nota = _edt_Nota.getText().toString().trim();
                String fecha = _edt_Fecha.getText().toString().trim();
                // Verifica que la variable no este vacia
                if (!TextUtils.isEmpty(nota)) {
                    // Llama a modificar los datos de la asignatura. RUT como identificador.
                    updateNota(id, Float.parseFloat(nota), fecha);
                    // Cierra el AlertDialog
                    b.dismiss();
                }
            }
        });
        // Agrega un Listener al evento OnClick del boton _btn_Eliminar dentro del AlertDialog
        _btn_Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llama a eliminar los datos del la asignatura.
                deleteNota(id);
                // Cierra el AlertDialog
                b.dismiss();
            }
        });
    }
    private boolean updateNota(String id, float nota, String fecha) {
        // Toma el valor que identifica la ID (El RUT)
        DatabaseReference dR = databaseNotas;
        // Crea un objeto Alumno
        Nota modNota = new Nota(id, nota, fecha);
        // Reemplaza los valores con el nuevo alumno
        dR.child(id).setValue(modNota);
        Toast.makeText(getApplicationContext(), "Nota actualizada", Toast.LENGTH_LONG).show();
        return true;
    }
    private boolean deleteNota(String id) {
        // Toma el valor que identifica la ID (El RUT) en Notas
        DatabaseReference drNotas = databaseNotas;
        // Elimina la llave
        drNotas.child(id).removeValue();
        Toast.makeText(getApplicationContext(), "Nota eliminada", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Agrega un Listener al evento de modificacion de la DB
        databaseNotas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notas.clear();
                // Define cuantas notas hay actualmente
                int cantidadNotas = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Nota nota = postSnapshot.getValue(Nota.class);
                    // Agrega la nota a la lista
                    notas.add(nota);
                    // Incrementa la cantidad de notas
                    cantidadNotas++;
                }
                // Define el adaptador para mostrar la lista en el ListView
                ListaNotas listaNotasAdapter = new ListaNotas(AlumnosActivity.this, notas);
                lst_Notas.setAdapter(listaNotasAdapter);
                // Llama a obtener el promedio de notas final. Pasa la cantidad para realizar la division.
                obtenerPromedio(cantidadNotas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void obtenerPromedio(int cantidad) {
        String promedio = "0";
        float misNotas = 0;
        // Suma cada nota y el resultado lo almacena en misNotas
        for (int i = 0; i<cantidad; i++) {
            misNotas += notas.get(i).getNota();
        }
        // Redondear la nota promedio a un decimal
        DecimalFormat df = new DecimalFormat("#.#");
        // Define el promedio para posteriormente mostrarlo en pantalla
        promedio = "Promedio de notas: "+df.format((misNotas / cantidad));
        // Muestra el promedio en el TextView
        txt_Promedio.setText(promedio);
    }

    private void saveNota() {
        // Define la nueva nota para agregar
        String newNota = edt_Nota.getText().toString().trim();
        // Verifica que la nota no este vacia
        if (!TextUtils.isEmpty(newNota)) {
            // Obtiene una llave aleatoria para realizar el ingreso
            String id  = databaseNotas.push().getKey();
            // Reemplaza la coma(,) por un punto(.) en el valor de la nota
            newNota = newNota.replace(',','.');
            // Crea un objeto nota. FechaEV = CURRENTTIMESTAMP
            Nota nota = new Nota(id, Float.parseFloat(newNota), "");
            // Agrega el objeto a la base de datos. NOTAS>RUT>ASIGNATURA>ID(RANDOM)>(ObjetoNota)
            databaseNotas.child(id).setValue(nota);
            Toast.makeText(this, "Nota agregada", Toast.LENGTH_LONG).show();
            edt_Nota.setText("");
        } else {
            Toast.makeText(this, "Por favor indique nota", Toast.LENGTH_LONG).show();
        }
    }
}
