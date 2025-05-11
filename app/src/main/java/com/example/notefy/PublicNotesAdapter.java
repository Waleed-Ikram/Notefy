package com.example.notefy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class PublicNotesAdapter extends FirebaseRecyclerAdapter<PublicNote, PublicNotesAdapter.ViewHolder> {


    Context context;
    public PublicNotesAdapter(Context context, @NonNull FirebaseRecyclerOptions<PublicNote> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i, @NonNull PublicNote note) {

        String key = getRef(i).getKey();

        viewHolder.tvTitle.setText(note.getTitle());
        viewHolder.tvDesc.setText(note.getDescription());
        if(note.getComments() == null)
            note.setComments(new ArrayList<>());
        CommentAdapter adapter = new CommentAdapter(context,note.getComments()); // Use a default layout for the list item

        // Set the adapter to the ListView
        Toast.makeText(context, ""+ note.getComments().size(), Toast.LENGTH_SHORT).show();

        viewHolder.comments.setAdapter(adapter);
        viewHolder.btnaddcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = viewHolder.comment.getText().toString().trim();

                // Check if the comment is empty
                if (comment.isEmpty()) {
                    Toast.makeText(v.getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Clear the comment input field
                viewHolder.comment.setText("");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                assert key != null;
                DatabaseReference noteReference = reference.child(AuthActivity.KEY_UID).child(AuthActivity.UID).child("Notes").child("Public").child(key);

                // Fetch the current comments array
                noteReference.child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> comments = new ArrayList<>();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String existingComment = snapshot.getValue(String.class);
                                comments.add(existingComment);
                            }
                        }

                        // Add the new comment to the list
                        comments.add(comment);

                        // Update the comments array in Firebase
                        noteReference.child("comments").setValue(comments)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Successfully updated comments
                                        Toast.makeText(v.getContext(), "Comment added successfully", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();  // Refresh the adapter
                                    } else {
                                        // Handle failure
                                        Toast.makeText(v.getContext(), "Failed to add comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        Toast.makeText(v.getContext(), "Failed to retrieve comments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        viewHolder.btnreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

                Intent it = new Intent(context,EditRequestActivity.class);
                it.putExtra("note",note.getDescription());

//                i.putExtra("noteid")

                DatabaseReference reference;
                reference = FirebaseDatabase.getInstance().getReference().child(AuthActivity.KEY_UID).child(AuthActivity.UID).child("Public");
                String key = getRef(i).getKey();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

                            userSnapshot.child("Notes").child("Public").getRef().orderByKey().equalTo(key)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot noteSnapshot) {

                                            if (noteSnapshot.exists()) {

                                                it.putExtra("rname",note.getTitle());
                                                it.putExtra("ogid",userSnapshot.getKey());
                                                it.putExtra("noteid",key);
                                                it.putExtra("email",(String)userSnapshot.child("email").getValue());

                                                // If the child ID exists under this user, print the user's key
                                                Toast.makeText(context, "Parent User ID: " + userSnapshot.child("email").getValue(), Toast.LENGTH_SHORT).show();
                                                context.startActivity(it);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(context, "failed to add", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Database error: " + databaseError.getMessage());
                    }
                });

            }
        });




//        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v1) {
//                AlertDialog.Builder updateNote = new AlertDialog.Builder(context);
//                updateNote.setTitle("UPDATE NOTE");
//                View v = LayoutInflater.from(context)
//                        .inflate(R.layout.add_new_note_form, null, false);
//                updateNote.setView(v);
//                EditText etTitle = v.findViewById(R.id.etTitle);
//                EditText etDesc = v.findViewById(R.id.etDesc);
//                etTitle.setText(note.getTitle());
//                etDesc.setText(note.getDescription());
//
//                updateNote.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        HashMap<Object, Object> data = new HashMap<>();
//                        data.put(DBNOTEKEY.KEY_TITLE, etTitle.getText().toString().trim());
//                        data.put(DBNOTEKEY.KEY_DESC, etDesc.getText().toString().trim());
//                        assert key != null;
//                        FirebaseDatabase.getInstance().getReference(DBNOTEKEY.KEY_PARENT)
//                                .child(key)
//                                .setValue(data)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Note updated", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                    }
//                });
//                updateNote.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        assert key != null;
//                        FirebaseDatabase.getInstance().getReference(DBNOTEKEY.KEY_PARENT)
//                                .child(key)
//                                .removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                });
//
//                updateNote.show();
//
//                return false;
//            }
//        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_public_note_item_design, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle, tvDesc;
        ListView comments ;

        EditText comment ;

        TextView btnaddcomment;

        TextView btnreq ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            comments = itemView.findViewById(R.id.commentslist);
            btnaddcomment = itemView.findViewById(R.id.btnaddcomment);
            btnreq = itemView.findViewById(R.id.btnreqedit);
            comment = itemView.findViewById(R.id.etcomm);

        }
    }
}
