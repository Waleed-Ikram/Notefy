package com.example.notefy;

import java.util.ArrayList;
import java.util.List;

public class PublicNote {


    private String title;
    private String description;
    List<String> comments ;



    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void AddComment(String s)
    {
        if(comments == null)
            comments = new ArrayList<>();
        comments.add(s);
    }

    public PublicNote() {
    }

    public PublicNote(String title, String description, ArrayList<String> comments) {
        this.title = title;
        this.description = description;
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

