
package com.example.notefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

    public class ListOwnPublicNotes extends AppCompatActivity {

        RecyclerView rvNotes;
        FloatingActionButton fabAddNote;
        DatabaseReference reference;
        OwnNoteAdapter notesAdapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_list_own_public_notes);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
            Intent i = new Intent(com.example.notefy.ListOwnPublicNotes.this, AddPublicNote.class);
            startActivity(i);
            finish();
        }

        private void init()
        {
            reference = FirebaseDatabase.getInstance().getReference();
            fabAddNote = findViewById(R.id.LfabAddNote);
            rvNotes = findViewById(R.id.LrvNotes);
            rvNotes.setHasFixedSize(true);
            rvNotes.setLayoutManager(new GridLayoutManager(this,2));

            Query query = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID)
                    .child(PUBLICNOTEKEY.KEY_PARENT).child("Public");

            FirebaseRecyclerOptions<PublicNote> options =
                    new FirebaseRecyclerOptions.Builder<PublicNote>()
                            .setQuery(query, PublicNote.class)
                            .build();

            notesAdapter = new OwnNoteAdapter(this, options);
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
