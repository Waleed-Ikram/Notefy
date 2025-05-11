package com.example.notefy;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> comments;

    public CommentAdapter(Context context, List<String> comments) {
        super(context, android.R.layout.simple_list_item_1, comments);
        this.context = context;
        this.comments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setTextColor(context.getResources().getColor(android.R.color.white)); // Set text color to white
        return view;
    }
}
