package com.ek307.voidbowels.buschedulemaker.activities;

import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.ek307.voidbowels.buschedulemaker.R;
import com.ek307.voidbowels.buschedulemaker.adapters.WorkingSchedulesAdapter;
import com.ek307.voidbowels.buschedulemaker.data.CourseList;


public class WorkingSchedulesActivity extends ActionBarActivity {

    protected int IMAGE_WIDTH = 0;
    WorkingSchedulesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_schedules);
        System.out.println("Working Schedules Size: " + CourseList.getSchedules().size());
//        Create and set an adapter that holds all the WorkingSchedules
        adapter = new WorkingSchedulesAdapter(this, this, R.layout.working_schedules_list_row, CourseList.getSchedules());
        ((ListView) findViewById(R.id.workingSchedulesList)).setAdapter(adapter);
//        Reset Filter Dialog
        DialogFilter.clearOptions();

//        Alert user of how many classes were found
        Toast.makeText(this, CourseList.getSchedules().size() + " Schedules Found!", Toast.LENGTH_LONG).show();

//        Get screen width and calculate size for image
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        IMAGE_WIDTH = (int) (size.x * .75);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_working_schedules, menu);
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
//            Show DialogFilter if the filter button is pressed
//        } else
        if (id == R.id.action_filter) {
            new DialogFilter(this, adapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getImageWidth() {
        return IMAGE_WIDTH;
    }

}
