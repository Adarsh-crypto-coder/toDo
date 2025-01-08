package com.example.todo;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class toDoViewHolder extends RecyclerView.ViewHolder {

    TextView courseIdText;
    TextView dateText;
    TextView titleText;
    TextView notesText;

    toDoViewHolder(View view){
        super(view);
        courseIdText = view.findViewById(R.id.courseId);
        dateText = view.findViewById(R.id.date);
        titleText = view.findViewById(R.id.title2);
        notesText = view.findViewById(R.id.notes_preview);
    }
}
