package com.cbcds.app.unitable.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Class {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    @ColumnInfo(name = "subject_name")
    private String subject;
    @ColumnInfo(name = "day_of_week")
    private int day;
    private String homework;
    private String classroom;
    @ColumnInfo(name = "class_number")
    private int number;
    private int completionStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(int status) {
        completionStatus = status;
    }
}
