package com.ek307.voidbowels.buschedulemaker.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vinay on 11/22/15.
 */
public class Course {

    private String college; //String to hold college code
    private String dept; //String to hold dept code
    private HashMap <String, List<TimeBlock>> courseMap = new HashMap<>(); //HashMap to group TimeBlocks by Section type

    public Course() {
        college = "";
        dept = "";
    }

    public String getCollege() {
        return college;
    }

    public String getDept() {
        return dept;
    }

//    Construct Course with needed information
    public Course(String[] sectionInfo) {
        String classInfo = sectionInfo[SectionIndices.CLASS];
        String splitChar = String.valueOf(classInfo.charAt(3));
        String[] classInfoArray = classInfo.split(splitChar);

        this.college = classInfoArray[0].trim();
        this.dept = classInfoArray[1].trim();
    }

//    Add the timeblock to the hashmap
    public void addTimeBlock(String[] sectionInfo) {
        String type = sectionInfo[SectionIndices.TYPE];
//        If the section type has already been found, add the TimeBlock to it
        if (courseMap.containsKey(type)) {

            List<TimeBlock> timeBlocks = courseMap.get(type);
//            If there is another section at the same time, add this section info to the same TimeBlock
            for (TimeBlock block : timeBlocks) {
                if (block.isArrayEqual(sectionInfo)) {
                    block.addSection(sectionInfo);
                    return;
                }
            }

//            Otherwise, create a new timeBlock and add it to the course
            TimeBlock newBlock = new TimeBlock(this, type);
            newBlock.addSection(sectionInfo);
            timeBlocks.add(newBlock);

        } else {
//            If the type isn't already existing, create it and add the TimeBlock to it, add it to the map
            List<TimeBlock> timeBlocks = new ArrayList<>();
            TimeBlock newBlock = new TimeBlock(this, type);
            newBlock.addSection(sectionInfo);
            timeBlocks.add(newBlock);

            courseMap.put(type, timeBlocks);
        }
    }

//    To check if two Courses are equal, check if all the info inside of them is the same
    @Override
    public boolean equals(Object o) {
        if (o instanceof String[]) {

            String[] sectionInfo = (String[]) o;
            String classInfo = sectionInfo[SectionIndices.CLASS];
            String splitChar = String.valueOf(classInfo.charAt(3));
            String[] classInfoArray = classInfo.split(splitChar);

            return classInfoArray[0].equals(this.college) &&
                    classInfoArray[1].equals(this.dept);

        } else if (o instanceof Course) {

            Course c = (Course) o;
            return c.college.equals(this.college) &&
                    c.dept.equals(this.dept);

        }

        return false;
    }

//    Return a collection of all the TimeBlock lists together
    public List getTimeBlockLists() {
        List<Set<TimeBlock>> timeBlockLists = new ArrayList<>();
        for(List l : courseMap.values())
            timeBlockLists.add(new HashSet<>(l));
        return timeBlockLists;
    }

}
