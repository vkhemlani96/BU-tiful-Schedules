package com.ek307.voidbowels.buschedulemaker.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ek307.voidbowels.buschedulemaker.R;
import com.ek307.voidbowels.buschedulemaker.adapters.WorkingSchedulesAdapter;

/**
 * Created by Vinay on 12/5/15.
 */
class DialogFilter extends AlertDialog.Builder {

//    Initializes text and values for filter choices
    private final String[] options = {"No 8AM Classes",
            "No Classes after 4PM",
            "No Classes on Mondays",
            "No Classes on Fridays"
    };
    private static boolean[] optionChecked = {false, false, false, false};
//    Keeps track of checked objects
    private final DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            optionChecked[which] = isChecked;
        }
    };
//    Closes Dialog
    private final DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
//    Forms a filter string to pass into filter by concatenating constant strings
    private final DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String filterString = "";
            if (optionChecked[0])
                filterString += WorkingSchedulesAdapter.AdapterFilters.NO_8AMS;
            if (optionChecked[1])
                filterString += WorkingSchedulesAdapter.AdapterFilters.NO_4PMS;
            if (optionChecked[2])
                filterString += WorkingSchedulesAdapter.AdapterFilters.NO_MONDAYS;
            if (optionChecked[3])
                filterString += WorkingSchedulesAdapter.AdapterFilters.NO_FRIDAYS;
            adapter.getFilter().filter(filterString);
        }
    };
    private WorkingSchedulesAdapter adapter;

    public DialogFilter(Context context, WorkingSchedulesAdapter adapter) {
//        Creates Dialog
        super(context, R.style.AppCompatAlertDialogStyle);
        this.adapter = adapter;
        this.setPositiveButton("Apply", positiveListener);
        this.setCancelable(true);
        this.setNegativeButton("Cancel", negativeListener);
        this.setTitle("Select Schedule Filters");
        this.setMultiChoiceItems(options, optionChecked, listener).show();
    }

    public static void clearOptions() {
//        Clears check boxes
        for (int i = 0; i < optionChecked.length; i++) {
            optionChecked[i] = false;
        }
    }

}
