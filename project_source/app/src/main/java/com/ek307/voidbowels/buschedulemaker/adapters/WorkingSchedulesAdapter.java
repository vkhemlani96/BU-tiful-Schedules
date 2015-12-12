package com.ek307.voidbowels.buschedulemaker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import com.ek307.voidbowels.buschedulemaker.data.CourseList;
import com.ek307.voidbowels.buschedulemaker.R;
import com.ek307.voidbowels.buschedulemaker.data.WorkingSchedule;
import com.ek307.voidbowels.buschedulemaker.activities.DetailedScheduleActivity;
import com.ek307.voidbowels.buschedulemaker.activities.WorkingSchedulesActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinay on 12/5/15.
 */
public class WorkingSchedulesAdapter extends ArrayAdapter<WorkingSchedule> implements Filterable {
    private WorkingSchedulesActivity workingSchedulesActivity;
    private List<WorkingSchedule> schedulesDatabase;
    private List<WorkingSchedule> acceptableSchedules;

//    Create constants to represent filters
    public class AdapterFilters {
        public static final String NO_MONDAYS = "com.ek307.voidbowels.buschedulemaker.NO_MONDAYS";
        public static final String NO_FRIDAYS = "com.ek307.voidbowels.buschedulemaker.NO_FRIDAYS";
        public static final String NO_8AMS = "com.ek307.voidbowels.buschedulemaker.NO_8AMS";
        public static final String NO_4PMS = "com.ek307.voidbowels.buschedulemaker.NO_4PMS";

    }

//    Construct adapter with items
    public WorkingSchedulesAdapter(WorkingSchedulesActivity workingSchedulesActivity, Context context, int resource) {
        super(context, resource);
        this.workingSchedulesActivity = workingSchedulesActivity;
    }

    public WorkingSchedulesAdapter(WorkingSchedulesActivity workingSchedulesActivity, Context context, int resource, List<WorkingSchedule> objects) {
        super(context, resource, objects);
        this.workingSchedulesActivity = workingSchedulesActivity;
        schedulesDatabase = objects;
        acceptableSchedules = objects;
    }

    @Override
    public WorkingSchedule getItem(int position) {
        return acceptableSchedules.get(position);
    }

    @Override
    public int getCount() {
        return acceptableSchedules.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
//        Inflate view with just image view
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.working_schedules_list_row, null);
        }

//        Get the schedule and set the imageview to the image associated with it
        WorkingSchedule schedule = getItem(position);

        ((ImageView) v.findViewById(R.id.image)).setImageBitmap(schedule.getBitmap(workingSchedulesActivity.getImageWidth()));

//        On click, open DetailedScheduleActivity
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(workingSchedulesActivity, DetailedScheduleActivity.class);
                intent.putExtra("com.ek307.voidbowels.WorkingSchedulesActivity.ScheduleIndex", position);
                workingSchedulesActivity.startActivity(intent);
            }
        });

        if (position == getCount() - 1)
            schedule.printURL();
        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
//                Check which filters the user wants to apply
                boolean noMondays = constraint.toString().contains(AdapterFilters.NO_MONDAYS);
                boolean noFridays = constraint.toString().contains(AdapterFilters.NO_FRIDAYS);
                boolean no8ams = constraint.toString().contains(AdapterFilters.NO_8AMS);
                boolean no4ams = constraint.toString().contains(AdapterFilters.NO_4PMS);

                FilterResults results = new FilterResults();
                List<WorkingSchedule> filteredSchedules = new ArrayList<>();

//                Iterate through schedules and  if schedule is acceptable
                for (WorkingSchedule schedule : schedulesDatabase) {
                    boolean isAcceptable = !
                            ((noFridays && schedule.hasFridays()) ||
                            (noMondays && schedule.hasMondays()) ||
                            (no8ams && schedule.has8AMs()) ||
                            (no4ams && schedule.has4PMs()));

//                    If it is acceptable, add it to the new list
                    if (isAcceptable)
                        filteredSchedules.add(schedule);
                }

                results.count = filteredSchedules.size();
                results.values = filteredSchedules;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//               Use new list for adapter
                acceptableSchedules = (List<WorkingSchedule>) results.values;
                CourseList.setFilteredSchedules(acceptableSchedules);
                notifyDataSetChanged();
//                Notify user how many schedules were found
                Toast.makeText(getContext(), acceptableSchedules.size() + " Schedules Found!", Toast.LENGTH_LONG).show();
            }
        };
    }
}
