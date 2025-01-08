package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private List<UserInfo> notesList = new ArrayList<>();
    private EditText courseIdEditText, titleEditText, notesEditText;
    private UserInfo users;
    private int notePosition = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        courseIdEditText = findViewById(R.id.course);
        titleEditText = findViewById(R.id.titleEdit);
        notesEditText = findViewById(R.id.editNotes);

        String existingUserJson = getIntent().getStringExtra("existing user");
        if (existingUserJson != null) {
            users = new Gson().fromJson(existingUserJson, UserInfo.class);
            courseIdEditText.setText(users.getCourseId());
            titleEditText.setText(users.getTitle());
            notesEditText.setText(users.getNotestextText());
            notePosition = getIntent().getIntExtra("position", -1);
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                changesDialog();
            }
        });
    }

    private void changesDialog() {
        String courseId = courseIdEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        if (courseId.isEmpty() && title.isEmpty() && notes.isEmpty()) {
            Toast.makeText(this, "Empty Item not Saved", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditActivity.this,MainActivity.class);
            startActivity(intent);
            return;
        }
        if (users != null && users.getCourseId().equals(courseId) && users.getTitle().equals(title) &&
                users.getNotestextText().equals(notes)) {
            Intent intent = new Intent(EditActivity.this,MainActivity.class);
            startActivity(intent);
            return;
        }
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("Your note titled is not saved ! Save note \"" + title + "\" ?")
                    .setPositiveButton("Yes", (dialog, which) -> save())
                    .setNegativeButton("No", (dialog, which) -> {
                        finish();
                    })
                    .show();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
    try {
        String courseId = courseIdEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String notes = notesEditText.getText().toString();

        if(title.isEmpty() && courseId.isEmpty() && notes.isEmpty()){
            Toast.makeText(this,"Empty item not saved",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditActivity.this,MainActivity.class);
            startActivity(intent);
        }

        if (title.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("Item cannot be saved without a title")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            return;
        }

        if (courseId.isEmpty() || title.isEmpty()) {
            Toast.makeText(this, "Empty item not saved", Toast.LENGTH_SHORT).show();
            return;
        }

        List<UserInfo> existingNotes = loadExistingNotes();

        if (notePosition != -1) {
            UserInfo currentNote = existingNotes.get(notePosition);
            if (currentNote.getCourseId().equals(courseId) && currentNote.getTitle().equals(title) &&
                    currentNote.getNotestextText().equals(notes)) {
                finish();
                return;
            }
            UserInfo updatedNote = new UserInfo(courseId, title, notes, new Date());
            existingNotes.set(notePosition, updatedNote);
        } else {
            UserInfo newNote = new UserInfo(courseId, title, notes, new Date());
            existingNotes.add(0, newNote);
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(existingNotes);

        FileOutputStream fileOutputStream = getApplicationContext()
                .openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
        PrintWriter printWriter = new PrintWriter(fileOutputStream);
        printWriter.print(jsonString);
        printWriter.close();
        fileOutputStream.close();

        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();

    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
    }
}

    private List<UserInfo> loadExistingNotes() {
        List<UserInfo> existingNotes = new ArrayList<>();
        try {
            FileInputStream fileInputStream = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            if (stringBuilder.length() > 0) {
                Gson gson = new Gson();
                UserInfo[] loadedNotes = gson.fromJson(stringBuilder.toString(), UserInfo[].class);
                Collections.addAll(existingNotes, loadedNotes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existingNotes;
    }
}
