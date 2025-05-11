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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddPrivateNote extends AppCompatActivity {

    private EditText etName;
    private EditText etDescription;
    private EditText etcol;
    private CheckBox cbPublic;
    private CheckBox cbPrivate;
    private TextView btnAdd;
    private TextView btnCancel;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_private_note);
        init();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colname = etcol.getText().toString().trim();
                // Reference to the "users" node in Firebase Realtime Database


                final String[] userName = new String[1];
                final String[] uid = new String[1];
//                final String[] userName = new String[1];

                if (!colname.isEmpty()) {
                    DatabaseReference usersRef = reference.child(AuthActivity.KEY_UID);
                    Query emailQuery = usersRef.orderByChild("email").equalTo(colname);

                    emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(AddPrivateNote.this, "User with the specified email found.", Toast.LENGTH_SHORT).show();

                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    // Extract user information and display it via Toast
                                    uid[0] = userSnapshot.getKey();
                                    userName[0] = userSnapshot.child("name").getValue(String.class);
                                    String userPhone = userSnapshot.child("phone").getValue(String.class);

                                    String message = "Name: " + userName[0] + ", Phone: " + userPhone;
                                    Toast.makeText(AddPrivateNote.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Show a Toast indicating no user with the specified email was found
                                Toast.makeText(AddPrivateNote.this, "No user found with the specified email.", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                            System.out.println("Error: " + databaseError.getMessage());
                        }
                    });
                }


                String noteName = etName.getText().toString().trim();
                String noteDescription = etDescription.getText().toString().trim();

                if (noteName.isEmpty() || noteDescription.isEmpty()) {
                    Toast.makeText(AddPrivateNote.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<Object, Object> data = new HashMap<>();

                data.put(PUBLICNOTEKEY.KEY_TITLE, noteName);
                data.put(PUBLICNOTEKEY.KEY_DESC, noteDescription);
                reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID).child(PUBLICNOTEKEY.KEY_PARENT).child("Private").push()
                        .setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddPrivateNote.this, "Note added", Toast.LENGTH_SHORT).show();
                                finish();

//                                String id = reference.getKey();
//                                reference = FirebaseDatabase.getInstance().getReference();
//                                HashMap<String, Object> ndata = new HashMap<>();
//
//                                ndata.put("userid", uid[0]);
////                                ndata.put("noteid", id);
//                                reference.child("users").child(uid[0]).child("notification").push().setValue(ndata).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(AddPrivateNote.this, "Send noti", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddPrivateNote.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                // Return to the previous screen or perform another action
//                Intent i = new Intent(AddPrivateNote.this, PrivateNoteActivity.class);
//                startActivity(i);


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void init() {
        reference = FirebaseDatabase.getInstance().getReference();
        etName = findViewById(R.id.petName);
        etDescription = findViewById(R.id.petdes);
        etcol = findViewById(R.id.petcol);
        btnAdd = findViewById(R.id.pbtnAdd);
        btnCancel = findViewById(R.id.pbtnCancel);

    }
}