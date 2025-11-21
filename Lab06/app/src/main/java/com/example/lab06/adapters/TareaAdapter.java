package com.example.lab06.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab06.R;
import com.example.lab06.models.Tarea;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private Context context;
    private List<Tarea> tareaList;
    private OnTareaClickListener editListener;
    private OnTareaClickListener deleteListener;

    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea);
    }

    public TareaAdapter(Context context, List<Tarea> tareaList, OnTareaClickListener editListener, OnTareaClickListener deleteListener) {
        this.context = context;
        this.tareaList = tareaList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareaList.get(position);
        
        holder.tvTitulo.setText(tarea.getTitulo());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvFecha.setText("Fecha límite: " + sdf.format(new Date(tarea.getFechaLimite())));
        
        holder.tvEstado.setText("Estado: " + tarea.getEstadoTexto());
        
        if (tarea.isEstado()) {
            holder.tvEstado.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvEstado.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        holder.btnEditar.setOnClickListener(v -> editListener.onTareaClick(tarea));
        holder.btnEliminar.setOnClickListener(v -> deleteListener.onTareaClick(tarea));
    }

    @Override
    public int getItemCount() {
        return tareaList.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvEstado;
        ImageButton btnEditar, btnEliminar;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloTarea);
            tvFecha = itemView.findViewById(R.id.tvFechaTarea);
            tvEstado = itemView.findViewById(R.id.tvEstadoTarea);
            btnEditar = itemView.findViewById(R.id.btnEditarTarea);
            btnEliminar = itemView.findViewById(R.id.btnEliminarTarea);
        }
    }
}
