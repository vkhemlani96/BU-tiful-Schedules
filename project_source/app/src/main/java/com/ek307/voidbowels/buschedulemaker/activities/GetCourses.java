package com.ek307.voidbowels.buschedulemaker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.ek307.voidbowels.buschedulemaker.activities.WorkingSchedulesActivity;
import com.ek307.voidbowels.buschedulemaker.data.CourseList;
import com.ek307.voidbowels.buschedulemaker.data.WorkingSchedule;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vinay on 11/22/15.
 */
public class GetCourses extends AsyncTask<Void, Void, Void> {


    private String[][] classCodes;
    private Context context;
    private ProgressDialog pDialog;

    public GetCourses(Context context, String[][] classCodes, ProgressDialog pDialog) {
        this.classCodes = classCodes;
        this.context = context;
        this.pDialog = pDialog;
    }

    @Override
    protected void onPreExecute() {
//        Sets up Progress Dialog to keep user updated on status of process
        super.onPreExecute();
        pDialog.setMax(classCodes.length + 1);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setMessage("Retrieving Course Information...");
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setProgressNumberFormat("");
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        Iterates through each class
        for (int i = 0; i < classCodes.length; i++) {
            pDialog.setProgress(i);
            String[] classCode = classCodes[i];

//            Forms course-specific URL
            String URL = "https://www.bu.edu/link/bin/uiscgi_studentlink.pl/1447905455?ModuleName=univschr.pl&SearchOptionDesc=Class+Number&SearchOptionCd=S&KeySem=20164&ViewSem=Spring+2016&College=" + classCode[0] + "&Dept=" + classCode[1] + "&Course=" + classCode[2] + "&Section=";
//            Attempts to process get request to website
            try {
//                Get all row items in from HTML code and determines if they apply to given course
                Document doc  = Jsoup.connect(URL).get();
                Elements rows = doc.select("tr");

                for (Element e : rows) {
                    if (e.toString().contains(classCode[0] + "&nbsp;" + classCode[1] + classCode[2])) {
//                        If they apply to the course, store parse through the row and store needed data
                        Document tableRow = Jsoup.parse(e.toString());
                        Elements rowElements = tableRow.select("font");
                        String[] sectionInfo = getSectionInfo(rowElements);

//                        Store the data in static class, CourseList
                        CourseList.add(sectionInfo);
                    }
                }

//                Store BU system's activity time, but only do so for the last get request to keep the most recent one
                if (i == classCodes.length-1) {
//                    Fine part of HTML using first three digits of Android's system time (always the same as BU's)
                    String timeURL = doc.select("a[href*=.pl/" + System.currentTimeMillis()/10000000000l
                            + "]").get(0).toString();
                    Pattern activityTimePattern = Pattern.compile(".pl/\\d+");
                    Matcher m = activityTimePattern.matcher(timeURL);

//                    If the time is found in the code of the page, store it
                    while (m.find()) {
                        System.out.println("Time:" + m.group(0).substring(4));
                        System.out.println(System.currentTimeMillis());

                        CourseList.setBUActivityTime(Integer.valueOf(m.group(0).substring(4)));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CourseList.getSchedules();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        Update user and then start next activity
        super.onPostExecute(aVoid);

        Intent intent = new Intent(context, WorkingSchedulesActivity.class);
        context.startActivity(intent);
    }

//    Parses through row and stores information in an array
    protected String[] getSectionInfo(Elements e) {
        String[] sectionInfo = new String[e.size()];
        for (int i = 0; i < e.size(); i++)
            sectionInfo[i] = e.get(i).text().trim();
        return sectionInfo;
    }


}
