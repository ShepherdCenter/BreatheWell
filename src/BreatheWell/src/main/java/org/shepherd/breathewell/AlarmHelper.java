package org.shepherd.breathewell;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by scott on 3/14/2015.
 */
public class AlarmHelper {

    public static boolean isTime(String sTime) {
        if (sTime.equals("None")){
            return false;
        }
        // 9 am
        String time[] = sTime.split(" ");
        return (time.length == 2);
    }
    private static void setAlarm(Calendar calendar, String sTime) {

        String speech[] = sTime.split(" ");
        String time = speech[0];
        String timeParts[] = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        String ampm = speech[1].toUpperCase();
        if (ampm.startsWith("P"))
        {
            hour += 12;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    public static void addAlarm(Context context, int num, String sTime) {

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        setAlarm(calendar, sTime);


        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("publish", true);
        alarmIntent = PendingIntent.getBroadcast(context, num, intent, 0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public static void cancelAlarm(Context context, int num) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, num, intent, 0);
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

    }


}
