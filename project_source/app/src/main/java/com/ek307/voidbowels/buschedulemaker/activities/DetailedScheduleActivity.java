package com.ek307.voidbowels.buschedulemaker.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.ek307.voidbowels.buschedulemaker.data.CourseList;
import com.ek307.voidbowels.buschedulemaker.R;
import com.ek307.voidbowels.buschedulemaker.adapters.ScheduleListAdapter;
import com.ek307.voidbowels.buschedulemaker.data.TimeBlock;
import com.ek307.voidbowels.buschedulemaker.data.WorkingSchedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class DetailedScheduleActivity extends ActionBarActivity {

    WorkingSchedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_schedule);

//        Gets the index of the working schedule and then retrieves the schedule itself
        int scheduleIndex = getIntent().getIntExtra("com.ek307.voidbowels.WorkingSchedulesActivity.ScheduleIndex", -1);
        schedule = CourseList.getFilteredSchedules().get(scheduleIndex);

//        Gets the size of the screen and then takes a proportion of it to determine the size of the image to display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int imageWidth = (int) (size.x * .75);
//        Creates and displays the image
        ((ImageView) findViewById(R.id.image_schedule)).setImageBitmap(schedule.getBitmap(imageWidth));

//        Sets the adapter for the ExampleListView by using the TimeBlocks in the Schedule
        ((ExpandableListView) findViewById(R.id.schedule_listview)).setAdapter(
                new ScheduleListAdapter(this, schedule.getTimeBlocks()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_schedule, menu);
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
//
//            return true;
//        } else
            if (id == R.id.action_share) {
//            Combines the course and section numbers into one string to share
            String shareString = "Schedule for Spring 2015:\n";
            for (TimeBlock block: schedule.getTimeBlocks()) {
                for (String[] s : block.getSections()) {
                    shareString += block.getCourse().getCollege() + " " + block.getCourse().getDept() + " " + s[0] + "\n";
                }
            }

//            Intent is fired to allow user to choose how to share/send section info
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareString.trim());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Schedule:"));
        } else if (id == R.id.action_save) {
//            Names the image with the title string and current time
            String title = "BU Schedule Maker Schedule";
            String description = "Created on "
                    + new SimpleDateFormat("MM/dd/y kk:mm aa", Locale.US).format(Calendar.getInstance().getTime());
//            Tries to save the image and notifies the user of success
            try {
                MediaStore.Images.Media.insertImage(
                        getContentResolver(), schedule.getLargeBitmap(), title, description);
                Toast.makeText(this, "Saved Imaged to Gallery!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to Save Image to Gallery", Toast.LENGTH_LONG).show();
            }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
