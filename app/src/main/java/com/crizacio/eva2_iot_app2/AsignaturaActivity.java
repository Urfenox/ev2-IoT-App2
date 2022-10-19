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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AsignaturaActivity extends AppCompatActivity {

    // Declarar objetos
    TextView txt_Nombre, txt_RUT, txt_Info;
    Button btn_Agregar;
    ListView lst_Asignaturas;
    Spinner spn_Asignatura;
    Intent intent;

    // Declara la DB y una lista para almacenar
    DatabaseReference databaseAsignaturas;
    List<Asignatura> asignaturas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura);

        // Obtiene el intent
        intent = getIntent();

        // Define la DB. ASIGNATURA
        databaseAsignaturas = FirebaseDatabase.getInstance().getReference("Notas").child(intent.getStringExtra(MainActivity.ALUMNO_RUT));

        // Define los objetos
        txt_Nombre = (TextView) findViewById(R.id.txtNombre);
        txt_RUT = (TextView) findViewById(R.id.txtRUT);
        txt_Info = (TextView) findViewById(R.id.txtInformacion);
        btn_Agregar = (Button) findViewById(R.id.btnAgregar);
        lst_Asignaturas = (ListView) findViewById(R.id.lstAsignaturas);
        spn_Asignatura = (Spinner) findViewById(R.id.spnAsignatura);

        asignaturas = new ArrayList<>();

        // Pone el nombre del alumno y su RUT. Desde los parametros Extra()
        txt_Nombre.setText(intent.getStringExtra(MainActivity.ALUMNO_NOMBRE));
        txt_RUT.setText(intent.getStringExtra(MainActivity.ALUMNO_RUT));

        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAsignatura();
            }
        });
        // Agrega un Listener al evento OnItemClick (al tocar un elemento del ListView)
        lst_Asignaturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtiene la asignatura de la lista
                Asignatura asignatura = asignaturas.get(i);
                Intent newIntent = new Intent(AsignaturaActivity.this, AlumnosActivity.class);
                // Ingresa parametros para el envio a la nueva actividad
                newIntent.putExtra(MainActivity.ALUMNO_RUT, intent.getStringExtra(MainActivity.ALUMNO_RUT));
                newIntent.putExtra(MainActivity.ALUMNO_NOMBRE, intent.getStringExtra(MainActivity.ALUMNO_NOMBRE));
                newIntent.putExtra(MainActivity.ALUMNO_ASIGNATURA, asignatura.getNombre());
                // Inicia la actividad
                startActivity(newIntent);
            }
        });
        // Agrega un Listener al evento OnItemLongClick (al tocar y mantener un elemento del ListView)
        lst_Asignaturas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtiene la asignatura de la lista
                Asignatura asignatura = asignaturas.get(i);
                // Llama a crear un dialogo (para modificar/eliminar)
                showUpdateDeleteDialog(asignatura.getRUT(), asignatura.getNombre());
                return true;
            }
        });
    }
    private void showUpdateDeleteDialog(final String RUT, String Nombre) {
        // Crea una instancia tipo AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Crea la instancia del layout
        LayoutInflater inflater = getLayoutInflater();
        // Pone el layout dentro de la instancia AlertDialog
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        // Declarar objetos y los define (tomando los controles del nuevo layout AlertDialog)
        final EditText _edt_Nombre = (EditText) dialogView.findViewById(R.id.edtCampoUNO);
        final EditText _edt_RUT = (EditText) dialogView.findViewById(R.id.edtCampoDOS);
        final Button _btn_Modificar = (Button) dialogView.findViewById(R.id.btnModificar);
        final Button _btn_Eliminar = (Button) dialogView.findViewById(R.id.btnEliminar);

        // Muestra el nombre y el RUT en los EditText
        _edt_Nombre.setText(Nombre);
        _edt_RUT.setText(RUT);

        // Muestra el AlertDialog
        dialogBuilder.setTitle(RUT);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        // Agrega un Listener al evento OnClick del boton _btn_Modificar dentro del AlertDialog
        _btn_Modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define variables y las habita con los valores de los controles correspondientes
                String nombre = _edt_Nombre.getText().toString().trim();
                String correo = _edt_RUT.getText().toString().trim();
                // Verifica que la variable no este vacia
                if (!TextUtils.isEmpty(nombre)) {
                    // Llama a modificar los datos de la asignatura. RUT como identificador.
                    updateAsignatura(RUT, nombre);
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
                deleteAsignatura(RUT, Nombre);
                // Cierra el AlertDialog
                b.dismiss();
            }
        });
    }
    private boolean updateAsignatura(String rut, String nombre) {
        // Toma el valor que identifica la ID (El RUT)
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Notas").child(rut);
        // Crea un objeto Alumno
        Asignatura asignatura = new Asignatura(nombre, rut);
        // Reemplaza los valores con el nuevo alumno
        dR.child(nombre).setValue(asignatura);
        Toast.makeText(getApplicationContext(), "Asignatura actualizada", Toast.LENGTH_LONG).show();
        return true;
    }
    private boolean deleteAsignatura(String rut, String nombre) {
        // Toma el valor que identifica la ID (El RUT) en Notas
        DatabaseReference drAsignatura = FirebaseDatabase.getInstance().getReference("Notas").child(rut).child(nombre);
        // Elimina la llave
        drAsignatura.removeValue();
        Toast.makeText(getApplicationContext(), "Asignatura eliminada", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Agrega un Listener al evento de modificacion de la DB
        databaseAsignaturas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asignaturas.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Asignatura asignatura = postSnapshot.getValue(Asignatura.class);
                    // Agrega la asignatura a la lista
                    asignaturas.add(asignatura);
                }
                // Define el adaptador para mostrar la lista en el ListView
                ListaAsignatura listaAsignaturasAdapter = new ListaAsignatura(AsignaturaActivity.this, asignaturas);
                lst_Asignaturas.setAdapter(listaAsignaturasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void saveAsignatura() {
        // Define la nueva asignatura para agregar
        String newAsignatura = spn_Asignatura.getSelectedItem().toString();
        // Verifica que la asignatura no este vacia
        if (!TextUtils.isEmpty(newAsignatura)) {
            // Crea un objeto asignatura
            Asignatura asignatura = new Asignatura(newAsignatura, intent.getStringExtra(MainActivity.ALUMNO_RUT));
            // Agrega a la base de datos. NOTAS>RUT>ASIGNATURA>(ObjetoAsignatura)
            databaseAsignaturas.child(newAsignatura).setValue(asignatura);
            Toast.makeText(this, "Asignatura agregada", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Por favor seleccione una asignatura", Toast.LENGTH_LONG).show();
        }
    }
}