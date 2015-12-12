package com.ek307.voidbowels.buschedulemaker.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ek307.voidbowels.buschedulemaker.R;
import com.ek307.voidbowels.buschedulemaker.data.CourseList;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    private final String[] COLLEGE_LIST = {"CAS", "CFA", "CGS", "COM", "ENG", "EOP", "FRA", "GMS", "GRS", "GSM", "KHC", "LAW", "MED", "MET", "OTP", "PDP", "SAR", "SDM", "SED", "SHA", "SMG", "SPH", "SSW", "STH", "UNI", "XAS", "XRG"};
    int numberOfClasses = 0;
    String[][] classCodes;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.classes_number).setFocusable(true);
        findViewById(R.id.classes_number).setFocusableInTouchMode(true);
        findViewById(R.id.classes_number).requestFocus();

        pDialog = new ProgressDialog(this);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setupViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        // Activity being restarted from stopped state
        classCodes = null;
        CourseList.clearWorkingSchedules();

//        Show initial layout
        findViewById(R.id.initial_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.enter_classes).setVisibility(View.GONE);
        findViewById(R.id.go_button_2).setVisibility(View.GONE);

//        Reset EditText to default text
        ((EditText) findViewById(R.id.classes_number)).setText("");

//        Clear class entering grid
        ViewGroup root = (ViewGroup) findViewById(R.id.class_row_holder);
        root.removeAllViews();

    }

    @Override
    public void onBackPressed() {
//        If on second screen, reset view to look like first, otherwise do what onBackPressed should do
        if (findViewById(R.id.initial_layout).getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            onRestart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pDialog.cancel();
    }

//    Instantiate Button and EditText listeners
    private void setupViews() {
//        On the press of the first button, check to make sure the number is valid and then create the grid layout
        findViewById(R.id.go_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Get number of classes and alert user if input is not valid number < 10
                EditText classesNumber = (EditText) findViewById(R.id.classes_number);
                try {
                    numberOfClasses = Integer.parseInt(classesNumber.getEditableText().toString());
                } catch (Exception e) {
                    showNoTextAlertDialog();
                    return;
                }
                if (numberOfClasses >= 10) {
                    showNoTextAlertDialog();
                    return;
                }

//                Create an array to hold the data
                classCodes = new String[numberOfClasses][];

//                Hide the initial layout
                findViewById(R.id.initial_layout).setVisibility(View.GONE);
                findViewById(R.id.enter_classes).setVisibility(View.VISIBLE);
                findViewById(R.id.go_button_2).setVisibility(View.VISIBLE);

//                Create each row in the grid
                ViewGroup root = (ViewGroup) findViewById(R.id.class_row_holder);

                for (int i = 0; i < numberOfClasses; i++) {
                    View row = createClassRow(i == 0);
                    root.addView(row);
                }

//                Add listeners at the end of the creation of each row because following row must be created in order to shift focus to it
                for (int i = 0; i < numberOfClasses; i++) {
                    addTextChangedListeners((ViewGroup) root.getChildAt(i));
                }
            }
        });

//        On the press of the second go button, fill the classCodes array with the data and then begin the process of pulling data
        ((Button) findViewById(R.id.go_button_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Loop through each row
                ViewGroup rowHolder = ((ViewGroup) findViewById(R.id.class_row_holder));
                for (int i = 0; i < numberOfClasses; i++) {

//                    Fill array with data from EditTexts
                    LinearLayout row = (LinearLayout) rowHolder.getChildAt(i);
                    String[] rowValues = new String[3];
                    rowValues[0] = ((EditText) row.findViewById(R.id.college)).getEditableText().toString();
                    rowValues[1] = ((EditText) row.findViewById(R.id.dept)).getEditableText().toString();
                    rowValues[2] = ((EditText) row.findViewById(R.id.courseNumber)).getEditableText().toString();

//                    If any fields are empty, stop and notify user
                    if (rowValues[0].equals("") || rowValues[1].equals("") || rowValues[2].equals("")) {
                        showBlankFieldAlertDialog();
                        classCodes = new String[3][];
                        return;
                    }

                    classCodes[i] = rowValues;
                }

//                Start processing
                new GetCourses(MainActivity.this, classCodes, pDialog).execute();
            }
        });

//        On keyboard press of done on last text field, simulate press of go button
        ((EditText) findViewById(R.id.classes_number)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((Button) findViewById(R.id.go_button)).performClick();
                }
                return false;
            }
        });
    }

//    Create views for each class row
    private View createClassRow(boolean firstRow) {
        View row = getLayoutInflater().inflate(R.layout.picking_classes_row, null);
        AutoCompleteTextView collegeView = (AutoCompleteTextView) row.findViewById(R.id.college);
        EditText deptView = (EditText) row.findViewById(R.id.dept);
        EditText courseView = (EditText) row.findViewById(R.id.courseNumber);

//        If it's the first row, set the hint and put the focus on the first field so the keyboard appears automatically
        if (firstRow) {
            collegeView.setHint("ex. ENG");
            deptView.setHint("EC");
            courseView.setHint("327");

            collegeView.setFocusable(true);
            collegeView.setFocusableInTouchMode(true);
            collegeView.requestFocus();
        }

//        Set the adapter for the AutoCompleteTextView to display the names of the colleges
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1, COLLEGE_LIST);
        collegeView.setAdapter(adapter);
        collegeView.setThreshold(1);

        return row;
    }

//    Add the text changed listener for each row
    private void addTextChangedListeners(ViewGroup row) {
        AutoCompleteTextView collegeView = (AutoCompleteTextView) row.findViewById(R.id.college);
        EditText deptView = (EditText) row.findViewById(R.id.dept);
        EditText courseView = (EditText) row.findViewById(R.id.courseNumber);

//        create the text watcher and add it to the first two views
        collegeView.addTextChangedListener(createTextWatch(true, 3, deptView));
        deptView.addTextChangedListener(createTextWatch(false, 2, courseView));

        ViewGroup root = (ViewGroup) findViewById(R.id.class_row_holder);
        int currentIndex = root.indexOfChild(row);

        if (currentIndex != root.getChildCount() - 1) {
//            For the third view, add the listener so it shifts focus to the next row upon completion
            courseView.addTextChangedListener(createTextWatch(false, 3, root.getChildAt(currentIndex+1).findViewById(R.id.college)));
        } else {
//            Otherwise on keyboard press of done on last text field, simulate press of go button
            courseView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        ((Button) findViewById(R.id.go_button_2)).performClick();
                    }
                    return false;
                }
            });
        }

    }

    private TextWatcher createTextWatch(final boolean needsValidation, final int maxChars, final View nextView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                If the max amount of characters has been entered, preform an action
                if (start + count == maxChars) {
//                    if the input is not valid, alert the user
                    if (needsValidation && !isValid(s)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Please add a valid college name!")
                                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
//                        shift the focus to the next view
                    } else {
                        nextView.setFocusable(true);
                        nextView.setFocusableInTouchMode(true);
                        nextView.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

//            returns if the entered text is a valid college name
            public boolean isValid(CharSequence text) {
                for (String s : COLLEGE_LIST) {
                    if (text.toString().equals(s))
                        return true;
                }
                return false;
            }
        };
    }

//    Create and show alert dialog to notify user that their class number input was invalid
    private AlertDialog showNoTextAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setMessage("Please enter a valid number less than 10 to continue.");
        return dialog.show();
    }

//    Create and show alert dialog to notify user that they left a field blank
    private AlertDialog showBlankFieldAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setMessage("Please fill out all available text fields.");
        return dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_watch) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
