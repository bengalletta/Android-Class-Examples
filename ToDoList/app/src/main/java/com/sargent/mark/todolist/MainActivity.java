package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener{
    private Spinner filter; //added a spinner
    private Button filter_button; //added a button for the spinner
    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");

        //instantiate the Spinner from the .xml file onto the activity
        filter = (Spinner) findViewById(R.id.filter_spinner);

        //creates an adapter for the current activity using the array resource created for spinner
        //items using a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filter_selections, android.R.layout.simple_spinner_item);

        //sets the default layout where the spinner items will appear on
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //applies the adapter on the spinner
        filter.setAdapter(spinnerAdapter);

        //instantiate the Button for filtering from the .xml file onto the activity
        filter_button = (Button) findViewById(R.id.filter_button);

        //sets up methods when the filter button is clicked
        filter_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                //gets the value of the selected item chosen on the filter spinner
                String filter_value = filter.getSelectedItem().toString();

                //sets the cursor depending on the value of the spinner chosen
                //if it's "All", it will use the getAllItems() function
                //else it will use returnCategoryItems() which filters depending on the
                //'category' column
                cursor = (filter_value.equals("All") ? getAllItems(db) : returnCategoryItems(db, filter_value));
                Log.v(TAG, "Filter button was pressed with value of: " + filter_value);

                //returns a new adapter/view of the activity based on the filter user has chosen
                adapter = returnNewAdapter();

                rv.setAdapter(adapter);
            }
        });

        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView)  findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        cursor = getAllItems(db);

        //moved the original code to its own method called returnNewAdapter(); since it will be used
        //again when the user decides to filter the recyclerview
        adapter = returnNewAdapter();

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    public void closeDialog(int year, int month, int day, String description, String category) {
        //appended String category to include extra attribute (category)
        addToDo(db, description, formatDate(year, month, day), category);

        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }


    //moved the code from the onStart() method to its own method to call it multiple times
    public ToDoListAdapter returnNewAdapter(){
        ToDoListAdapter adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {

            //categories put into here
            @Override
            public void onItemClick(int pos, String description, String duedate, long id, String category) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));

                FragmentManager fm = getSupportFragmentManager();

                //appended String category to include extra attribute (category)
                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, id, category);
                frag.show(fm, "updatetodofragment");
            }
        });

        return adapter;
    }

    //Function that returns all the items in the database sorted by due date
    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    //Function that returns all the items that fits the category the user chooses
    private Cursor returnCategoryItems(SQLiteDatabase db, String category){
        //query will be specific to return results based on the 'category' column
        String selection = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + "=?";

        //the WHERE x = "?" clause of the query, more specifically the value of ?
        String[] selectionArgs = {category};

        return db.query(
                Contract.TABLE_TODO.TABLE_NAME, //table
                null, //columns
                selection, //selection
                selectionArgs, //selectionArgs
                null, //groupBy
                null, //having
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE //orderBy

        );
    }

    private long addToDo(SQLiteDatabase db, String description, String duedate, String category) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        //gets the value of the selected spinner item and add it to the 'category' column of the DB
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }

    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String category){
        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        //included category attribute to insert the value inside column 'category'
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String category) {
        //appended String category to include extra attribute (category)
        updateToDo(db, year, month, day, description, id, category);
        adapter.swapCursor(getAllItems(db));
    }
}