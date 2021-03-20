package com.xxun.xunlauncher.receiver;

import android.app.ActivityManager;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Log;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;

import com.xxun.xunlauncher.activity.MainActivity;
import com.xxun.xunlauncher.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class LauncherAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "LauncherAlarmReceiver";
    //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
    private static final String duerActivityName = "com.xxun.duer.dcs.XunDuerBaseActivity";
    private static final String otaDownloadServiceName = "com.xxun.watch.xunsettings.service.DownloadService";
    private static final String otaAutoDownloadServiceName = "com.xxun.watch.xunsettings.service.AutoDownloadService";
    private static final String  storyDownloadServiceName="com.xxun.watch.storydownloadservice.DownloadService";
    private AlarmManager closeWifialarmManager;
    //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 end

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.KEEP_WIFI_CONNECT.equals(intent.getAction())) {
            Log.d(TAG, "receive closewifi alarm broadcaset");
            closeWifi(context);
        }
    }

    /**
     * @author lihaizhou
     * @time 2018.03.16
     * @class describe close wifi
     */
    public void closeWifi(Context context) {
        int keepvalue = Settings.System.getInt(context.getContentResolver(),"keep_wifi_connect",1);
        //Modify by xuzhonghong for when power connect the wifi don't connect on 20180320 start
        boolean isUsbConfigured = SystemProperties.getBoolean("persist.sys.isUsbConfigured", false);
        String isUpgrade = SystemProperties.get("persist.sys.xxun.ota_status");
        //Modify by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
        boolean isActivityTop = isActivityTop(context,duerActivityName);
        boolean isOtaDownloadServiceRunning = isServiceRunning(context,otaDownloadServiceName);
        boolean isStoryDownloadServiceName = isServiceRunning(context,storyDownloadServiceName);
        boolean isOtaAutoDownloadServiceRunning = isServiceRunning(context,otaAutoDownloadServiceName);
        //Modify by xuzhonghong on 20180528 start
        int story_downloading_flag = Settings.Global.getInt(context.getContentResolver(),"story_downloading_flag",99);
        Log.d(TAG, "begin closewifi, current keepvalue is = " + keepvalue + ",isUsbConfigured = " + isUsbConfigured + ",isUpgrade = " + isUpgrade
                + ",isActivityTop = " + isActivityTop + ",isOtaDownload = " + isOtaDownloadServiceRunning + ",isOtaAutoDownload = " + isOtaAutoDownloadServiceRunning + ",story_downloading_flag = " + story_downloading_flag);
        if (keepvalue == 0 && isUsbConfigured == false/* && !isActivityTop */&& (!(isUpgrade.equals("1") && (isOtaDownloadServiceRunning || isOtaAutoDownloadServiceRunning))) && (story_downloading_flag == 0 || !isStoryDownloadServiceName)) {
        //Modify by xuzhonghong on 20180528 end
        //Modify by xuzhonghong for XUN_SW710_A01-516 on 20180521 end
        //Modify by xuzhonghong for when power connect the wifi don't connect on 20180320 end
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 start
        } else {
            if (!isScreenon(context)) {
                Log.d(TAG,"closeWifi | screen is off");
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                if(wifiManager.isWifiEnabled()) {
                    setCloseWifiAlarm(context);
                }
            }
        //Add by xuzhonghong for XUN_SW710_A01-516 on 20180521 end
        }
    }

    /**
     * @author xuzhonghong
     * @time 2018.05.21
     * @class describe 判断Activity是否在栈顶，true: 是 false: 否
     */
    private boolean isActivityTop(Context context, String ClassName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(ClassName);
    }

    /**
     * @author xuzhonghong
     * @time 2018.05.21
     * @class describe 判断Service是否在后台运行，true: 是 false: 否
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @author xuzhonghong
     * @time 2018.05.21
     * @class describe set alarm to close wifi after 5 min
     */
    public void setCloseWifiAlarm(Context context) {
        Log.d(TAG,"LauncherAlarmReceiver | setCloseWifiAlarm");
        closeWifialarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LauncherAlarmReceiver.class);
        intent.setAction(Constants.KEEP_WIFI_CONNECT);
        MainActivity.getmainacticvityInstance().pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);
        closeWifialarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), MainActivity.getmainacticvityInstance().pendingIntent);
    }

    /**
     * @author xuzhonghong
     * @time 2018.05.21
     * @class describe 判断设备当前亮灭屏状态，true: 亮屏 false: 灭屏
     */
    public boolean isScreenon(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

}
