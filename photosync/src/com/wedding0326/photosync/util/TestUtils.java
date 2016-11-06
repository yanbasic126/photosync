package com.wedding0326.photosync.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Display;

public class TestUtils {

    public static Date LAST_TIME = new Date();

    public static void printTime(String name) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
        long last = LAST_TIME.getTime();
        LAST_TIME = new Date();
        long duration = LAST_TIME.getTime() - last;
        System.out.println("[" + dateFormat.format(LAST_TIME) + "]: " + name + " duration:" + duration);
    }

    public static void openSleak() {

        DeviceData data = new DeviceData();

        data.tracking = true;

        Display display = new Display(data);

        Sleak sleak = new Sleak();

        sleak.open();

    }

}
