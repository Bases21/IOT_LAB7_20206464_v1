package com.example.lab06.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {
    
    private static AuthService instance;
    private FirebaseAuth mAuth;
    
    private AuthService() {
        initializeAuth();
    }
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    public void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");
    }
    
    public FirebaseAuth getAuth() {
        return mAuth;
    }
    
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }
    
    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }
    
    public Task<Void> sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }
    
    public void signOut() {
        mAuth.signOut();
    }
}
