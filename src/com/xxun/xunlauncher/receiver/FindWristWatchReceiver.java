package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xxun.xunlauncher.activity.FindWristWatchActivity;

/**
 * Created by liaoyi on 12/21/17.
 * 找手表广播接收处理类
 */
public class FindWristWatchReceiver extends BroadcastReceiver {

    //static final String ACTION_FIND = "com.xunlauncher.find";//Del by lihaizhou 2018.01.18
    private static final String ACTION_FIND = "com.xunlauncher.find";//Add by lihaizhou 2018.01.18
    private static final String TAG = "FindWristWatchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //del by lihaizhou begin 2018.01.18
        /*String action = intent.getAction();//del by lihaizhou
        switch (action) {
            case ACTION_FIND:
                Log.i(TAG, "onReceive: liaoyi ");
                FindWristWatchActivity.startActivity(context);
                break;
            default:
                break;
        }*/
        //del by lihaizhou end 2018.01.18

        //Add by lihaizhou begin 2018.01.18
        if (ACTION_FIND.equals(intent.getAction())) {
            String isInvideo = (android.provider.Settings.System.getString(context.getContentResolver(), "xun_video") == null) ? "false" : android.provider.Settings.System.getString(context.getContentResolver(), "xun_video");
            if (isInvideo.equals("true")) return;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //电话状态 无任何状态时
            if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE && !isDisturb(context)) {
                Log.d(TAG, "receive findWatch Request broadcast");
                Intent findWatchIntent = new Intent();
                findWatchIntent.setClass(context, FindWristWatchActivity.class);
                findWatchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(findWatchIntent);
            }/* else if(tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
        } else if(tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
        }*/
        }
        //Add by lihaizhou end 2018.01.18
    }

    /**
     * 是否是免打扰模式
     *
     * @return
     */
    private boolean isDisturb(Context context) {
        String result = android.provider.Settings.System.getString(context.getApplicationContext().getContentResolver(), "SilenceList_result");
        boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
        return SilenceList_result;
    }
}
