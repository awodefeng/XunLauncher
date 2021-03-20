package com.xxun.xunlauncher.utils;

import android.app.Service;
//import android.app.usage.NetworkStats;
//import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import net.minidev.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.os.BatteryManager;
import android.os.Bundle;
import android.content.SharedPreferences;

/**
 * @author xuzhonghong
 * @createtime 2017.12.06
 * @class describe 上传手表状态，包括电量状态，充电状态，开机状态，信号强度，电池温度
 */
public class UploadStatusUtils {

    private final String TAG = "UploadStatusUtils";
    private static final String BATTEMP_PATH = "/sys/class/power_supply/battery/temp";
    private static final String BATTERY_LEVEL_PATH = "/sys/class/power_supply/battery/capacity";
    private XiaoXunNetworkManager mXiaoXunNetworkManager;
    private String gid;
    private String[] keys = {"watch_status", "status", "signal_level", "battery_level", "battery_temp", "net_status"/*, "flowmeter_level"*/};
    private String[] values = new String[6];
    private String chargestatus = "0";
    private String signallevel = "0";
    private String batterylevel;
    private String time;
    private int int_electricity = 0;
    private String str_electricity;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor editor;
    public static final String BATTERY_SERVICE = "batterymanager";
    private long oldTime;
    private boolean mIsShutDown = false;

//    private NetworkStatsManager networkStatsManager;

    public static UploadStatusUtils getUploadStatusUtilsInstance() {
        return UploadStatusUtilshodler.uploadStatusUtilsInstance;
    }

    private static class UploadStatusUtilshodler {
        private static UploadStatusUtils uploadStatusUtilsInstance = new UploadStatusUtils();
    }

    public synchronized void uploadStatus(Context context) {
        Log.d(TAG, "System.currentTimeMillis() - oldTime = " + (System.currentTimeMillis() - oldTime));
        if ((System.currentTimeMillis() - oldTime) > 1000) {
            if (mXiaoXunNetworkManager == null) {
                mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
            }

            Log.d(TAG, " uploadStatus!!! ");


            getValues(context);
            if (mXiaoXunNetworkManager.isLoginOK() && values[3] != "") {
                mXiaoXunNetworkManager.setMapMSetValue(gid, keys, values, new SendMessageCallback());
            }
            oldTime = System.currentTimeMillis();
        }
    }

    public synchronized void uploadStatusForpadding(Context context) {
        Log.d(TAG, "System.currentTimeMillis() - oldTime = " + (System.currentTimeMillis() - oldTime));
        if ((System.currentTimeMillis() - oldTime) > 1000) {
            if (mXiaoXunNetworkManager == null) {
                mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
            }


            Log.d(TAG, " uploadStatusForpadding!!! ");
            getValues(context);
            if (mXiaoXunNetworkManager.isLoginOK() && values[3] != "") {
                mXiaoXunNetworkManager.paddingSetMapMSetValue(gid, keys, values, new SendMessageCallback());
            }
            oldTime = System.currentTimeMillis();
        }
    }

    private void getValues(Context context) {
        gid = mXiaoXunNetworkManager.getWatchGid();
        values[0] = getWatchStatus();
        values[1] = getChargeStatus();
        values[2] = getSignalLevel();
        values[3] = getBatteryLevel(context);
        values[4] = getBatteryTemp(BATTEMP_PATH);
        values[5] = getConnNetStatus(context);
        //add by liaoyi 18/5/31
//        values[5] = time + "_" + getMonthTodayMobile(context, System.currentTimeMillis());
        //end
        //Log.d(TAG, "keys[0] = " + keys[0] + ",keys[1] = " + keys[1] + ",keys[2] = " + keys[2] + ",keys[3] = " + keys[3] + ",keys[4] = " + keys[4] + ",keys[5] = " + keys[5]);
        //Log.d(TAG, "values[0] = " + values[0] + ",values[1] = " + values[1] + ",values[2] = " + values[2] + ",values[3] = " + values[3] + ",values[4] = " + values[4] + ",values[5] = " + values[5]);
    }

    /**
     * add by liaoyi
     * 上报版本号等
     **/
    public void uploadVersion(final Context context) {
        final String nowVersion = SystemProperties.get("ro.custom.internal.version");
        String oldVersion = SharedPreferencesUtils.getDeviceVersion(context);
        if (!nowVersion.equals(oldVersion)) {

            if (mXiaoXunNetworkManager == null) {
                mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
            }
            JSONObject plObject = new JSONObject();
            plObject.put("EID", mXiaoXunNetworkManager.getWatchEid());
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                String sim1IMSI = telephonyManager.getSubscriberId();
                plObject.put("Imsi", sim1IMSI);
                plObject.put("Iccid", telephonyManager.getSimSerialNumber());
            } else {
                plObject.put("Imsi", "");
                plObject.put("Iccid", "");
            }

            plObject.put("VersionCur", nowVersion);
            plObject.put("VersionOrg", "");

            String uploadJson = plObject.toJSONString();
            Log.i(TAG, "uploadVersion: upload version json = " + uploadJson);
            mXiaoXunNetworkManager.setDeviceAttribute(uploadJson, new IResponseDataCallBack.Stub() {
                @Override
                public void onSuccess(ResponseData responseData) throws RemoteException {
                    if (responseData.getResponseCode() == ResponseData.RESPONSE_CODE_OK) {
                        SharedPreferencesUtils.putDeviceVersion(context, nowVersion);
                    }
                    Log.i(TAG, "onSuccess: upload version --> code = " + responseData.getResponseCode());
                }

                @Override
                public void onError(int errorCode, String errorMessage) throws RemoteException {
                    Log.e(TAG, "onError: upload version failed");
                }
            });

        }
    }

    public void setChargeStatus(String chargestatus) {
        this.chargestatus = chargestatus;
    }

    public void setSignalLevel(String signallevel) {
        this.signallevel = signallevel;
    }

    public void setBatteryLevel(String batterylevel) {
        this.batterylevel = batterylevel;
    }

    public void setShutDownFlag(){ mIsShutDown = true; }

    private String getWatchStatus() {
        time = getCurrentTime();
        if(mIsShutDown)
            return time + "_2";
        return time + "_1";
    }

    private String getChargeStatus() {
        return chargestatus;
    }

    private String getSignalLevel() {

        int sig_level = Integer.parseInt(signallevel);
        int_electricity = sig_level > 0 ? sig_level * 20 - 1 : 0;

        str_electricity = Integer.toString(int_electricity);
        Log.d(TAG, "getSignalLevel:str_electricity=" + str_electricity + "signallevel=" + signallevel);

        return time + "_" + str_electricity;
    }

    private String getBatteryLevel(Context context) {

        Log.d(TAG, "getBatteryLevel,batterylevel= " + batterylevel);

        if ((batterylevel == null) || (batterylevel == "")) {
            //Modify by xuzhonghong on 20180718 start
            int battery = getBattery(BATTERY_LEVEL_PATH);
            //Modify by xuzhonghong on 20180718 end
            Log.d(TAG, "before battery=" + battery);
            int a = battery / 5;
            if ((battery % 5) != 0) {
                battery = (a + 1) * 5;
            }

            Log.d(TAG, "after battery=" + battery);

            sp = context.getSharedPreferences("battery_level", 0);
            editor = sp.edit();
            editor.putInt("level", battery);
            editor.commit();

            batterylevel = Integer.toString(battery);
        }
        return time + "_" + batterylevel;
    }

    private String getBatteryTemp(String path) {
        String temp = "";
        int i = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            if (reader != null) {
                try {
                    temp = reader.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            i = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        temp = Integer.toString(i / 10);
        return time + "_" + temp;
    }

    private int getBattery(String path) {
        String level = "";
        int i = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            if (reader != null) {
                try {
                    level = reader.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            i = Integer.parseInt(level);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    private String getConnNetStatus(Context context) {
        String status = "0000";
        if (mXiaoXunNetworkManager == null) {
            mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
        }

        status = mXiaoXunNetworkManager.getNetStatus();
        Log.d(TAG, "getConnNetStatus:status = " + status);

        return time + "_" + status;
    }
//    private String getMonthTodayMobile(Context context, long endTime) {
//        if (networkStatsManager == null) {
//            networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
//        }
//
//        NetworkStats.Bucket bucket = null;
//        try {
//            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, null,
//                    getTimesMorning(), endTime);
//        } catch (RemoteException e) {
//            return "-1";
//        }
//        return new DecimalFormat("#.##").format((bucket.getTxBytes() + bucket.getRxBytes()) / 1024d);
//    }
//
//    private static long getTimesMorning() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        return cal.getTimeInMillis();
//    }

    private static String getCurrentTime() {
        Date d = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return format.format(d);
    }

    private class SendMessageCallback extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
            Log.d(TAG, "upload success!" + responseData);
        }

        @Override
        public void onError(int i, String s) {
            Log.d(TAG, "upload error! i = " + i + ",s = :" + s);
        }
    }
}
