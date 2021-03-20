package com.xxun.xunlauncher.utils;

import android.app.Activity;
import java.io.*;
import android.util.Log;
import android.app.ActivityManager;
import android.content.Context;
import java.lang.reflect.Method;
import java.util.List;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.gif.GifTempletView;
import android.app.AlertDialog;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import com.xxun.xunlauncher.R;
import android.os.SystemProperties;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author zhangweinan
 * @createtime 2018.01.10
 * @class 读取主板温度
 */
public class TempControlUtils {
    private final static String TAG = "TempControlUtils";
    private static FileReader mTempCurrent=null;
    private static int  mTempCurrentValue = 0;
    private final static String mAPtempCurrent = "/sys/class/thermal/thermal_zone13/temp";
    private static File file = null;

    private TempControlUtils(){
        
    }

    public static TempControlUtils getTempControlUtilsInstance(){
        return TempControlUtilsHolder.tempControlUtils;
    }

    private static class TempControlUtilsHolder{
        private static TempControlUtils tempControlUtils = new TempControlUtils();
    }

    public static void readCurrentAPtemp() { 
        try {
            file = new File(mAPtempCurrent);
            mTempCurrent = new FileReader(file);
            char[] buf = new char[(int)file.length()];
            int n = mTempCurrent.read(buf);
            if (n > 1) {
                mTempCurrentValue = Integer.parseInt(new String(buf, 0, n-1));
            }
            Log.i(TAG,  "mTempCurrentValue = " + mTempCurrentValue);
        } catch (IOException ex) {
            Log.i(TAG,  "ex: " + ex);
        } catch (NumberFormatException ex) {
            Log.i(TAG, "ex: " + ex);
        } finally {
            if (mTempCurrent != null) {
                try {
                    mTempCurrent.close();
                } catch (IOException ex) {
                }
            }
        }

    }
    
    
  public void forceStopPackage(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo : list){
            if(!"com.xxun.xunlauncher".equals(packageInfo.applicationInfo.packageName) 
                        && !"com.github.uiautomator".equals(packageInfo.applicationInfo.packageName)
                        && !"com.xiaoxun.dialer".equals(packageInfo.applicationInfo.packageName)
                        && !"com.xxun.xunalarm".equals(packageInfo.applicationInfo.packageName)
                        && !packageInfo.applicationInfo.packageName.contains("com.android")
						&& !"com.longcheertel.runintest2".equals(packageInfo.applicationInfo.packageName)){
                forceStopPackage(packageInfo.applicationInfo.packageName,context);
            }
        }
    }

  public void highTemperForceStopPackage(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo : list){
            if(!"com.xxun.xunlauncher".equals(packageInfo.applicationInfo.packageName)
                    && !"com.xxun.watch.xunsettings".equals(packageInfo.applicationInfo.packageName)
				&& !"com.longcheertel.runintest2".equals(packageInfo.applicationInfo.packageName)){
                forceStopPackage(packageInfo.applicationInfo.packageName,context);
            }
        }
    }

   public void forceStopPackage(String packageName,Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
         Method mRemoveTask;
         try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);
            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            Context CONTEXT_INSTANCE =(Context)method2.invoke(currentActivityThread);
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            mRemoveTask = activityManagerClass.getMethod("forceStopPackage", new Class[] { String.class });
            mRemoveTask.setAccessible(true);
            mRemoveTask.invoke(am, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void removeAllTasks(){
         ActivityManager mActivityManager = (ActivityManager) LauncherApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
         Method mRemoveTask;
         try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);
            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            Context CONTEXT_INSTANCE =(Context)method2.invoke(currentActivityThread);
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            mRemoveTask = activityManagerClass.getMethod("removeTask", new Class[] { int.class });
            mRemoveTask.setAccessible(true);
	    //因4.4上接口问题，清理后台应用相关代码暂时注释，待Launcher移植后再调试 by lihaizhou 20180710 begin
            /*List<ActivityManager.RecentTaskInfo> recents = mActivityManager.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
            for (ActivityManager.RecentTaskInfo recentTaskInfo:recents){
                if(null!=recentTaskInfo.baseActivity){
                    if(!"com.xxun.xunlauncher".equals(recentTaskInfo.baseActivity.getPackageName())){
                        mRemoveTask.invoke(mActivityManager, recentTaskInfo.persistentId);
                    }
                }
            }*/
	    //因4.4上接口问题，清理后台应用相关代码暂时注释，待Launcher移植后再调试 20180710 end
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
