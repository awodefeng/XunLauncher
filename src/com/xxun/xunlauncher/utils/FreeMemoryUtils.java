package com.xxun.xunlauncher.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.List;
import android.app.ActivityManagerNative;
import com.xxun.xunlauncher.application.LauncherApplication;

/**
 * @author lihaizhou
 * @time 2017.12.20
 * @class describe 释放内存, 作用等同于清理最近使用程序
 */

public class FreeMemoryUtils {

    private FreeMemAsyncTask freeMemAsyncTask;
    private static final String TAG = "FreeMemoryUtils";

    private FreeMemoryUtils(){}

    public static FreeMemoryUtils getFreeMemoryUtilsInstance(){
        return FreeMemoryUtilsHolder.freeMemoryUtils;
    }

    private static class FreeMemoryUtilsHolder{
        private static FreeMemoryUtils freeMemoryUtils = new FreeMemoryUtils();
    }


    public void freeMemory(){

        if(freeMemAsyncTask == null){
            freeMemAsyncTask = new FreeMemAsyncTask();
        }
        freeMemAsyncTask.execute();

    }

    private class FreeMemAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            removebackgroundTaks();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {}

        @Override
        protected void onPostExecute(String result) {}

        @Override
        protected void onCancelled() {}
    }

    public void removebackgroundTaks(){
        ActivityManager mActivityManager = (ActivityManager) LauncherApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        //mActivityManager.killAllBackgroundProcessesExceptWhiteList();
	    try {
            ActivityManagerNative.getDefault().killAllBackgroundProcessesExceptWhiteList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
