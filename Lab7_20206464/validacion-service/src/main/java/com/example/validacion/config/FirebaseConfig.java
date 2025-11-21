package com.example.validacion.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    @Value("${firebase.database.url}")
    private String databaseUrl;
    
    @Value("${firebase.credentials.path}")
    private String credentialsPath;
    
    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("firebase-adminsdk.json");
            
            if (serviceAccount == null) {
                throw new IOException("No se encontró el archivo firebase-adminsdk.json");
            }
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl)
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar Firebase: " + e.getMessage());
        }
    }
}
