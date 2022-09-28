package com.bignerdranch.android.todolist;

import android.support.v4.app.Fragment;

public class TaskListActivity extends FullFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new TaskListFragment();
    }
}
