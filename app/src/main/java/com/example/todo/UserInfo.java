package com.example.todo;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {

    public final String courseId;
    public final String title;
    public final String notestext;
    public final Date date;

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getNotestextText() {
        return notestext;
    }

    public Date getDate() {
        return date;
    }

    public UserInfo(String courseId, String title, String text, Date date){
        this.courseId = courseId;
        this.title = title;
        this.notestext = text;
        this.date = new Date();
    }

}
