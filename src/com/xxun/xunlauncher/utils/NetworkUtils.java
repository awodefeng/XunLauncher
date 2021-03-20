package com.xxun.xunlauncher.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.ims.ImsManager;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.R;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xxun.xunlauncher.application.LauncherApplication;
import com.xxun.xunlauncher.callback.ChinaTelecomVolteCallBack;

import net.minidev.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 监测当前网络类型的变化以及SIM状态变化等网络相关的工具类
 */
public class NetworkUtils {

    private String networkType;

    private static final String TAG = "NetworkUtils";
    private final static String SN_CONTENT_URI = "content://com.xxun.xunlauncher.provider/sim_serial_number";// liuluyang add


    private NetworkUtils() {
    }

    public static NetworkUtils getNetWorkUtilsInstance() {
        return NetWorkUtilsHoloder.netWorkUtilsInstance;
    }

    private static class NetWorkUtilsHoloder {
        private static NetworkUtils netWorkUtilsInstance = new NetworkUtils();
    }

    /*
    * 检查SIM卡是否在位
    * */
    public boolean isSimCardOn() {
    	TelephonyManager telMgr = (TelephonyManager)LauncherApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    	int simState = telMgr.getSimState();
    	boolean result = true;
	if(simState == TelephonyManager.SIM_STATE_ABSENT){
	   result = false;
	}
        Log.d(TAG, "result = :"+result+",simState = :"+simState);
        return result;
     }
    
    /**
     * 判断WIFI是否连接成功
     * @param context
     * @return
     */
    public boolean isWifiContected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            Log.i(TAG, "Wifi网络连接成功");
            return true;
        }
        Log.i(TAG, "Wifi网络连接失败");
        return false;
    }

    /**
     * 检查网络是否可用
     * param context
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) LauncherApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return networkinfo != null && networkinfo.isAvailable();
    }

    /**
     * 获取网络的信息
     */
    public String getNetWorkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "PhoneNetUtils info = :" + info);
        if (info != null && info.isAvailable()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //wifi
                    WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = manager.getConnectionInfo();
                    int rssi = connectionInfo.getRssi();
                    Log.d(TAG, "当前为wifi网络，信号强度 rssi:" + rssi);
                    networkType = "wifi";
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    int subType = info.getSubtype();
                    if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                            || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                        networkType = "2G";
                    } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                            || subType == TelephonyManager.NETWORK_TYPE_HSUPA || subType == TelephonyManager.NETWORK_TYPE_HSPA
                            || subType == TelephonyManager.NETWORK_TYPE_HSPAP || subType == TelephonyManager.NETWORK_TYPE_EVDO_B
                            || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                        networkType = "3G";
                    } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
                        networkType = "4G";
                    }
                    break;
                default:
                    break;
            }
        } else {
            networkType = "unaviable";
        }
        return networkType;
    }
    // xxun liuluyang start
    public void checkSimCarrier(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sn = telephonyManager.getSimSerialNumber();
        Log.d("ChinaTelecomVolte", "checkSimCarrier--sim序列号: " + sn);
        if (sn != null) {
            boolean is = false;// 用于判断 是否已存在 sn
            Uri uri = Uri.parse(SN_CONTENT_URI);
            //查询数据
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String ss = cursor.getString(cursor.getColumnIndex("sn"));
                    Log.d("ChinaTelecomVolte", "NetworkUtils--数据库存储sim卡序列号sn: " + ss);
                    if (sn.equals(ss)) {
                        // 说明该 sim 卡已经插入过手表，那么就没必要再弹框提示了
                        is = true;
                    }
                }
                cursor.close();
            }
            // 手表首次插入该 sim 卡，保存数据
            if (!is) {
                ContentValues values = new ContentValues();
                values.put("sn", sn);
                context.getContentResolver().insert(uri, values);
                if (Settings.Global.getInt(context.getContentResolver(), "volte_vt_enabled", 0) != 1) {
                    openVolte(context);
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.xxun.xunlauncher", "com.xxun.xunlauncher.activity.AlertDialogActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.d("ChinaTelecomVolte", "UnStart AlertDialogActivity : " + e);
                }

            }
        }
    }

    /**
     * Settings.Global.getInt(mContext.getContentResolver(),
     * android.provider.Settings.Global.ENHANCED_4G_MODE_ENABLED)
     */
    private void openVolte(Context context) {
        ImsManager.setEnhanced4gLteModeSetting(context, true);
        Settings.Global.putInt(context.getContentResolver(), "volte_vt_enabled", 1);
    }

    private void sendVolteMessageToApp(Context context) {
        XiaoXunNetworkManager mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
        String eid = mXiaoXunNetworkManager.getWatchEid();
        String gid = mXiaoXunNetworkManager.getWatchGid();
        JSONObject content = new JSONObject();
        content.put("title", context.getResources().getText(R.string.china_telecom_volte_title));
        content.put("text", context.getResources().getText(R.string.china_telecom_volte_content));
        content.put("url", "https://app.xunkids.com/common_help/usedianxin.html");

        JSONObject value = new JSONObject();
        value.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        value.put(CloudBridgeUtil.KEY_NAME_TYPE, "system");
        value.put(CloudBridgeUtil.KEY_NAME_CONTENT, content);
        value.put(CloudBridgeUtil.KEY_NAME_DURATION, 100);

        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_VALUE, value);
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, gid);
        String key = "GP/" + gid + "/MSG/" + "#TIME#";
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key);
        int sn = Long.valueOf(getTimeStampGMT()).intValue();

        JSONObject msg = new JSONObject();
        String sid = mXiaoXunNetworkManager.getSID();
        msg.put(CloudBridgeUtil.KEY_NAME_CID, CloudBridgeUtil.CID_UPLOAD_NOTICE);
        if (sid != null) {
            msg.put(CloudBridgeUtil.KEY_NAME_SID, sid);
        }
        msg.put(CloudBridgeUtil.KEY_NAME_SN, sn);
        msg.put(CloudBridgeUtil.KEY_NAME_VERSION, CloudBridgeUtil.PROTOCOL_VERSION);
        msg.put(CloudBridgeUtil.KEY_NAME_PL, pl);
        mXiaoXunNetworkManager.sendJsonMessage(msg.toString(), new ChinaTelecomVolteCallBack());
    }

    private static String getTimeStampGMT() {
        Date d = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(d);
    }

    public void modifyVolteMsgStatus(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sn = telephonyManager.getSimSerialNumber();
        Log.d("ChinaTelecomVolte", "modifyVolteMsgStatus--sim序列号: " + sn);
        if (sn != null) {
            boolean is = false;
            Uri uri = Uri.parse(SN_CONTENT_URI);
            //先查询
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String ss = cursor.getString(cursor.getColumnIndex("sn"));
                    Log.d("ChinaTelecomVolte", "NetworkUtils--modifyVolteMsgStatus--存储的序列号: " + ss);
                    if (sn.equals(ss)) {
                        // 当前sim与已存储的sim匹配
                        int status = cursor.getInt(cursor.getColumnIndex("status"));
                        Log.d("ChinaTelecomVolte", "NetworkUtils--status: " + status);
                        if (status != 1) {
                            // 说明还没有发送消息给 APP
                            is = true;
                            sendVolteMessageToApp(context);
                        }
                    }
                }
                cursor.close();
            }
            if (is) {
                // 已发送给消息给 APP，修改状态值
                ContentValues values = new ContentValues();
                values.put("status", 1);
                context.getContentResolver().update(uri, values, "sn = ?", new String[]{sn});
            }
        }
    }

    /**
     * @param param 运营商
     *              移动 46000、46002、46007
     *              联通 46001、46006
     *              电信 46003、46005、46011
     * @return
     */
    public boolean isNeedCarrier(Context context, String[] param) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getSimOperator();
        Log.d("ChinaTelecomVolte", "NetworkUtils--sim运营商: " + operator);
        if (operator != null) {
            for (int i = 0; i < param.length; i++) {
                if (param[i].equals(operator)) {
                    return true;
                }
            }
        }
        return false;
    }
    // xxun liuluyang end
}
