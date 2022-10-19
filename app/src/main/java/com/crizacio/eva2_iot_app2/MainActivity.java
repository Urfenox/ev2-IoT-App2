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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // DECLARACION DE CONSTANTES PARA USO POSTERIOR (como identificadores llave (para llave:valor))
    public static final String ALUMNO_NOMBRE = "com.crizacio.ev2.alumnonombre";
    public static final String ALUMNO_RUT = "com.crizacio.ev2.alumnorut";
    public static final String ALUMNO_ASIGNATURA = "com.crizacio.ev2.alumnoasignatura";

    // Declarar objetos
    EditText edt_RUT, edt_Nombre, edt_Correo;
    Button btn_Agregar;
    ListView lst_Alumnos;
    // Declara la DB y una lista para almacenar
    DatabaseReference databaseAlumnos;
    List<Alumno> alumnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define la DB. ALUMNOS
        databaseAlumnos = FirebaseDatabase.getInstance().getReference("Alumnos");

        // Define los objetos
        edt_RUT = (EditText) findViewById(R.id.edtRUT);
        edt_Nombre = (EditText) findViewById(R.id.edtNombre);
        edt_Correo = (EditText) findViewById(R.id.edtCorreo);
        lst_Alumnos = (ListView) findViewById(R.id.lstAlumnos);
        btn_Agregar = (Button) findViewById(R.id.btnAgregar);

        alumnos = new ArrayList<>();

        // Agrega un Listener al evento OnClick del btn_Agregar
        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlumno();
            }
        });
        // Agrega un Listener al evento OnItemClick (al tocar un elemento del ListView)
        lst_Alumnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtiene el alumno de la lista
                Alumno alumno = alumnos.get(i);
                Intent intent = new Intent(MainActivity.this, AsignaturaActivity.class);
                // Ingresa parametros para el envio a la nueva actividad
                intent.putExtra(ALUMNO_RUT, alumno.getRUT());
                intent.putExtra(ALUMNO_NOMBRE, alumno.getNombre());
                // Inicia la actividad
                startActivity(intent);
            }
        });
        // Agrega un Listener al evento OnItemLongClick (al tocar y mantener un elemento del ListView)
        lst_Alumnos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtiene el alumno de la lista
                Alumno alumno = alumnos.get(i);
                // Llama a crear un dialogo (para modificar/eliminar)
                showUpdateDeleteDialog(alumno.getRUT(), alumno.getNombre(), alumno.getCorreo());
                return true;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Agrega un Listener al evento de modificacion de la DB
        databaseAlumnos.addValueEventListener(new ValueEventListener() {
            @Override
            // Agrega un Listener al evento cambio_de_datos de la DB
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Limpia la lista
                alumnos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Alumno alumno = postSnapshot.getValue(Alumno.class);
                    // Agrega a la lista los datos de la DB
                    alumnos.add(alumno);
                }
                // Define el adaptador para mostrar la lista en el ListView
                ListaAlumno alumnoAdapter = new ListaAlumno(MainActivity.this, alumnos);
                lst_Alumnos.setAdapter(alumnoAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void showUpdateDeleteDialog(final String alumnoRUT, String alumnoNombre, String alumnoCorreo) {
        // Crea una instancia tipo AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Crea la instancia del layout
        LayoutInflater inflater = getLayoutInflater();
        // Pone el layout dentro de la instancia AlertDialog
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        // Declarar objetos y los define (tomando los controles del nuevo layout AlertDialog)
        final EditText _edt_Nombre = (EditText) dialogView.findViewById(R.id.edtCampoUNO);
        final EditText _edt_Correo = (EditText) dialogView.findViewById(R.id.edtCampoDOS);
        final Button _btn_Modificar = (Button) dialogView.findViewById(R.id.btnModificar);
        final Button _btn_Eliminar = (Button) dialogView.findViewById(R.id.btnEliminar);

        // Muestra el nombre y el correo en los EditText
        _edt_Nombre.setText(alumnoNombre);
        _edt_Correo.setText(alumnoCorreo);

        // Muestra el AlertDialog
        dialogBuilder.setTitle(alumnoNombre);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        // Agrega un Listener al evento OnClick del boton _btn_Modificar dentro del AlertDialog
        _btn_Modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define variables y las habita con los valores de los controles correspondientes
                String nombre = _edt_Nombre.getText().toString().trim();
                String correo = _edt_Correo.getText().toString().trim();
                // Verifica que la variable no este vacia
                if (!TextUtils.isEmpty(nombre)) {
                    // Llama a modificar los datos del alumno. RUT como identificador.
                    updateAlumno(alumnoRUT, nombre, correo);
                    // Cierra el AlertDialog
                    b.dismiss();
                }
            }
        });
        // Agrega un Listener al evento OnClick del boton _btn_Eliminar dentro del AlertDialog
        _btn_Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llama a eliminar los datos del alumno.
                deleteAlumno(alumnoRUT);
                // Cierra el AlertDialog
                b.dismiss();
            }
        });
    }
    private void addAlumno() {
        // Define variables y las habita con los valores de los controles correspondientes
        String rut = edt_RUT.getText().toString().trim();
        String nombre = edt_Nombre.getText().toString().trim();
        String correo = edt_Correo.getText().toString().trim();
        // Verifica que las variables no esten vacias
        if ((!TextUtils.isEmpty(nombre))&& (!TextUtils.isEmpty(correo))) {

            // Crea un objeto Alumno
            Alumno alumno = new Alumno(rut, nombre, correo);
            // Mete el objeto Alumno dentro de la llave RUT
            databaseAlumnos.child(rut).setValue(alumno);
            // Limpia los campos
            edt_RUT.setText("");
            edt_Nombre.setText("");
            edt_Correo.setText("");
            Toast.makeText(this, "Alumno agregado", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Por favor indique los valores", Toast.LENGTH_LONG).show();
        }
    }
    private boolean updateAlumno(String id, String nombre, String correo) {
        // Toma el valor que identifica la ID (El RUT)
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Alumnos").child(id);
        // Crea un objeto Alumno
        Alumno alumno = new Alumno(id, nombre, correo);
        // Reemplaza los valores con el nuevo alumno
        dR.setValue(alumno);
        Toast.makeText(getApplicationContext(), "Alumno actualizado", Toast.LENGTH_LONG).show();
        return true;
    }
    private boolean deleteAlumno(String id) {
        // Toma el valor que identifica la ID (El RUT) en Alumnos
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Alumnos").child(id);
        // Elimina la llave
        dR.removeValue();
        // Toma el valor que identifica la ID (El RUT) en Notas
        DatabaseReference drNotas = FirebaseDatabase.getInstance().getReference("Notas").child(id);
        // Elimina la llave
        drNotas.removeValue();
        Toast.makeText(getApplicationContext(), "Alumno eliminado", Toast.LENGTH_LONG).show();
        return true;
    }
}
