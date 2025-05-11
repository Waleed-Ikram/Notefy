package com.example.notefy;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PrivateNoteActivity extends AppCompatActivity {

    RecyclerView rvNotes;
    FloatingActionButton fabAddNote;
    DatabaseReference reference;
    PrivateNoteAdapter notesAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_private_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNote();
            }
        });
    }

    private void addNewNote()
    {
        Intent i = new Intent(PrivateNoteActivity.this, AddPrivateNote.class);
        startActivity(i);
//        finish();
//        AlertDialog.Builder addNewNote = new AlertDialog.Builder(this);
//        addNewNote.setTitle("NEW NOTE");
//        View v = LayoutInflater.from(this)
//                .inflate(R.layout.add_new_note_form, null, false);
//        addNewNote.setView(v);
//        EditText etTitle = v.findViewById(R.id.etTitle);
//        EditText etDesc = v.findViewById(R.id.etDesc);
//
//        addNewNote.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                HashMap<Object, Object> data = new HashMap<>();
//                data.put(KEY_TITLE, etTitle.getText().toString().trim());
//                data.put(KEY_DESC, etDesc.getText().toString().trim());
//                reference.child(KEY_PARENT).push()
//                        .setValue(data)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(MainActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//            }
//        });
//        addNewNote.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        addNewNote.show();
    }

    private void init()
    {
        reference = FirebaseDatabase.getInstance().getReference();
        fabAddNote = findViewById(R.id.pfabAddNote);
        rvNotes = findViewById(R.id.prvNotes);
        rvNotes.setHasFixedSize(true);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        Query query = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID)
                .child(PRIVATENOTEKEY.KEY_PARENT).child("Private");

        FirebaseRecyclerOptions<PrivateNote> options =
                new FirebaseRecyclerOptions.Builder<PrivateNote>()
                        .setQuery(query, PrivateNote.class)
                        .build();

        notesAdapter = new PrivateNoteAdapter(this, options);
        rvNotes.setAdapter(notesAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }
}