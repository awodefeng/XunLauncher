package com.xxun.xunlauncher.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import android.content.ComponentName;
import com.xxun.xunlauncher.R;
import com.xxun.xunlauncher.Constants;

import android.provider.Settings;

import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xxun.xunlauncher.utils.NetworkUtils;
import android.util.XiaoXunUtil;

/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 接收SIM卡状态变化的广播类
 */
public class SimCardStateChangeReceiver extends BroadcastReceiver {

    private Object lock = new Object();
    private boolean isSimCardValid;
    protected static List<SimStateChangeListener> listeners = new ArrayList<SimStateChangeListener>();
    private static final String TAG = "SimCardChangeReceiver";
    private boolean isFirst = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.ACTION_SIM_STATE_CHANGED)) {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (telephonyManager.getSimState()) {
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                        if(XiaoXunUtil.XIAOXUN_CONFIG_PRODUCT_CTA_TEST){
		                Intent unlockpin = new Intent();
		                unlockpin.setComponent(new ComponentName("com.android.settings","com.android.settings.XunPinPassword"));
		                unlockpin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		                context.startActivity(unlockpin);
		        }
                    break;
            
                case TelephonyManager.SIM_STATE_ABSENT:
                    Log.d(TAG, "SIM_STATE_ABSENT");
                    //Toast.makeText(context, "SIM卡不在位", Toast.LENGTH_SHORT).show();
                    Settings.System.putString(context.getContentResolver(), "volte_on", "absent");
                    isSimCardValid = false;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    Log.d(TAG, "SIM_STATE_UNKNOWN");
                    //Toast.makeText(context, "SIM卡未知,正在识别中", Toast.LENGTH_SHORT).show();
                    isSimCardValid = false;
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    Log.d(TAG, "SIM_STATE_READY");
                    //added by mayanjun for sim changed.20171218
                    String simNo = telephonyManager.getLine1Number();
                    String imsi = telephonyManager.getSubscriberId();
                    String iccid = telephonyManager.getSimSerialNumber();
                    String savedIccid = android.provider.Settings.System.getString(context.getContentResolver(), com.xiaoxun.sdk.utils.Constant.SETTING_WATCH_ICCID);
                    isSimCardValid = true;
                    if (iccid != null && !iccid.equals(savedIccid)) {
                        //if current iccid does not match the saved one,need sync with server.
                        XiaoXunNetworkManager xunNetService = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
                        xunNetService.setDeviceSimChange(xunNetService.getWatchEid(),
                                xunNetService.getWatchGid(),
                                iccid,
                                simNo,
                                imsi,
                                null);
                    }
                    // xxun liuluyang start
                    if (com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW706") && isFirst
                            && NetworkUtils.getNetWorkUtilsInstance().isNeedCarrier(context, Constants.CHINA_TELECOM)) {
                        isFirst = false;
                        NetworkUtils.getNetWorkUtilsInstance().checkSimCarrier(context);
                    }
                    // xxun liuluyang end
                    break;
                default:
                    break;
            }
            Log.d(TAG, isSimCardValid ? "有SIM卡" : "无SIM卡");
            //通知所有注册监听者状态变化
            notifyStateToAll();
        }
    }

    private void notifyStateToAll() {
        synchronized (lock) {
            for (SimStateChangeListener listener : listeners) {
                notifyState(listener);
            }
        }
    }

    private void notifyState(SimStateChangeListener listener) {
        listener.isSimCardValid(isSimCardValid);
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.30
     * @describe SIM卡状态变化接口
     */
    public interface SimStateChangeListener {
        void isSimCardValid(boolean isSimCardOn);
    }

    public static void registerSimListener(SimStateChangeListener listener) {
        listeners.add(listener);
    }

    public static void unRegisterSimListener() {
        listeners.clear();
    }

}

