package com.bignerdranch.android.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class TaskFragment extends Fragment{

    private static final String ARG_TASK_ID = "task_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_REMOVE = "DialogRemove";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final int REQUEST_REMOVE = 3;

    private Task mTask;
    private File mImageFile;
    private EditText mTitleField;
    private EditText mNoteField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReminderButton;
    private CheckBox mCompletedCheckBox;
    private Spinner mPrioritySpinner;
    private ImageButton mImageButton;
    private ImageView mImageView;
    private ArrayAdapter<CharSequence> mArrayAdapter;

    public static TaskFragment newInstance(UUID taskId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_ID, taskId);

        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        mTask = TaskCollection.get(getActivity()).getTask(taskId);
        mImageFile = TaskCollection.get(getActivity()).getImageFile(mTask);
    }

    @Override
    public void onPause(){
        super.onPause();

        TaskCollection.get(getActivity()).updateTask(mTask);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_task, container, false);

        mTitleField = (EditText) v.findViewById(R.id.task_title);
        mTitleField.setText(mTask.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                mTask.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if( TextUtils.isEmpty(mTitleField.getText())){
            mTask.setTitle("(Unnamed Task)");
        }

        mNoteField = (EditText) v.findViewById(R.id.task_note);
        mNoteField.setText(mTask.getNote());
        mNoteField.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setNote(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateButton = (Button) v.findViewById(R.id.task_date);
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager1 = getFragmentManager();
                DatePickerFragment dialog1 = DatePickerFragment.newInstance(mTask.getDate());
                dialog1.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                dialog1.show(manager1, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.task_time);
        mTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager2 = getFragmentManager();
                TimePickerFragment dialog2 = TimePickerFragment.newInstance(mTask.getDate());
                dialog2.setTargetFragment(TaskFragment.this, REQUEST_TIME);
                dialog2.show(manager2,DIALOG_TIME);
            }
        });

        mCompletedCheckBox = (CheckBox) v.findViewById(R.id.task_completed);
        mCompletedCheckBox.setChecked(mTask.taskCompleted());
        mCompletedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTask.setCompleted(isChecked);
                checkDate();
            }
        });

        mPrioritySpinner = (Spinner) v.findViewById(R.id.task_priority);
        mArrayAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.priority, android.R.layout.simple_spinner_item);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mPrioritySpinner.setAdapter(mArrayAdapter);
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                mTask.setPriority(mPrioritySpinner.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });
        mPrioritySpinner.setSelection(mArrayAdapter.getPosition(mTask.getPriority()));

        mReminderButton = (Button) v.findViewById(R.id.set_reminder);
        mReminderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity(), R.string.reminder_hint, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), TaskNotification.class);
                int id = (int) mTask.getTaskId().toString().hashCode();
                intent.putExtra("title", mTask.getTitle());
                intent.putExtra("priority", mTask.getPriority());
                intent.putExtra("unique_id", id);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mTask.getDate());

                long time = calendar.getTimeInMillis();
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();

        mImageButton = (ImageButton) v.findViewById(R.id.task_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mImageFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mImageButton.setEnabled(canTakePhoto);

        mImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.todolist.fileprovider",
                        mImageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_IMAGE);
            }
        });

        mImageView = (ImageView) v.findViewById(R.id.task_image);
        updateImageView();

        checkDate();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateDate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.remove_task:
                FragmentManager manager = getFragmentManager();
                ConfirmationFragment dialog = new ConfirmationFragment();
                dialog.setTargetFragment(TaskFragment.this, REQUEST_REMOVE);
                dialog.show(manager, DIALOG_REMOVE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTask.setDate(date);
        }

        else if (requestCode == REQUEST_TIME){
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mTask.setDate(time);
        }

        else if (requestCode == REQUEST_IMAGE){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.todolist.fileprovider",
                    mImageFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateImageView();
        }

        else if(requestCode == REQUEST_REMOVE){
            TaskCollection.get(getActivity()).removeTask(mTask);
            getActivity().finish();
        }

        checkDate();
        updateDate();
    }

    private void updateDate() {
        Date d = mTask.getDate();
        CharSequence c = DateFormat.format("EEEE, MMM dd, yyyy", d);
        CharSequence t = DateFormat.format("h:mm a", d);
        mDateButton.setText(c);
        mTimeButton.setText(t);
    }

    private void checkDate(){
        Date currentDateTime = Calendar.getInstance().getTime();

        if ((mTask.getDate().compareTo(currentDateTime) < 0) || mTask.taskCompleted() ){
            mReminderButton.setEnabled(false);
        }
        else{
            mReminderButton.setEnabled(true);
        }
    }

    private void updateImageView(){
        if (mImageFile == null || !mImageFile.exists()){
            mImageView.setImageDrawable(null);
        }
        else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mImageFile.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
        }
    }
}
