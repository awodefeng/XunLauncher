package com.xxun.xunlauncher.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by liaoyi on 3/21/18.
 */
public class PowerOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int hour = intent.getIntExtra("poweronhour", -1);
            int min = intent.getIntExtra("poweronmin", -1);

            if (hour != -1 && min != -1) {
                Calendar calendar = Calendar.getInstance();
                long timeInMillis = calendar.getTimeInMillis();

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                long newTimeInMillis = calendar.getTimeInMillis();
                long diff = newTimeInMillis - timeInMillis;
                if (diff <= 0) {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, min);
                    newTimeInMillis = calendar.getTimeInMillis();
                }

                Log.i("lyly", " lyly onReceive: day = " + calendar.get(Calendar.DAY_OF_MONTH) + " hour = " + calendar.get(Calendar.HOUR_OF_DAY) + " min = " + calendar.get(Calendar.MINUTE));

                schpwrOn(context, newTimeInMillis);

                shutDownWatch(context);
            }
        }
    }

    public void schpwrOn(Context context, long millis) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent_poweron = new Intent("com.android.settings.action.REQUEST_POWER_ON");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent_poweron, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(5, millis, pendingIntent);
    }

    public void shutDownWatch(Context context) {
        Intent shutDownintent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        shutDownintent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        shutDownintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shutDownintent);
    }
}
