package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class toDoAdapter extends RecyclerView.Adapter<toDoViewHolder> {

    private final List<UserInfo> userInfoList;
    private final MainActivity mainActivity;

    public toDoAdapter(List<UserInfo> userInfos, MainActivity mainAct){
        this.userInfoList = userInfos;
        this.mainActivity = mainAct;
    }
    @NonNull
    @Override
    public toDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_entrry,parent,false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new toDoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull toDoViewHolder holder, int position) {

        UserInfo userInfo = userInfoList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm a");
        String date = dateFormat.format(userInfo.getDate());
        holder.courseIdText.setText(userInfo.getCourseId());
        String title = userInfo.getTitle();
        holder.titleText.setText(title.length() > 80 ? title.substring(0,80) + "..." : title);
        String notes = userInfo.getNotestextText();
        holder.notesText.setText(notes.length() > 80 ? notes.substring(0, 80) + "..." : notes);
        holder.dateText.setText(date);

    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }
}
