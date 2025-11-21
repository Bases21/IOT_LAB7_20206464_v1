package com.example.lab06.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab06.R;
import com.example.lab06.adapters.TareaAdapter;
import com.example.lab06.models.Tarea;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareasFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAgregarTarea;
    private TareaAdapter adapter;
    private List<Tarea> tareaList;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tareas, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tareas");

        recyclerView = view.findViewById(R.id.recyclerViewTareas);
        fabAgregarTarea = view.findViewById(R.id.fabAgregarTarea);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tareaList = new ArrayList<>();
        adapter = new TareaAdapter(getContext(), tareaList, this::editarTarea, this::eliminarTarea);
        recyclerView.setAdapter(adapter);

        fabAgregarTarea.setOnClickListener(v -> mostrarDialogoAgregarTarea());

        cargarTareas();

        return view;
    }

    private void cargarTareas() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tareaList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Tarea tarea = dataSnapshot.getValue(Tarea.class);
                            if (tarea != null) {
                                tarea.setId(dataSnapshot.getKey());
                                tareaList.add(tarea);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error al cargar tareas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoAgregarTarea() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_tarea, null);
        
        android.widget.EditText etTitulo = dialogView.findViewById(R.id.etTituloTarea);
        android.widget.EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionTarea);
        android.widget.Button btnSeleccionarFecha = dialogView.findViewById(R.id.btnSeleccionarFecha);
        android.widget.TextView tvFechaSeleccionada = dialogView.findViewById(R.id.tvFechaSeleccionada);

        final long[] fechaSeleccionada = {System.currentTimeMillis()};

        btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        fechaSeleccionada[0] = calendar.getTimeInMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        tvFechaSeleccionada.setText("Fecha: " + sdf.format(new Date(fechaSeleccionada[0])));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Agregar Tarea")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();

                    if (titulo.isEmpty()) {
                        Toast.makeText(getContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userId = mAuth.getCurrentUser().getUid();
                    Tarea tarea = new Tarea(titulo, descripcion, fechaSeleccionada[0], false, userId);
                    
                    String tareaId = databaseReference.push().getKey();
                    if (tareaId != null) {
                        databaseReference.child(tareaId).setValue(tarea)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Tarea registrada correctamente", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al registrar tarea", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void editarTarea(Tarea tarea) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_tarea, null);
        
        android.widget.EditText etTitulo = dialogView.findViewById(R.id.etTituloTarea);
        android.widget.EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionTarea);
        android.widget.Button btnSeleccionarFecha = dialogView.findViewById(R.id.btnSeleccionarFecha);
        android.widget.TextView tvFechaSeleccionada = dialogView.findViewById(R.id.tvFechaSeleccionada);
        android.widget.CheckBox cbEstado = dialogView.findViewById(R.id.cbEstadoTarea);

        etTitulo.setText(tarea.getTitulo());
        etDescripcion.setText(tarea.getDescripcion());
        cbEstado.setChecked(tarea.isEstado());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvFechaSeleccionada.setText("Fecha: " + sdf.format(new Date(tarea.getFechaLimite())));

        final long[] fechaSeleccionada = {tarea.getFechaLimite()};

        btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(fechaSeleccionada[0]);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        fechaSeleccionada[0] = calendar.getTimeInMillis();
                        tvFechaSeleccionada.setText("Fecha: " + sdf.format(new Date(fechaSeleccionada[0])));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Editar Tarea")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    boolean estado = cbEstado.isChecked();

                    if (titulo.isEmpty()) {
                        Toast.makeText(getContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tarea.setTitulo(titulo);
                    tarea.setDescripcion(descripcion);
                    tarea.setFechaLimite(fechaSeleccionada[0]);
                    tarea.setEstado(estado);

                    databaseReference.child(tarea.getId()).setValue(tarea)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void eliminarTarea(Tarea tarea) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Tarea")
                .setMessage("¿Está seguro de eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    databaseReference.child(tarea.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }
}
