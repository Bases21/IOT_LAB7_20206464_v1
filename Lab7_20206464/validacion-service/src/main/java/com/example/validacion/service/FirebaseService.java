package com.example.validacion.service;

import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    private final DatabaseReference databaseReference;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    public boolean dniExists(String dni) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        databaseReference.orderByChild("dni").equalTo(dni).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                future.complete(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean correoExists(String correo) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        databaseReference.orderByChild("correo").equalTo(correo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                future.complete(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
}
