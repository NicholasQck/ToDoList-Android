package com.bignerdranch.android.todolist;

import java.util.Date;
import java.util.UUID;

public class Task {
    private UUID mTaskId;
    private String mTitle;
    private String mNote;
    private String mPriority;
    private Date mDate;
    private boolean mCompleted;

    public Task(){
        this(UUID.randomUUID());
    }

    public Task(UUID id){
        mTaskId = id;
        mDate = new Date();
    }

    public UUID getTaskId() {
        return mTaskId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getPriority() {
        return mPriority;
    }

    public void setPriority(String priority) {
        mPriority = priority;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date Date) {
        mDate = Date;
    }

    public boolean taskCompleted(){
        return mCompleted;
    }

    public void setCompleted(boolean completed){
        mCompleted = completed;
    }

    public String getImageFilename(){
        return "IMG_" + getTaskId().toString() + ".jpg";
    }
}
