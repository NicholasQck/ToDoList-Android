package com.bignerdranch.android.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.todolist.database.TaskBaseHelper;
import com.bignerdranch.android.todolist.database.TaskCursorWrapper;
import com.bignerdranch.android.todolist.database.TaskDbSchema.TaskTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskCollection {

    private static TaskCollection sTaskCollection;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TaskCollection get(Context context){
        if (sTaskCollection == null){
            sTaskCollection = new TaskCollection(context);
        }
        return sTaskCollection;
    }

    private TaskCollection(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new TaskBaseHelper(mContext).getWritableDatabase();
    }

    public void addTask(Task t){
        ContentValues values = getContentValues(t);
        mDatabase.insert(TaskTable.NAME, null, values);
    }

    public void removeTask(Task t){
        String uuidString = t.getTaskId().toString();
        mDatabase.delete(TaskTable.NAME, TaskTable.Cols.UUID + "= ?", new String[]{uuidString});
    }

    public List<Task> getTasks(){
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = queryTasks(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        }
        finally{
            cursor.close();
        }

        return tasks;
    }

    public Task getTask(UUID id){
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.UUID + "=? ",
                new String[]{id.toString()}
        );

        try{
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getTask();
        }
        finally{
            cursor.close();
        }
    }

    public File getImageFile(Task task){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, task.getImageFilename());
    }

    public void updateTask(Task task){
        String uuidString = task.getTaskId().toString();
        ContentValues values = getContentValues(task);

        mDatabase.update(TaskTable.NAME, values,
                TaskTable.Cols.UUID + "= ?",
                new String[]{uuidString});
    }

    private TaskCursorWrapper queryTasks(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null, //columns - select all columns
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy

        );

        return new TaskCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Task task){
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getTaskId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.NOTE, task.getNote());
        values.put(TaskTable.Cols.PRIORITY, task.getPriority());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.COMPLETED, task.taskCompleted() ? 1 : 0);

        return values;
    }
}
