package com.example.notefy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {

    CardView btnpub , btnpri , btnownnote;
    TextView reqcount ;
    TextView tvadvice ;
    LinearLayout Layoutreq ;

    DatabaseReference newRef ;



    TextView NTV ;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
        init();
        LifeLessonApiService apiService = RetrofitClient.getClient().create(LifeLessonApiService.class);
        Call<AdviceResponse> call = apiService.getRandomAdvice();

        call.enqueue(new Callback<AdviceResponse>() {
            @Override
            public void onResponse(Call<AdviceResponse> call, Response<AdviceResponse> response) {
                if (response.isSuccessful()) {
                    String adviceMessage = response.body().getAdvice().getAdviceMessage();
                    tvadvice.setText(adviceMessage);
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<AdviceResponse> call, Throwable t) {
                t.printStackTrace();
                // Handle failure
            }
        });

        btnpub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, PublicNoteActivity.class);
                startActivity(i);
            }
        });
        btnpri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, PrivateNoteActivity.class);
                startActivity(i);
            }
        });
        btnownnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ListOwnPublicNotes.class);
                startActivity(i);
            }
        });
        newRef = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID).child("req");

// Attach a ChildEventListener to listen for changes in the children
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Count the children whenever a new child is added
                updateReqCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // You can handle child changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Count the children whenever a child is removed
                updateReqCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // You can handle child moves if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.w("Firebase", "Error in reading data", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Error in reading data", Toast.LENGTH_SHORT).show();
            }
        });


        Layoutreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ListReqActivity.class);
                startActivity(i);

            }
        });

    }
    private void updateReqCount() {
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Count the children
                int childCount = (int) dataSnapshot.getChildrenCount();
                reqcount.setText(""+childCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.w("Firebase", "Error in reading data", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Error in reading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init()
    {
        tvadvice = findViewById(R.id.tvadvice);
        btnpub = findViewById(R.id.btnpubnote);
        btnpri = findViewById(R.id.btnprinote);
        btnownnote = findViewById(R.id.btnownpubnote);
        Layoutreq = findViewById(R.id.layoutreq);
        reqcount = findViewById(R.id.reqcount);
        NTV = findViewById(R.id.Htvname);
        NTV.setText("Hey "+AuthActivity.NAME + " ,");
        reference = FirebaseDatabase.getInstance().getReference();
        newRef = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID).child("req");
    }


}
