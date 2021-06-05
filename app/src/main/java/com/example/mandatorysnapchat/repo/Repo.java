package com.example.mandatorysnapchat.repo;

import android.graphics.Bitmap;

import com.example.mandatorysnapchat.Tasklistener;
import com.example.mandatorysnapchat.Updatable;
import com.example.mandatorysnapchat.model.Snap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class Repo {

    private static Repo repo = new Repo();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private Updatable activity;
    public List<Snap> snaps = new ArrayList<>();
    private final String SNAPS = "snaps";
    private final long ONE_MEGABYTE = 1024 * 1024;

    public static Repo singletonRepo() {
            return repo;
    }

    public void setup(Updatable a, List<Snap> snaps) {
        activity = a;
        this.snaps = snaps;
        startListener();
    }

    //uploads image with an ID as an argument, that results in the image being
    //stored under that ID, is primarily used to store a note with an associated image
    public void uploadBitmap(String ID, String title, Bitmap bitmap) {
        StorageReference ref = firebaseStorage.getReference(ID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnCompleteListener( img -> {
            System.out.println("OK to upload" + img);
            newImageData(ID, title);
        }).addOnFailureListener( exception -> {
            System.out.println("failure to upload" + exception);
        });
        activity.update(null);
    }

    public void newImageData(String ID,String title) {
        //insert new note, with "new note"
        DocumentReference ref = firestore.collection(SNAPS).document(ID);
        Map<String,Object> map = new HashMap<>();
        map.put("ID", ID);
        map.put("title", title);
        ref.set(map);  //will replace any previous value.
        System.out.println("Done inserting new document " + ref.getId());
    }

    public void downloadSnap(String ID, Tasklistener tasklistener) {
        StorageReference ref = firebaseStorage.getReference();
        StorageReference pathReference = ref.child(ID);
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            tasklistener.receive(bytes);
            System.out.println("Download OK");
        }).addOnFailureListener(ex -> {
            System.out.println("error in download" + ex);
        });
    }

    //deletes the storage and firestore data
    public void deleteSnap(String ID) {
        firestore.collection(SNAPS).document(ID).delete(); //deletes data in firestore

        StorageReference ref = firebaseStorage.getReference();
        StorageReference pathReference = ref.child(ID);
        pathReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    //loads all images into the listView initially, it does not update newly created images
    public void startListener(){

        StorageReference storageRef = firebaseStorage.getReference();

        firestore.collection(SNAPS).addSnapshotListener((value, error) -> {
                    System.out.println("snapshot called");
                    snaps.clear();
                    for (DocumentSnapshot imgStore : value.getDocuments()) {
                        Object ID = imgStore.get("ID");
                        Object title = imgStore.get("title");
                        if (ID != null && title != null) {
                            snaps.add(new Snap(ID.toString(), title.toString()));
                        }
                    }
                    activity.update(null);
                });
    }
}



