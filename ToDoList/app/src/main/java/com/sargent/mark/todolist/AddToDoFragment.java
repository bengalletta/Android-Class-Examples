package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by mark on 7/4/17.
 */

public class AddToDoFragment extends DialogFragment{

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "addtodofragment";
    Spinner spinner;

    public AddToDoFragment() {
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnDialogCloseListener {
        //appended String category to include extra attribute (category)
        void closeDialog(int year, int month, int day, String description, String category);
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_categories, android.R.layout.simple_spinner_item);

        //sets the default layout where the spinner items will appear on
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //applies the adapter on the spinner
        spinner.setAdapter(adapter);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dp.updateDate(year, month, day);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDialogCloseListener activity = (OnDialogCloseListener) getActivity();
                //appended String category to include extra attribute (category)
                activity.closeDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), spinner.getSelectedItem().toString());
                AddToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}


