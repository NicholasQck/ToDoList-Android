package com.bignerdranch.android.todolist.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.todolist.Task;
import com.bignerdranch.android.todolist.database.TaskDbSchema.TaskTable;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Task getTask(){
        String uuidString = getString(getColumnIndex(TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        String note = getString(getColumnIndex(TaskTable.Cols.NOTE));
        String priority = getString(getColumnIndex(TaskTable.Cols.PRIORITY));
        long date = getLong(getColumnIndex(TaskTable.Cols.DATE));
        int completed = getInt(getColumnIndex(TaskTable.Cols.COMPLETED));

        Task task = new Task(UUID.fromString(uuidString));
        task.setTitle(title);
        task.setNote(note);
        task.setPriority(priority);
        task.setDate(new Date(date));
        task.setCompleted(completed != 0);

        return task;
    }
}
