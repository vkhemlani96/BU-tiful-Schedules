package com.ek307.voidbowels.buschedulemaker.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinay on 11/30/15.
 */
public class WorkingSchedule {

    private List<TimeBlock> timeBlocks = new ArrayList<>();
    private String imageURL = "http://www.bu.edu/uiszl_j2ee/ScheduleImage/ScheduleImageServlet?c1=";
    private static BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    private Bitmap imageBitmap;

//    Construct with all timeblocks inside of it
    public WorkingSchedule(List<TimeBlock> timeBlocks) {
        this.timeBlocks = timeBlocks;
        for (TimeBlock block: timeBlocks) {
            imageURL = block.printURL(imageURL);
        }
        imageURL = imageURL.substring(0, imageURL.lastIndexOf("c"));

        bmOptions.inSampleSize = 1;
    }

    public List<TimeBlock> getTimeBlocks() {
        return timeBlocks;
    }

//    Print URL for image for debugging
    public void printURL() {
        int lastActivityTime = (int) System.currentTimeMillis()/1000 - CourseList.getRecordedActivityTime() + CourseList.getBUActivityTime();
        long e = 10000000000l - lastActivityTime;

        System.out.println(imageURL + "e=" + e + "&height=412&width=631&LastActivityTime=" +
                lastActivityTime);
    }

//    Check if any classes are at or before 8AM to filter out
    public boolean has8AMs() {
        for (TimeBlock blocks : timeBlocks) {
            for (double[] blockTimes : blocks.getTimes())
                if (blockTimes[0] % 24 <= 8) //Formula for checking
                    return true;
        }
        return false;
    }

//    Check if classes end after 4pm
    public boolean has4PMs() {
        for (TimeBlock blocks : timeBlocks) {
            for (double[] blockTimes : blocks.getTimes())
                if (blockTimes[1] % 24 >= 16) //Formula
                    return true;
        }
        return false;
    }

//    Check if classes start on a Monday
    public boolean hasMondays() {
        for (TimeBlock blocks : timeBlocks) {
            for (double[] blockTimes : blocks.getTimes())
                if ((int) blockTimes[0]/ 24 < 2) //Formula
                    return true;
        }
        return false;
    }

//    Check if classes are after thursday
    public boolean hasFridays() {
        for (TimeBlock blocks : timeBlocks) {
            for (double[] blockTimes : blocks.getTimes())
                if ((int) blockTimes[0]/ 24 >= 5) //Formula
                    return true;
        }
        return false;
    }

//    Generate Image URL based on time and then download image
    public Bitmap getBitmap(int width) {
        if (imageBitmap != null)
            return imageBitmap;

//        Calculate new time based on time duration
        int lastActivityTime = (int) System.currentTimeMillis()/1000 - CourseList.getRecordedActivityTime() + CourseList.getBUActivityTime();

        imageURL += "e=" +
                (10000000000l - lastActivityTime) + "&height=" + (int) (.652 * width) + "&width=" + width + "&LastActivityTime=" + (lastActivityTime);
        Bitmap bitmap = null;
        InputStream in = null;

//        Try to download image and return it to activity
        try {
            in = OpenHttpConnection(imageURL);
            bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) (bitmap.getWidth() * .80), bitmap.getHeight());
            in.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        imageBitmap = bitmap;
        return imageBitmap;
    }

//    Get larger form of bitmap to change by using same process as getBitmap
    public Bitmap getLargeBitmap() {
        int lastActivityTime = (int) System.currentTimeMillis()/1000 - CourseList.getRecordedActivityTime() + CourseList.getBUActivityTime();
        String largeImageURL = imageURL.replaceAll("&height=\\d+&width=\\d+", "&height=412&width=631");

        Bitmap bitmap = null;
        InputStream in = null;

        try {
            in = OpenHttpConnection(imageURL);
            bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
            in.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        imageBitmap = bitmap;
        return imageBitmap;
    }

//    Used to download image
    private static InputStream OpenHttpConnection(String strURL)
            throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
        }
        return inputStream;
    }

}
