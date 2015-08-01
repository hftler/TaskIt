package com.example.andi.taskit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TaskListActivity extends AppCompatActivity {

    private ListView list;
    private static final int EDIT_TASK_REQUEST = 10;
    private static final int EDIT_TASK_CREATE = 100;
    private ArrayList<Task> mTasks;
    private int mLastPositionClicked;
    private TaskAdapter mAdapter;

    private static final String TAG = "wokrs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);


        mTasks = new ArrayList<Task>();

        mTasks.add(new Task());
        mTasks.get(0).setName("Task 1");
        mTasks.get(0).setDueDate(new Date());
        mTasks.add(new Task());
        mTasks.get(1).setName("Task 2");
        mTasks.get(1).setDone(true);
        mTasks.add(new Task());
        mTasks.get(2).setName("Task 3");




        list = (ListView) findViewById(R.id.task_list);
        mAdapter = new TaskAdapter(mTasks);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLastPositionClicked = position;
                Intent i = new Intent(TaskListActivity.this, TaskActivity.class);
                Task task = (Task) parent.getAdapter().getItem(position);
                i.putExtra(TaskActivity.EXTRA,task);
                startActivityForResult(i, EDIT_TASK_REQUEST);
            }
        });

        list.getSelectedItemPosition();
        list.setChoiceMode(list.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_tasklist_context,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                SparseBooleanArray positions = list.getCheckedItemPositions();
                if(id == R.id.delete_task){
                    for(int i=positions.size()-1; i>= 0; i--){
                        if(positions.valueAt(i)){
                            mTasks.remove(positions.keyAt(i));
                        }
                    }
                    mode.finish();
                    mAdapter.notifyDataSetChanged();
                    return true;
                }


                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        registerForContextMenu(list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task task = (Task) data.getSerializableExtra(TaskActivity.EXTRA);
        if(resultCode == RESULT_OK ){
            switch (requestCode){
                case EDIT_TASK_REQUEST:
                    mTasks.set(mLastPositionClicked, task);
                    mAdapter.notifyDataSetChanged();
                    break;
                case EDIT_TASK_CREATE:
                    mTasks.add(task);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    private class TaskAdapter extends ArrayAdapter<Task> {
        TaskAdapter(ArrayList<Task> tasks){
            super(TaskListActivity.this, R.layout.task_list_row,R.id.task_item_name,tasks);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Task task = getItem(position);

            TextView taskName = (TextView) convertView.findViewById(R.id.task_item_name);
            taskName.setText(task.getName());

            CheckBox doneBox = (CheckBox)convertView.findViewById(R.id.task_item_done);
            doneBox.setChecked(task.isDone());

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_task) {
            Intent i = new Intent(TaskListActivity.this, TaskActivity.class);
            startActivityForResult(i, EDIT_TASK_CREATE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d("tag","context menu");
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_tasklist_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete_task){
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            mTasks.remove(menuInfo.position);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }
}
