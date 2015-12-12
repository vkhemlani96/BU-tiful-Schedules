package com.ek307.voidbowels.buschedulemaker.data;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by Vinay on 11/19/15.
 */
public class CourseList {

    private static List<Course> COURSES = new ArrayList<>();
    private static List<WorkingSchedule> WORKING_SCHEDULES = new ArrayList<>();
    private static List<WorkingSchedule> FILTERED_SCHEDULES;
    private static int BUActivityTime = 0;
    private static int recordedActivityTime = 0;

//    record and set BU system's activity time
    public static void setBUActivityTime(int time) {
        BUActivityTime = time;
        recordedActivityTime = (int) System.currentTimeMillis() / 1000;
    }

    public static int getBUActivityTime() {
        return BUActivityTime;
    }

    public static int getRecordedActivityTime() {
        return recordedActivityTime;
    }

    public static void add(String[] sectionInfo) {
        Course foundCourse = getCourse(sectionInfo);
//        If the course doesn't already exist, create it
        if (foundCourse == null) {
            foundCourse = new Course(sectionInfo);
            COURSES.add(foundCourse);
        }

//        If a row contains multiple TimeBlocks, seperate them and create TimeBlocks for each
        String[] types = sectionInfo[SectionIndices.TYPE].split(" ");
        String[] blds = sectionInfo[SectionIndices.BUILDING].split(" ");
        String[] rooms = sectionInfo[SectionIndices.ROOM].split(" ");
        String[] days = sectionInfo[SectionIndices.DAYS].split(" ");
        String[] starts = sectionInfo[SectionIndices.START].split(" ");
        String[] ends = sectionInfo[SectionIndices.END].split(" ");

//        Iterate through the row and create TimeBlocks with the appropriate data
        for (int i = 0; i < starts.length; i++) {
            String[] specificSectionInfo = {
                    null,
                    sectionInfo[SectionIndices.CLASS],
                    sectionInfo[SectionIndices.NAME],
                    null,
                    sectionInfo[SectionIndices.CREDITS],
                    i < types.length ? types[i] : types[0],
                    sectionInfo[SectionIndices.SEATS],
                    i < blds.length ? blds[i] : blds[0],
                    i < rooms.length ? rooms[i] : rooms[0],
                    i < days.length ? days[i] : days[0],
                    i < starts.length ? starts[i] : starts[0],
                    i < ends.length ? ends[i] : ends[0],
                    sectionInfo[SectionIndices.NOTES]
            };

            foundCourse.addTimeBlock(specificSectionInfo);
        }
    }

//    Get Course if it already exists, otherwise return null
    private static Course getCourse(String[] sectionInfo) {
        for (Course c : COURSES)
            if (c.equals(sectionInfo))
                return c;
        return null;
    }

    public static List<WorkingSchedule> getSchedules() {
//        If schedules have already been found, return them
        if (WORKING_SCHEDULES.size() > 0)
            return WORKING_SCHEDULES;

//        Otherwise create a list of working ones
        List<Set<TimeBlock>> completeBlockList = new ArrayList<>();
//        Get all the different Lists of TimeBlocks needed
        for (Course c : COURSES)
            completeBlockList.addAll(c.getTimeBlockLists());
//        Create the cartesian product of them all
        Set<List<TimeBlock>> setOfBlocks = Sets.cartesianProduct(completeBlockList);

        List<TimeBlock>[] blockArray = new List[setOfBlocks.size()];
        setOfBlocks.toArray(blockArray);

//        For each schedule generated, check if it has no overlaps and if so, dont add it to the final list
        for (List<TimeBlock> list : blockArray)
            if (isScheduleValid(list)) {
                WORKING_SCHEDULES.add(new WorkingSchedule(list));
            }

        FILTERED_SCHEDULES = WORKING_SCHEDULES;
        return WORKING_SCHEDULES;

    }

    public static List<WorkingSchedule> getFilteredSchedules() {
        return FILTERED_SCHEDULES;
    }

    public static void setFilteredSchedules(List<WorkingSchedule> schedules) {
        CourseList.FILTERED_SCHEDULES = schedules;
    }

//    Reset all lists
    public static void clearWorkingSchedules() {
        COURSES.clear();
        WORKING_SCHEDULES.clear();
        FILTERED_SCHEDULES.clear();
    }

    private static boolean isScheduleValid(List<TimeBlock> listOfBlocks) {

//        Compile all the times for the time blocks into one list
        List<double[]> timeBlockArray = new ArrayList<>();
        for (TimeBlock block : listOfBlocks)
            timeBlockArray.addAll(block.getTimes());

//        Sort each timeblock
        Collections.sort(timeBlockArray, new Comparator<double[]>() {
            @Override
            public int compare(double[] lhs, double[] rhs) {
                if (lhs[0] > rhs[0])
                    return 1;
                else if (lhs[0] < rhs[0])
                    return -1;
                return 0;
            }
        });

//        Check for overlap, if there is overlap, return false. Otherwise return true
        for (int i = 0; i < timeBlockArray.size(); i++) {
            if (i > 0 && timeBlockArray.get(i)[0] < timeBlockArray.get(i-1)[1])
                return false;
            if (i < timeBlockArray.size()-1 && timeBlockArray.get(i)[1] > timeBlockArray.get(i+1)[0])
                return false;
        }

        return true;
    }

}
