
package com.example.notefy;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class OwnNoteAdapter extends FirebaseRecyclerAdapter<PublicNote, OwnNoteAdapter.ViewHolder> {


    Context context;
    public OwnNoteAdapter(Context context, @NonNull FirebaseRecyclerOptions<PublicNote> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull PublicNote note) {

        String key = getRef(i).getKey();

        viewHolder.tvTitle.setText(note.getTitle());
        String s = note.getDescription() ;
        if(s ==null)
            s=new String();

        if(s.length() > 6)
            s = s.substring(0,6) + ".....";
        viewHolder.tvDesc.setText(s) ;
        if(note.getComments() == null)
            note.setComments(new ArrayList<>());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, note.getComments()); // Use a default layout for the list item

        // Set the adapter to the ListView
        Toast.makeText(context, ""+ note.getComments().size(), Toast.LENGTH_SHORT).show();
        viewHolder.comments.setText(note.comments.size() + " comments");




        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v1) {
                AlertDialog.Builder updateNote = new AlertDialog.Builder(context);
                updateNote.setTitle("UPDATE NOTE");
                View v = LayoutInflater.from(context)
                        .inflate(R.layout.add_new_note_form, null, false);
                updateNote.setView(v);
                EditText etTitle = v.findViewById(R.id.etTitle);
                EditText etDesc = v.findViewById(R.id.etDesc);
                etTitle.setText(note.getTitle());
                etDesc.setText(note.getDescription());

                updateNote.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<Object, Object> data = new HashMap<>();
                        data.put(PUBLICNOTEKEY.KEY_TITLE, etTitle.getText().toString().trim());
                        data.put(PUBLICNOTEKEY.KEY_DESC, etDesc.getText().toString().trim());
                        assert key != null;
                        FirebaseDatabase.getInstance().getReference().child(AuthActivity.KEY_UID).child(AuthActivity.UID).child(PUBLICNOTEKEY.KEY_PARENT).child("Public")
                                .child(key)
                                .setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Note updated", Toast.LENGTH_SHORT).show();
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
                updateNote.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assert key != null;
                        FirebaseDatabase.getInstance().getReference().child(AuthActivity.KEY_UID).child(AuthActivity.UID).child(PUBLICNOTEKEY.KEY_PARENT).child("Public")
                                .child(key)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
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

                updateNote.show();

                return false;
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_own_note_view, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle, tvDesc , comments ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.Ltvdesc);
            tvTitle = itemView.findViewById(R.id.LtvTitle);
            comments = itemView.findViewById(R.id.Ltvcom);
        }
    }
}
