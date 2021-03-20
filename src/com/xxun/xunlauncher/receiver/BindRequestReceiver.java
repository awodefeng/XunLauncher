package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.provider.Settings;
import android.os.SystemProperties;

import com.xxun.xunlauncher.Constants;
import com.xxun.xunlauncher.utils.NetworkUtils;
import com.xxun.xunlauncher.utils.ShutDownUtils;
import com.xxun.xunlauncher.activity.ShowUnbindAlertActivity;
import com.xxun.xunlauncher.activity.ShowBindRequestActivity;

import android.content.ComponentName;


/**
 * @author lihaizhou
 * @createtime 2017.09.20
 * @class describe 接收绑定相关请求的广播类
 */
public class BindRequestReceiver extends BroadcastReceiver {

    private boolean isBindSuccessNow = false;
    private boolean isConfirmBindNow = false;
    protected static List<BindStateChangeListener> bindListeners = new ArrayList<BindStateChangeListener>();
    protected static List<ConfirmStateChangeListener> confirmListeners = new ArrayList<ConfirmStateChangeListener>();

    private static final String TAG = "BindRequestReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Constants.BIND_REQUEST_FROM_SERVER)) {
            Log.d(TAG, "receive bind request from server!");
            Intent bindrequestIntent = new Intent();
            bindrequestIntent.setClass(context, ShowBindRequestActivity.class);
            bindrequestIntent.putExtra("bindRequest", intent.getExtras().getString("bindRequestData"));
            bindrequestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bindrequestIntent);
        } else if (intent.getAction().equals(Constants.UNBIND_REQUEST_FROM_SERVER)) {
            Log.d(TAG, "receive unbind request from server!");
            SystemProperties.set("persist.sys.isbinded", "false");
            Settings.System.putString(context.getContentResolver(), "xunlauncher_first_boot", "false");
            isBindSuccessNow = false;
            notifyBindStateToAll();

            Intent unBindIntent = new Intent();
            unBindIntent.setClass(context, ShowUnbindAlertActivity.class);
            unBindIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(unBindIntent);

        } else if (intent.getAction().equals(Constants.BIND_SUCCESS_FROM_SERVER)) {
            Log.d(TAG, "zhj receive bind success msg from server!!!!");
            SystemProperties.set("persist.sys.isbinded", "true");
            isBindSuccessNow = true;
            notifyBindStateToAll();//通知所有注册监听者状态变化
            notifyAppstoreToDoUpdate(context);//通知应用商店去同步服务器默认配置author by jxring 2019.4.29;
            // xxun liuluyang start
            if (com.xiaoxun.sdk.utils.Constant.PROJECT_NAME.equals("SW706")
                    && NetworkUtils.getNetWorkUtilsInstance().isNeedCarrier(context, Constants.CHINA_TELECOM)) {
                Message msg = new Message();
                msg.what = 101;
                msg.obj = context;
                mDelayHandler.sendMessageDelayed(msg, 10 * 1000);
            }
            // xxun liuluyang end
        } else if (intent.getAction().equals(Constants.BIND_CONFIRM_ACTION)) {
            Log.d(TAG, "receive bind confirm msg!!!!");
            isConfirmBindNow = true;
            notifyConfirmStateToAll();//通知所有注册监听者状态变化
        }
    }

    //启动应用商店服务，做完同步行为，即自动销毁 author by jxring 2019.4.29
    private void notifyAppstoreToDoUpdate(Context context) {
        Intent mintent = new Intent();
        mintent.setComponent(new ComponentName("ado.install.xiaoxun.com.xiaoxuninstallapk", "com.xiaoxun.service.DealSubActionService"));
        mintent.setAction("xun.appstore.bind.first");
        context.startService(mintent);
    }

    private void notifyBindStateToAll() {
        for (BindStateChangeListener listener : bindListeners) {
            notifyBindState(listener);
        }
    }

    private void notifyConfirmStateToAll() {
        for (ConfirmStateChangeListener listener : confirmListeners) {
            notifyConfirmState(listener);
        }
    }

    private void notifyBindState(BindStateChangeListener listener) {
        listener.isBindSuccessNow(isBindSuccessNow);
    }

    private void notifyConfirmState(ConfirmStateChangeListener listener) {
        listener.isConfirmBindNow(isConfirmBindNow);
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.20
     * @describe 手表绑定状态变化接口
     */
    public interface BindStateChangeListener {
        void isBindSuccessNow(boolean isBindSuccessNow);
    }

    /**
     * @author lihaizhou
     * @createtime 2017.10.20
     * @describe 按下power键确认接收绑定请求的接口
     */
    public interface ConfirmStateChangeListener {
        void isConfirmBindNow(boolean isConfirmBindNow);
    }

    /*
     * 注册Bind监听器
     * */
    public static void registerBindListener(BindStateChangeListener listener) {
        bindListeners.add(listener);
    }

    /*
     * 解除注册Bind监听器
     * */
    public static void unRegisterBindListener() {
        bindListeners.clear();
    }

    /*

     * 注册Confirm监听器
     * */
    public static void registerConfirmListener(ConfirmStateChangeListener listener) {
        confirmListeners.add(listener);
    }

    /*
     * 解除注册Confirm监听器
     * */
    public static void unRegisterConfirmListener() {
        confirmListeners.clear();
    }
    // xxun liuluyang start
    private Handler mDelayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                mDelayHandler.removeMessages(101);
                NetworkUtils.getNetWorkUtilsInstance().modifyVolteMsgStatus((Context) msg.obj);
            }
        }
    };
    // xxun liuluyang end
}
