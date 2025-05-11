package com.example.notefy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddPublicNote extends AppCompatActivity {
    private EditText etName;
    private EditText etDescription;
    private TextView btnAdd;
    private TextView btnCancel;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_public_note);
        init();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteName = etName.getText().toString().trim();
                String noteDescription = etDescription.getText().toString().trim();
                List<String> items = new ArrayList<>();

                if (noteName.isEmpty() || noteDescription.isEmpty()) {
                    Toast.makeText(AddPublicNote.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<Object, Object> data = new HashMap<>();

                data.put(PUBLICNOTEKEY.KEY_TITLE, noteName);
                data.put(PUBLICNOTEKEY.KEY_DESC, noteDescription);
                data.put(PUBLICNOTEKEY.KEY_COMMENTS, items);

                DatabaseReference newRef = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID).child(PUBLICNOTEKEY.KEY_PARENT).child("Public").push();

                newRef.setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Use newRef to get the key of the newly created node
                                Toast.makeText(AddPublicNote.this, "Note added with key: " + newRef.getKey(), Toast.LENGTH_SHORT).show();
//                                DatabaseReference newRef2 = reference.child("req").push().setValue()

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddPublicNote.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                // Return to the previous screen or perform another action
                Intent i = new Intent(AddPublicNote.this, PublicNoteActivity.class);
                startActivity(i);
                finish();
//                finish();


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPublicNote.this, PublicNoteActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    void init()
    {
        reference = FirebaseDatabase.getInstance().getReference();
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etdes);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

    }

}