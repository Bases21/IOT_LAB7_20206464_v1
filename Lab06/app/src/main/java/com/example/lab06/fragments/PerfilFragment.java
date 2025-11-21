package com.example.lab06.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab06.R;
import com.example.lab06.models.Usuario;
import com.example.lab06.services.AuthService;
import com.example.lab06.services.CloudStorage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilFragment extends Fragment {

    private ImageView ivProfileImage;
    private TextView tvNombre, tvEmail, tvDni;
    private Button btnSelectImage, btnUploadImage;
    private ProgressBar progressBar;

    private AuthService authService;
    private CloudStorage cloudStorage;
    private DatabaseReference databaseReference;
    
    private Uri selectedImageUri;
    private String userId;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        authService = AuthService.getInstance();
        cloudStorage = CloudStorage.getInstance();
        userId = authService.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvDni = view.findViewById(R.id.tvDni);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        progressBar = view.findViewById(R.id.progressBarPerfil);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProfileImage.setImageURI(selectedImageUri);
                        btnUploadImage.setEnabled(true);
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnUploadImage.setOnClickListener(v -> uploadImage());

        cargarDatosUsuario();

        return view;
    }

    private void cargarDatosUsuario() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null) {
                    tvNombre.setText("Nombre: " + usuario.getNombre());
                    tvEmail.setText("Email: " + usuario.getCorreo());
                    tvDni.setText("DNI: " + usuario.getDni());

                    if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                        ivProfileImage.setImageURI(Uri.parse(usuario.getFotoPerfil()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error cargando datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Seleccione una imagen primero", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUploadImage.setEnabled(false);

        cloudStorage.uploadImage(selectedImageUri, userId)
                .addOnSuccessListener(taskSnapshot -> {
                    cloudStorage.getDownloadUrlFromReference(taskSnapshot.getStorage())
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                
                                databaseReference.child("fotoPerfil").setValue(downloadUrl)
                                        .addOnCompleteListener(task -> {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), 
                                                    "Imagen subida\nURL: " + downloadUrl, 
                                                    Toast.LENGTH_LONG).show();
                                                selectedImageUri = null;
                                            } else {
                                                Toast.makeText(getContext(), 
                                                    "Error guardando URL", 
                                                    Toast.LENGTH_SHORT).show();
                                                btnUploadImage.setEnabled(true);
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), 
                                    "Error obteniendo URL: " + e.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                                btnUploadImage.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), 
                        "Error subiendo imagen: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    btnUploadImage.setEnabled(true);
                });
    }
}
