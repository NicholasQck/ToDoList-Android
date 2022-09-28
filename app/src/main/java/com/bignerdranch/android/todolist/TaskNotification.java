package com.bignerdranch.android.todolist;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


public class TaskNotification extends BroadcastReceiver {

    private String mTaskTitle;
    private String mTaskPriority;
    private int mUniqueId;

    @Override
    public void onReceive(Context context, Intent intent) {
        mTaskTitle = intent.getStringExtra("title");
        mTaskPriority = intent.getStringExtra("priority");
        mUniqueId = intent.getIntExtra("unique_id", 0);

        Intent resultIntent = new Intent(context, TaskListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "taskReminder")
                .setSmallIcon(R.drawable.ic_task_reminder)
                .setContentTitle(mTaskTitle)
                .setContentText("Priority level: " + mTaskPriority.toUpperCase() )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(mUniqueId, builder.build());

    }
}
