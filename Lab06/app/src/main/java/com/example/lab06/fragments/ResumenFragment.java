package com.example.lab06.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab06.R;
import com.example.lab06.models.Tarea;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResumenFragment extends Fragment {

    private PieChart pieChart;
    private TextView tvTotalTareas, tvCompletadas, tvPendientes;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tareas");

        pieChart = view.findViewById(R.id.pieChart);
        tvTotalTareas = view.findViewById(R.id.tvTotalTareas);
        tvCompletadas = view.findViewById(R.id.tvCompletadas);
        tvPendientes = view.findViewById(R.id.tvPendientes);

        cargarResumen();

        return view;
    }

    private void cargarResumen() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int total = 0;
                        int completadas = 0;
                        int pendientes = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Tarea tarea = dataSnapshot.getValue(Tarea.class);
                            if (tarea != null) {
                                total++;
                                if (tarea.isEstado()) {
                                    completadas++;
                                } else {
                                    pendientes++;
                                }
                            }
                        }

                        tvTotalTareas.setText("Total de tareas: " + total);
                        tvCompletadas.setText("Tareas completadas: " + completadas);
                        tvPendientes.setText("Tareas pendientes: " + pendientes);

                        actualizarGrafico(completadas, pendientes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void actualizarGrafico(int completadas, int pendientes) {
        List<PieEntry> entries = new ArrayList<>();
        
        if (completadas > 0) {
            entries.add(new PieEntry(completadas, "Completadas"));
        }
        if (pendientes > 0) {
            entries.add(new PieEntry(pendientes, "Pendientes"));
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Sin tareas"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Resumen\nde Tareas");
        pieChart.setCenterTextSize(16f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
