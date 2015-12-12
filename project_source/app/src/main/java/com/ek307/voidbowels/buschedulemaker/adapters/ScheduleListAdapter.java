package com.ek307.voidbowels.buschedulemaker.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ek307.voidbowels.buschedulemaker.data.Course;
import com.ek307.voidbowels.buschedulemaker.data.TimeBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vinay on 12/2/15.
 */
public class ScheduleListAdapter extends BaseExpandableListAdapter {

    Context context;
    Map<Course, List<TimeBlock>> courseMap = new HashMap<>();

    public ScheduleListAdapter(Context context, List<TimeBlock> scheduleTimeBlocks) {
        this.context = context;
//        Group TimeBlocks by their course so they can be displayed together
        for (TimeBlock block : scheduleTimeBlocks) {
            Course c = block.getCourse();
            List<TimeBlock> blockList = courseMap.containsKey(c) ? courseMap.get(c) : new ArrayList<TimeBlock>();
            blockList.add(block);
//            Create an entry with the Course as the key if it doesnt exist
            if (!courseMap.containsKey(c))
                courseMap.put(c, blockList);
        }

//        Reverse them so Lectures come first
        for (List<TimeBlock> list : courseMap.values())
            Collections.reverse(list);


    }

//    Get the number of TimeBlocks for each course
    @Override
    public int getChildrenCount(int groupPosition) {
        Course c = getGroup(groupPosition);
        return courseMap.get(c).size();
    }

//    Get all the Courses in the schedule
    @Override
    public Course getGroup(int groupPosition) {
        return new ArrayList<>(courseMap.keySet()).get(groupPosition);
    }

//    Get the specific TimeBlock
    @Override
    public TimeBlock getChild(int groupPosition, int childPosition) {
        return courseMap.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

//    Create a default simple list item with the name of the course
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        String className = getGroup(groupPosition).getDept();
        TextView tView = (TextView) convertView.findViewById(android.R.id.text1);
        tView.setText(className);
        tView.setGravity(Gravity.CENTER);
        tView.setTypeface(null, Typeface.BOLD);

        return convertView;
    }

//    Return number of Courses in the scheduels
    @Override
    public int getGroupCount() {
        return courseMap.keySet().size();
    }

//    Create the expanded view for each course
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        Inflate two item view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        TimeBlock timeBlock = getChild(groupPosition, childPosition);
        List<String[]> sections = timeBlock.getSections();
        boolean multipleSections = sections.size() > 1;
//        List section name and type (ie. Lecture A1) in first line item
        String line1 = timeBlock.getSectionType() + ": " + (multipleSections ? sections.size() + " Possible Sections" : sections.get(0)[0]);
        line1 += "\n" + timeBlock.getFormattedDayTimes();

//        Concatenate section details (room number, professor...) in second line item
        String line2 = "";
        for (int i = 0; i < sections.size(); i++) {
            String[] sectionInfo = sections.get(i);
            if (multipleSections)
                line2 += sectionInfo[0] + ": ";
            line2 += sectionInfo[1] + " (" + sectionInfo[3] + " " + sectionInfo[4] + ")";
            if (i != sections.size()-1)
                line2 += "\n";
        }

//        Set text and make first item bold
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(line1);
        ((TextView) convertView.findViewById(android.R.id.text1)).setTypeface(null, Typeface.BOLD);
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(line2);

        return convertView;
    }

//    Dont allow for children to be clicked on
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
