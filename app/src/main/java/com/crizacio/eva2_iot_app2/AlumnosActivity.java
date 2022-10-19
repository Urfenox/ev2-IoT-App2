package com.crizacio.eva2_iot_app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno);

        // Obtiene el intent
        Intent intent = getIntent();

        // Define la DB. NOTAS>RUT>ASIGNATURA
        databaseNotas = FirebaseDatabase.getInstance().getReference("Notas").child(intent.getStringExtra(MainActivity.ALUMNO_RUT)).child(intent.getStringExtra(MainActivity.ALUMNO_ASIGNATURA));

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
            Nota nota = new Nota(id, Float.parseFloat(newNota));
            // Agrega el objeto a la base de datos. NOTAS>RUT>ASIGNATURA>ID(RANDOM)>(ObjetoNota)
            databaseNotas.child(id).setValue(nota);
            Toast.makeText(this, "Nota agregada", Toast.LENGTH_LONG).show();
            edt_Nota.setText("");
        } else {
            Toast.makeText(this, "Por favor indique nota", Toast.LENGTH_LONG).show();
        }
    }
}
