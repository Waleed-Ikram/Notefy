package com.example.notefy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditRequestActivity extends AppCompatActivity {

    private EditText etDes;
    private TextView tvOgNote;
    private TextView btnReq;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);
        init();
        Intent intent = getIntent();

        // Extract data using the keys
        String noteDescription = intent.getStringExtra("note");
        String email = intent.getStringExtra("email");
        String rname = intent.getStringExtra("rname");
        String originalUserId = intent.getStringExtra("ogid");
        Toast.makeText(this, originalUserId, Toast.LENGTH_SHORT).show();
        String noteId = intent.getStringExtra("noteid");
        tvOgNote.setText(noteDescription);
        btnReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addedNote = etDes.getText().toString().trim();

                // Check if the added note field is empty
                if (addedNote.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Note description is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<Object, Object> data = new HashMap<>();
                data.put("noteid", noteId);
                data.put("userid", email);
                data.put("addednote", addedNote);
                data.put("ognote", noteDescription);
                data.put("rname",rname);

                DatabaseReference newRef = reference.child(AuthActivity.KEY_UID).child(originalUserId).child("req").push();
                newRef.setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Finish the activity on successful data addition
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error
                                Toast.makeText(getApplicationContext(), "Failed to add note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }

    void init()
    {
        etDes = findViewById(R.id.etdes);
        tvOgNote = findViewById(R.id.tvognote);
        btnReq = findViewById(R.id.btnreq);
        reference = FirebaseDatabase.getInstance().getReference();


    }
}