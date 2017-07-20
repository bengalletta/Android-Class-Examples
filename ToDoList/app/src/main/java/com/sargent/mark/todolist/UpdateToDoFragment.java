package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "updatetodofragment";
    private long id;
    Spinner spinner;


    public UpdateToDoFragment(){}

    public static UpdateToDoFragment newInstance(int year, int month, int day, String description, long id, String category) {
        UpdateToDoFragment f = new UpdateToDoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", description);
        //added category attribute
        args.putString("category", category);

        f.setArguments(args);

        return f;
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {
        //appended String category to include extra attribute (category)
        void closeUpdateDialog(int year, int month, int day, String description, long id, String category);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);

        //instantiate the Spinner from the .xml file onto the activity
        spinner = (Spinner) view.findViewById(R.id.category_spinner);

        //creates an adapter for the current activity using the array resource created for spinner
        //items using a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_categories, android.R.layout.simple_spinner_item);

        //sets the default layout where the spinner items will appear on
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //applies the adapter on the spinner
        spinner.setAdapter(adapter);

        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");
        dp.updateDate(year, month, day);

        toDo.setText(description);

        //gets the value set for the to-do item
        String item = getArguments().getString("category");

        //creates an ArrayAdapter to grab the current adapter for the spinner
        ArrayAdapter spinAdapt = (ArrayAdapter) spinner.getAdapter();

        //gets the position of the item value
        int defaultPos = spinAdapt.getPosition(item);

        //sets the position of the default selected item for the spinner as the current category
        //for the to-do item
        spinner.setSelection(defaultPos);

        add.setText("Update");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                Log.d(TAG, "id: " + id);
                //appended String category to include extra attribute (category)
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, spinner.getSelectedItem().toString());
                UpdateToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}