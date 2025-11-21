package com.example.lab06.services;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CloudStorage {
    
    private static CloudStorage instance;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    
    private CloudStorage() {
        initializeStorage();
    }
    
    public static synchronized CloudStorage getInstance() {
        if (instance == null) {
            instance = new CloudStorage();
        }
        return instance;
    }
    
    public void initializeStorage() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    
    public Task<UploadTask.TaskSnapshot> uploadImage(Uri fileUri, String userId) {
        String fileName = "profile_images/" + userId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);
        return fileRef.putFile(fileUri);
    }
    
    public Task<Uri> getDownloadUrlFromReference(StorageReference reference) {
        return reference.getDownloadUrl();
    }
}
