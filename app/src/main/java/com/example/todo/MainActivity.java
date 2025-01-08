package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener , View.OnLongClickListener {

    private RecyclerView recyclerView;
    private toDoAdapter adapter;
    private final List<UserInfo> user = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("To Do : " + user.size());
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new toDoAdapter(user,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu){
        getMenuInflater().inflate(R.menu.activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.info) {
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(MainActivity.this,EditActivity.class);
            startActivityForResult(intent,1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildAdapterPosition(view);
        UserInfo currentUser = user.get(pos);
        Intent intent = new Intent(MainActivity.this,EditActivity.class);
        intent.putExtra("existing user",new Gson().toJson(currentUser));
        intent.putExtra("position",pos);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildAdapterPosition(view);
        UserInfo cUser = user.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Delete Note " + cUser.getCourseId() + " : " + cUser.getTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    user.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    updateToolbarSubtitle();
                    saveNotes();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
        return true;
    }

    protected void onResume(){
        loadNotes();
        super.onResume();
    }

    private void saveNotes() {
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(user);
            FileOutputStream fileOutputStream = getApplicationContext()
                    .openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.print(jsonString);
            printWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNotes() {
        user.clear();

        try {
            FileInputStream inputStream = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            if (stringBuilder.length() > 0) {
                Gson gson = new Gson();
                UserInfo[] loadedNotes = gson.fromJson(stringBuilder.toString(), UserInfo[].class);
                Collections.addAll(user, loadedNotes);
            }
            adapter.notifyDataSetChanged();
            updateToolbarSubtitle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateToolbarSubtitle() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle("To Do: " + user.size());
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("newNote")) {
                String jsonNote = data.getStringExtra("newNote");
                Gson gson = new Gson();
                UserInfo newNote = gson.fromJson(jsonNote, UserInfo.class);
                user.add(0,newNote);

                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                updateToolbarSubtitle();
            }
        }
    }
}