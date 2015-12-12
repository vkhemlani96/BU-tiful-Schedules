package com.ek307.voidbowels.buschedulemaker.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vinay on 11/22/15.
 */
public class TimeBlock {

    List<String[]> sections;
    List<double[]> times;
    Course course;
    String sectionType;

    public TimeBlock() {
        this.sections = new ArrayList<String[]>();
        this.times = new ArrayList<double[]>();
        this.course = new Course();
        this.sectionType = "";
    }

//    Construct time blocks with Course and sectionType
    public TimeBlock(Course course, String sectionType) {
        this.sections = new ArrayList<String[]>();
        this.times = new ArrayList<double[]>();
        this.course = course;
        this.sectionType = sectionType;
    }

    public String getSectionType() {
        return sectionType;
    }

//    Add necessary section info to it
    public void addSection(String[] sectionInfo) {
        if (times.size() == 0) {
            times.addAll(calculateTimes(sectionInfo));
        }

        char splitChar = sectionInfo[SectionIndices.CLASS].charAt(3);
        String[] section = {
                sectionInfo[SectionIndices.CLASS]
                        .substring(sectionInfo[SectionIndices.CLASS].lastIndexOf(splitChar) + 1),   //Section Number
                sectionInfo[SectionIndices.NAME]
                        .substring(sectionInfo[SectionIndices.NAME].lastIndexOf(' ') + 1),          //Professor Name
                sectionInfo[SectionIndices.SEATS].trim(),                                           //Seats
                sectionInfo[SectionIndices.BUILDING].trim(),                                        //Building
                sectionInfo[SectionIndices.ROOM].trim()                                             //Room
        };

        sections.add(section);

    }

//    Calculate absolute times from Sun 12:00am
    private List<double[]> calculateTimes(String[] sectionInfo) {
        ArrayList<double[]> times = new ArrayList<>();

        if (times.size() == 0) {
//            Split into each array
            String[] days = sectionInfo[SectionIndices.DAYS].split(",");
            for (String d : days) {
                double[] timeBlock = new double[3];

//                Split into start and end times and calculate each before adding to list
                timeBlock[0] = getAbsoluteTime(d.trim(), sectionInfo[SectionIndices.START]);
                timeBlock[1] = getAbsoluteTime(d.trim(), sectionInfo[SectionIndices.END]);
                timeBlock[2] = getDayInt(d.trim());

                times.add(timeBlock);
            }
        }

        return times;
    }

//    Return what number day of the week it is
    private int getDayInt(String day) {
        switch (day) {
            case "Sun": return 0;
            case "Mon": return 1;
            case "Tue": return 2;
            case "Wed": return 3;
            case "Thu": return 4;
            case "Fri": return 5;
            case "Sat": return 6;
        }
        return -1;
    }

//    Go from number day of week to String
    private String getDayString(int timeIndex) {
        switch ((int) times.get(timeIndex)[2]) {
            case 0: return "Sun";
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
        }
        return "Sun";
    }

//    Calcuate hours from Sun 12:00am
    private double getAbsoluteTime(String day, String time) {
//        Add 24 hours for each day
        double absoluteTime = 0;
        absoluteTime += getDayInt(day) * 24;

        if (absoluteTime < 0)
            return 0;

        String[] timeParts = time.split(":");
        try {
//            Add whole number of hours
            absoluteTime += Double.parseDouble(timeParts[0]) % 12;
        } catch (Exception e) {
            System.out.println(day + " " + time);
            absoluteTime += Double.parseDouble(timeParts[0]) % 12;
        }

//        If the time is after noon, add 12 more hours
        absoluteTime += timeParts[1].contains("pm") ? 12 : 0;
//        Add minutes in the decimal form of an hour
        absoluteTime += Double.parseDouble(timeParts[1].replaceAll("[apm]", "")) / 60.0;

        return absoluteTime;
    }

//    Convert to military time for use in image URL
    private int getMilitaryTime(double time) {
        int hours = (((int) time) % 24) * 100;
        int mins = time % 1 == 0 ? 0 : 30;
        return hours + mins;
    }

//    Convert back from absolute time to time of day
    private String getTimeString(double time) {
        String minutes = time - ((int) time) == .5 ? ":30" : ":00";
        String hours = String.valueOf(((int) time) % 12);
        if (hours.equals("0"))
            hours = "12";
        String amPM = ((int) time) % 24 >= 12 ? "pm" : "am";
        return hours + minutes + amPM;
    }

//    For an array to be equal, all the time values must be equal
    public boolean isArrayEqual(String[] sectionInfo) {
        List<double[]> timesList = calculateTimes(sectionInfo);
        for (double[] list: timesList) {
            for (double[] time : times)
                if (Arrays.equals(time, list))
                    return true;
        }
        return false;
    }

//    Print TimeBlock for debugging
    public void print() {
        for (int i = 0; i < times.size(); i++)
            System.out.print(Arrays.toString(times.get(i)));
        System.out.print(" | ");
        for (int i = 0; i < sections.size(); i++)
            System.out.print(Arrays.toString(sections.get(i)));
        System.out.println();
    }

    public List<double[]> getTimes() {
        return times;
    }

    public List<String[]> getSections() {
        return sections;
    }

    public Course getCourse() {
        return course;
    }

//    Create URL for the Image, see Project Documention for URL template
    public String printURL(String inURL) {
        String outURL = inURL;
        int nStart = inURL.lastIndexOf("c") + 1;
        int nEnd = inURL.lastIndexOf("=");
        int courseN = Integer.parseInt(inURL.substring(nStart, nEnd));
//        c1=CAS+RN103+A1&d1=Tue&tb1=1400&te1=1530&db1=20160119&de1=20160428&
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i)[2] != -1) {
                int n = courseN + i;
                int startTime = getMilitaryTime(times.get(i)[0]);
                int endTime = getMilitaryTime(times.get(i)[1]);
                outURL += course.getCollege() + "+" + getCourse().getDept() + "+" + sections.get(0)[0] + "&d" + n + "=" + getDayString(i) + "&tb" + n + "=" + (startTime < 1000 ? 0 : "") + startTime + "&te" + n + "=" + (endTime < 1000 ? 0 : "") + endTime + "&db" + n + "=20160119&de" + n + "=20160428&" + "c" + (n+1) + "=";
            }
        }
        return outURL;
    }

//    Format Time for display in Detailed View
    public String getFormattedDayTimes() {
        String formatted = "";
        for (int i = 0; i < times.size(); i++) {
            formatted += getDayString(i) + " (";
            formatted += getTimeString(times.get(i)[0]) + " - " + getTimeString(times.get(i)[1]) + ")";
            if (i != times.size()-1)
                formatted += "\n";
        }
        return formatted;
    }
}
