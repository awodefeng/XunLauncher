package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;

import android.app.AlarmManager;
import android.app.PendingIntent;

import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.utils.UploadStatusUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author pengzonghong
 * @createtime 2017.12.20
 * @class describe 接收关机以及定时开机
 */
public class SchedulePowerOnOffReceiver extends BroadcastReceiver {

    private static final String TAG = "SchedulePowerOnOffReceiver";
    private static final int SCHEDULE_SHUT_DOWN = 200;

    private int hour = 0;
    private int minute = 0;
    private boolean start_schedule = false;
    private XiaoXunNetworkManager mXiaoXunNetworkManager;
    private String[] teidArray;
    private Context mContext;
    private String receive_msg;
    private JSONObject recvJsonMsg;
    private Handler mHandler = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        receive_msg = intent.getStringExtra("data");
        mHandler = new shutDownHandler();
        String shutdown_time = "";
        Log.d(TAG, "receive_msg:" + receive_msg);

        try {
            recvJsonMsg = new JSONObject(receive_msg);
            int sn = Integer.parseInt(recvJsonMsg.get("SN").toString());//Modified by lihaizhou for Android 4.4,avoid compile error
            String teid = (String) recvJsonMsg.get("SEID");

            Log.d(TAG, "sn:" + sn + " /teid:" + teid);

            teidArray = new String[1];
            teidArray[0] = teid;

            JSONObject recv_pl = (JSONObject) recvJsonMsg.get("PL");
            shutdown_time = (String) recv_pl.get("Key");

            Log.d(TAG, "shutdown time:" + shutdown_time);

            JSONObject new_pl = new JSONObject();
            new_pl.put("sub_action", 154);
            new_pl.put("Key", shutdown_time);
            new_pl.put("RC", 1);

            if (mXiaoXunNetworkManager == null) {
                mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
            }
            mXiaoXunNetworkManager.sendE2EMessage(teidArray, sn, new_pl.toString(), new shutdownSendMessageCallback());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //60061 start
        UploadStatusUtils.getUploadStatusUtilsInstance().setChargeStatus("0");
        UploadStatusUtils.getUploadStatusUtilsInstance().setShutDownFlag();
        UploadStatusUtils.getUploadStatusUtilsInstance().uploadStatus(context);
    // 60061 end

        Message newmsg = mHandler.obtainMessage(SCHEDULE_SHUT_DOWN);
        Bundle bd = newmsg.getData();
        bd.putString("times", shutdown_time);
        mHandler.sendMessageDelayed(newmsg, 800);

    }

    private void scheduleShutDown(String times){
        Log.d(TAG, "scheduleShutDown , times = " + times);
        if ((times != null) && (times != "")) {
            if (times.equals("0")) {

                Log.d(TAG, "scheduleShutDown now");
                Intent intent_shutdown = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                intent_shutdown.putExtra("android.intent.extra.KEY_CONFIRM", false);
                intent_shutdown.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent_shutdown);


            } else {

                Log.d(TAG, "[SHUTDOWN-MSG],shutdown setting time");
                int poweron_time = Integer.parseInt(times);

                Log.d(TAG, "[SHUTDOWN-MSG],poweron_time:" + poweron_time);

                AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent_poweron = new Intent("com.android.settings.action.REQUEST_POWER_ON");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent_poweron, PendingIntent.FLAG_CANCEL_CURRENT);


                long time = System.currentTimeMillis();
                Log.d(TAG, "before time:" + time);
                time = time + poweron_time * 60 * 1000;
                Log.d(TAG, "after time:" + time);

                am.set(5, time, pendingIntent);

                Log.d(TAG, "shutdown now");
                Intent intent_shutdown = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                intent_shutdown.putExtra("android.intent.extra.KEY_CONFIRM", false);
                intent_shutdown.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent_shutdown);

            }
        } else {
            Log.d(TAG, "shutdown_time is null");
        }
    }

    private class shutdownSendMessageCallback extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
            Log.d(TAG, "response shutdown state success!!!" + responseData);
        }

        @Override
        public void onError(int i, String s) {
            Log.d(TAG, "response shutdown state fail !!! i = " + i + ",s = :" + s);
        }
    }

    class shutDownHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCHEDULE_SHUT_DOWN:
                    scheduleShutDown(msg.peekData().getString("times"));
                    break;
                default:
                    break;
            }
        }

    }
}
