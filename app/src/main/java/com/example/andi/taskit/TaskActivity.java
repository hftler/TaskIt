package com.example.andi.taskit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskActivity extends AppCompatActivity {

    public static  final String EXTRA = "TaskExtra";
    private Button dateButton;
    private Task task;
    private EditText mTaskNameInput;
    private Button mSaveButton;
    private CheckBox mDoneBox;

    private Calendar mCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        task = (Task) getIntent().getSerializableExtra(EXTRA);
        if(task == null){
            task = new Task();
        }

        mCal = Calendar.getInstance();


        mTaskNameInput = (EditText) findViewById(R.id.task_name);
        dateButton = (Button) findViewById(R.id.task_date);
        mDoneBox = (CheckBox) findViewById(R.id.task_done);
        mSaveButton =(Button) findViewById(R.id.save_button);

        mTaskNameInput.setText(task.getName());
        if(task.getDueDate() == null){
            mCal.setTime(new Date());
            dateButton.setText("no date");
        }else{
            mCal.setTime(task.getDueDate());
            updateButton();
        }

        mDoneBox.setChecked(task.isDone());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TaskActivity.this, new DatePickerDialog.OnDateSetListener(){

                    @Override public void onDateSet(DatePicker dp, int year, int monthOfYear, int dayOfMonth){
                        mCal.set(Calendar.YEAR, year);
                        mCal.set(Calendar.MONTH, monthOfYear);
                        mCal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        updateButton();
                    }
                }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH)); dpd.show();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setName(mTaskNameInput.getText().toString());
                task.setDone(mDoneBox.isChecked());
                task.setDueDate(mCal.getTime());

                Intent i = new Intent();
                i.putExtra(EXTRA,task);
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

    private void updateButton(){
        DateFormat df = DateFormat.getDateInstance();
        dateButton.setText(df.format(mCal.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}