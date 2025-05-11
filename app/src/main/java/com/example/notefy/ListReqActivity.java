package com.example.notefy;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListReqActivity extends AppCompatActivity {

    RecyclerView listreq;
    DatabaseReference reference;
    ReqAdapter reqAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_req);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();


    }



    private void init()
    {
        reference = FirebaseDatabase.getInstance().getReference();
        listreq = findViewById(R.id.listreq);
        listreq.setHasFixedSize(true);
        listreq.setLayoutManager(new LinearLayoutManager(this));

        Query query = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID)
                .child("req");

        FirebaseRecyclerOptions<Req> options =
                new FirebaseRecyclerOptions.Builder<Req>()
                        .setQuery(query, Req.class)
                        .build();

        reqAdapter = new ReqAdapter(this, options);
        listreq.setAdapter(reqAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        reqAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reqAdapter.stopListening();
    }
}
