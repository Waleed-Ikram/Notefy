package com.example.notefy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReqAdapter extends FirebaseRecyclerAdapter<Req,ReqAdapter.ViewHolder> {


    Context context;
    public ReqAdapter(Context context, @NonNull FirebaseRecyclerOptions<Req> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Req note)
    {


        String key = getRef(i).getKey();
        DatabaseReference titleRef = FirebaseDatabase.getInstance().getReference(AuthActivity.KEY_UID)
                .child(AuthActivity.UID)
                .child("Notes")
                .child(note.getNoteid())
                        .child("title");


        titleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.getValue(String.class);
                    viewHolder.tvName.setText(title);

                    System.out.println("Title: " + title);
                    // Further actions based on title can be performed here
                } else {
                    System.out.println("Title not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Database Error: " + databaseError.getMessage());
            }
        });



        viewHolder.tvName.setText(note.getRname());
        viewHolder.tvReq.setText(note.getAddednote());
        viewHolder.tvNote.setText(note.getUserid());


        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                DatabaseReference descriptionRef = FirebaseDatabase.getInstance().getReference(AuthActivity.KEY_UID)
                        .child(AuthActivity.UID)
                        .child("Notes")
                        .child("Public")
                        .child(note.getNoteid())
                        .child("description");

                descriptionRef.setValue(note.getOgnote() + note.getAddednote()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Description updated successfully.");
                        DatabaseReference newref = FirebaseDatabase.getInstance().getReference(AuthActivity.KEY_UID)
                                .child(AuthActivity.UID)
                                .child("req")
                                .child(key);
                        newref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Req Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        // Further actions after successful update can be performed here
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.err.println("Failed to update description: " + e.getMessage());
                    }
                });

            }
        });
        viewHolder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                DatabaseReference newref = FirebaseDatabase.getInstance().getReference(AuthActivity.KEY_UID)
                        .child(AuthActivity.UID)
                        .child("req")
                        .child(key);
                newref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Req Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_req_item, parent, false);
        return new ViewHolder(v);
    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName ;
        TextView tvReq ;
        TextView tvNote ;
        // Initializing Buttons
        TextView btnReject ;
        TextView btnAccept ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.reqtvname);
             tvReq = itemView.findViewById(R.id.reqtvreq);
             tvNote =itemView.findViewById(R.id.reqtvnote);

            // Initializing Buttons
             btnReject = itemView.findViewById(R.id.btnreject);
             btnAccept = itemView.findViewById(R.id.btnaccept);
        }
    }
}

