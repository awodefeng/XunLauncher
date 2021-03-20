package com.xxun.xunlauncher.application;

import android.app.Application;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import android.view.KeyEvent;
import android.app.Activity;
import java.util.Set;
import android.util.Log;
//import com.tencent.bugly.crashreport.CrashReport;
import com.xiaoxun.statistics.XiaoXunStatisticsManager;

import com.xxun.xunlauncher.utils.PhaseCheckParse;
import android.os.SystemProperties;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 自定义Application类
 */
public class LauncherApplication extends Application {

    private static Context mContext;
    private static int homeScreenIndex;
    private static boolean isLoginSuccess;    
	   
    private static Map<String, Activity> destoryMap = new HashMap<String, Activity>();

    private static XiaoXunStatisticsManager statisticsManager;
    
    private static final String TAG = "LauncherApplication";

    public static int defalutClockStyleIndex;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
	//CrashReport.initCrashReport(getApplicationContext(), "0014757acb", true);
        statisticsManager = (XiaoXunStatisticsManager) getSystemService("xun.statistics.service");

        readSn();
    }

    public static Context getInstance() {
        return mContext;
    }

    public static int getHomeScreenIndex() {
        return homeScreenIndex;
    }

    public static void setHomeScreenIndex(int homescreenIndex) {
        homeScreenIndex = homescreenIndex;
    }

 
    /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @param activity 要销毁的activity 
     * @describe 将需要在其他地方也能控制销毁的activity加入该集合中
     */
    public static void addIntoWaitDestoryList(Activity activity,String activityName) {
        Log.d(TAG,"addIntoWaitDestoryList"); 
        destoryMap.put(activityName,activity);  
    }  

    /**
     * @author lihaizhou
     * @createtime 2018.06.08
     * @param activityName 需要销毁的activity名称
     * @describe 销毁指定名称的activity，从集合中取符合name的key从而拿到对应的activity
     */ 
    public static void destoryActivity(String activityName) {
    Log.d(TAG,"destoryActivity"); 
	try{  
           Set<String> keySet = destoryMap.keySet();
           if (keySet.size() > 0) {
               for (String key : keySet) {
                 if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                 }
               }
	   }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static XiaoXunStatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    //*/ xiaoxun.zhangweinan, 20180313. for Read SN from NV & save value
    private void readSn() {
        byte[] mSNBuff = new byte[15];
        byte[] mBootBuff = new byte[1];
        String mAPSN = "";
        char bootFlag = '0';
        /*
        byte[] mSNBuff = new byte[15];
        byte[] mBootBuff = new byte[1];
        String mAPSN = "";
        char bootFlag = '0';
        try {
            FileInputStream fileInstream = new FileInputStream(SN_FLAG_FILE);
            fileInstream.read(mSNBuff);
        } catch (Exception e) {
            Log.i(TAG, "read sn flag error = " + e);
        }
        for(int i=0; i<15; i++){
            char mValue = (char)(new Byte(mSNBuff[i])).intValue();
            mAPSN += String.valueOf(mValue).trim();
        }
        */
        PhaseCheckParse parse = new PhaseCheckParse();
        if (parse != null) {
            mAPSN = parse.getSn2().trim();
        }
        Log.i(TAG, "SN =" + mAPSN);

        //Log.i(TAG, "defalutClockStyleIndex =" + "SWX004".equals(mAPSN.substring(0, 6)));
        SystemProperties.set("persist.sys.xxun.sn", mAPSN);

        //Add by lihaizhou for defaultclockstyle begin 20190130 #sync code from 705
        if (mAPSN != null && mAPSN.length() >= 6) {
            if ("SWX003".equals(mAPSN.substring(0, 6)) || "SWX004".equals(mAPSN.substring(0, 6))) {
                defalutClockStyleIndex = 1;
            } else if ("SWF005".equals(mAPSN.substring(0, 6)) || "SWF006".equals(mAPSN.substring(0, 6))) {
                defalutClockStyleIndex = 2;
            }
        }
        //Add by lihaizhou for defaultclockstyle end 20190130 #sync code from 705
    }

}
