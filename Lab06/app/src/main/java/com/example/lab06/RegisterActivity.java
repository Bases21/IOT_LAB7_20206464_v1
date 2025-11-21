package com.example.lab06;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab06.api.RegistroRequest;
import com.example.lab06.api.RegistroResponse;
import com.example.lab06.api.RegistroService;
import com.example.lab06.models.Usuario;
import com.example.lab06.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private EditText etNombre, etDni, etEmail, etPassword, etConfirmPassword;
    private Button btnRegistrar;
    private ProgressBar progressBar;

    private AuthService authService;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = AuthService.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        etNombre = findViewById(R.id.etNombre);
        etDni = findViewById(R.id.etDni);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progressBar = findViewById(R.id.progressBarRegister);

        btnRegistrar.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese su nombre");
            etNombre.requestFocus();
            return;
        }

        if (dni.isEmpty()) {
            etDni.setError("Ingrese su DNI");
            etDni.requestFocus();
            return;
        }

        if (dni.length() != 8) {
            etDni.setError("El DNI debe tener 8 dígitos");
            etDni.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Ingrese su correo electrónico");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegistroService service = retrofit.create(RegistroService.class);
        RegistroRequest request = new RegistroRequest(dni, email);

        service.validarRegistro(request).enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() != null) {
                    Log.d(TAG, "Response body - exito: " + response.body().isExito() + ", mensaje: " + response.body().getMensaje());
                }

                if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                    crearUsuarioEnFirebase(nombre, dni, email, password);
                } else {
                    progressBar.setVisibility(View.GONE);
                    String mensaje = "Error en validación";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            org.json.JSONObject jsonObject = new org.json.JSONObject(errorJson);
                            mensaje = jsonObject.optString("mensaje", mensaje);
                        } else if (response.body() != null) {
                            mensaje = response.body().getMensaje();
                        }
                    } catch (Exception e) {
                        // Ignorar
                    }
                    Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void crearUsuarioEnFirebase(String nombre, String dni, String email, String password) {
        authService.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(RegisterActivity.this, "Usuario creado en Auth", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = authService.getCurrentUser();

                        if (firebaseUser != null) {
                            Log.d(TAG, "UID obtenido: " + firebaseUser.getUid());
                            Toast.makeText(RegisterActivity.this, "UID obtenido", Toast.LENGTH_SHORT).show();
                            Usuario usuario = new Usuario(firebaseUser.getUid(), nombre, email, dni);

                            databaseReference.child(firebaseUser.getUid()).setValue(usuario)
                                    .addOnCompleteListener(dbTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        if (dbTask.isSuccessful()) {
                                            Log.d(TAG, "Usuario guardado en BD");
                                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.e(TAG, "Error guardando en BD", dbTask.getException());
                                            Toast.makeText(RegisterActivity.this, "Error guardando datos", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.e(TAG, "firebaseUser es null tras crear usuario");
                            Toast.makeText(RegisterActivity.this, "Error: usuario no obtenido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        String errorMessage = "Error en el registro";
                        if (task.getException() != null) {
                            String exceptionMessage = task.getException().getMessage();
                            if (exceptionMessage != null) {
                                if (exceptionMessage.contains("email address is already in use")) {
                                    errorMessage = "El correo electrónico ya está registrado";
                                } else if (exceptionMessage.contains("password")) {
                                    errorMessage = "La contraseña es muy débil";
                                } else if (exceptionMessage.contains("badly formatted")) {
                                    errorMessage = "El formato del correo es inválido";
                                } else {
                                    errorMessage = exceptionMessage;
                                }
                            }
                        }

                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
