package com.xxun.xunlauncher.utils;

import android.content.Intent;
import com.xxun.xunlauncher.application.LauncherApplication;

/**
 * @author lihaizhou
 * @createtime 2017.12.21
 * @class describe shutDown&factoryReset工具类
 */
public class ShutDownUtils {
    
    private ShutDownUtils(){}
	
    public static ShutDownUtils getShutDownUtilsInstance(){
        return ShutDownUtilsHolder.shutDownUtils;
    }

    private static class ShutDownUtilsHolder{
        private static ShutDownUtils shutDownUtils = new ShutDownUtils();
    }
    
    /**
     * @author lihaizhou
     * @createtime 2017.12.21
     * @describe do factoryReset,wipe all user data
     */
    public void factoryResetWatch(){
        Intent factoryIntent = new Intent("android.intent.action.MASTER_CLEAR");
	factoryIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
	factoryIntent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
	factoryIntent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
	LauncherApplication.getInstance().sendBroadcast(factoryIntent);
    }
	

    /**
     * @author lihaizhou
     * @createtime 2017.12.21
     * @describe shutDown watch
     */
    public void shutDownWatch(){
        Intent shutDownintent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        shutDownintent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        shutDownintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LauncherApplication.getInstance().startActivity(shutDownintent);
    }
	 
	
      	


}

