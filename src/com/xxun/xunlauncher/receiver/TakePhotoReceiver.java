/**
 * 作者：郭洪成
 * 功能：监听后台拍照广播
 */
package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.SystemProperties;
import android.os.Looper;
import android.util.Log;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.xunlauncher.Constants;
import com.xiaoxun.sdk.utils.Constant;
import com.xxun.xunlauncher.utils.CameraUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


/**
 * 实现后台拍照功能
 * 服务器下发的后台拍照指令，会在网络层做处理XiaoXunNetMsgDispatcher.java
 * 并通过广播的方式转发给APP层
 */
public class TakePhotoReceiver extends BroadcastReceiver {
    private static final String TAG = "TakePhotoReceiver";
    private XiaoXunNetworkManager mXunNetworkManager;
    private String[] teidArray = {"0"};
    private Lock lock = new ReentrantLock();

    // add by guohongcheng_20180514
    // 省电测策略，分享前由2G切4G
    private int mFileType = -1;
    private String mRecvMsg;
    private NetSwitchReceiver mNetSwitchReceiver = null;
    private XiaoXunNetworkManager mXiaoXunNetworkManager = null;
    private Context mContext;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if ("com.xunlauncher.takephoto".equals(intent.getAction())) {
            Log.d(TAG, "TakePhotoReceiver >> com.xunlauncher.takephoto");
            ExecutorService singleThreadExecutor = newSingleThreadExecutor();
            mContext = context;
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    if (mXunNetworkManager == null) {
                        mXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
                    }
                    String recvMsg = intent.getStringExtra("recvMsg");

                    if (isWifiContected()) { // WIFI模式下 ，直接分享，不需要判断是否2G切4G
                        Log.d(TAG, "isWifiContected = true ");
                        upload(recvMsg);
                    } else if (isNeedChangeTo4GMode()) { // 4G模式下 ，直接分享，不需要2G切4G
                        Log.d(TAG, "isNeedChangeTo4GMode = true ");
                        upload(recvMsg);
                        releaseLTEMode();
                    } else { // 正在做2G切换4G操作，等待切换完毕后分享。
                        doRegister();
                        mRecvMsg = recvMsg;                        
                    }                    
                }
            });
            // add by guohongcheng_20180622 start
            // 倒计时10S，注销广播
            countDown();
            // add by guohongcheng_20180622 end
        }
    }

    /**
     *
     */
    private class SendMsgCallBack extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
            Log.d(TAG, "SendMsgCallBack onSuccess " + responseData);
        }

        @Override
        public void onError(int i, String s) {
            Log.d(TAG, "SendMsgCallBack onError " + i + s);
        }
    }

    /**
     * 处理广播携带的msg，组合成Jason，发送给APP端
     */
    private void sendE2EMsg(String recvMsg, int rc) {
        try {
            JSONObject recvJsonMsg = new JSONObject(recvMsg);
            //int sn = (int) recvJsonMsg.get("SN");//del by lihaizhou avoid compile error on 4.4 20180710
	    int sn = Integer.parseInt(recvJsonMsg.get("SN").toString());//Add by lihaizhou for 4.4
            String teid = (String) recvJsonMsg.get("SEID");
            Log.d(TAG, "sendE2EMsg sn " + sn);

            teidArray[0] = teid;
            JSONObject pl = new JSONObject();
            pl.put("EID", mXunNetworkManager.getWatchEid());
            pl.put("sub_action", 113);
            pl.put("RC", rc);

            mXunNetworkManager.sendE2EMessageEX(teidArray, sn, rc, pl.toString(), new SendMsgCallBack());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // add by guohongcheng_20180514
    // 省电测策略，分享前由2G切4G
    private void doRegister() {
        Log.d(TAG, "doRegister ");
        mNetSwitchReceiver = new NetSwitchReceiver();
        IntentFilter loginFilter = new IntentFilter();
        loginFilter.addAction(Constant.ACTION_NET_SWITCH_SUCC);
        if (mContext != null) {
            mContext.getApplicationContext().registerReceiver(mNetSwitchReceiver, loginFilter);
        }
    }

    // 收到2G切换到4G的广播后，再进行分享
    class NetSwitchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "NetSwitchReceiver: " + action);
            if (Constant.ACTION_NET_SWITCH_SUCC.equals(action)) {
                if (mRecvMsg != null) {
                    uploadWithWorkThread(mRecvMsg);
                }
                releaseLTEMode();
                unRegisterReceiver();
            }
        }
    }

    /**
     * 判断WIFI是否连接成功
     *
     * @return
     */
    private boolean isWifiContected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            Log.d(TAG, "Wifi网络连接成功");
            return true;
        }
        Log.d(TAG, "Wifi网络连接失败");
        return false;
    }

    private boolean isNeedChangeTo4GMode() {
        Log.d(TAG, "isNeedChangeTo4GMode.. ");
        if (mXiaoXunNetworkManager == null) {
            mXiaoXunNetworkManager =
                    (XiaoXunNetworkManager) mContext.getSystemService("xun.network.Service");
        }
        // requireLTEMode 如果当前已经处于4G，则不需要切换网络，requireLTEMode返回false
        // 如果是网络状况不好导致切换到2G，此时requireLTEMode返回false，并且保持2G，不切换到4G
        return !mXiaoXunNetworkManager.requireLTEMode("com.background.photo");
    }

    private void releaseLTEMode() {
        Log.d(TAG, "releaseLTEMode.. ");
        if (mXiaoXunNetworkManager != null) {
            mXiaoXunNetworkManager.releaseLTEMode("com.background.photo");
        }
    }

    private void unRegisterReceiver() {
        Log.d(TAG, "unRegisterReceiver.. ");
        if (mNetSwitchReceiver != null && mContext != null) {
            mContext.getApplicationContext().unregisterReceiver(mNetSwitchReceiver);
            mNetSwitchReceiver = null;
        }
    }

    private void uploadWithWorkThread(final String recvMsg) {
        Log.d(TAG, "uploadWithWorkThread " + recvMsg);
        ExecutorService singleThreadExecutor = newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                upload(recvMsg);
            }
        });
    }

    private void upload(String recvMsg) {
        Log.d(TAG, "upload recvMsg " + recvMsg);
        // 高温时，不响应后台拍照
        int currentTmp = Integer.valueOf(SystemProperties.get("persist.sys.xxun.aptemper"));
        Log.d(TAG, "TakePhotoReceiver >> isCameraUsing " + " currentTmp " + currentTmp);
        // 当前Camera没在使用
        String foregroundActivityName = ForegroundAppUtil.getForegroundActivityName(mContext);
        Log.d(TAG, "foregroundActivityName " + foregroundActivityName);
        if (!"com.xxun.camera".equals(foregroundActivityName) // 730滤镜相机
                && !"com.xxun.xunimgrec".equals(foregroundActivityName) // 拍照试图
                && !"com.xxun.xunwordsrec".equals(foregroundActivityName) // 拍照试图
                && !"com.xxun.xuncamera".equals(foregroundActivityName) // 710 相机
/* longcheer:lishuangwei on: Fri, 13 Dec 2019 10:57:45 +0800
                && !(currentTmp == 7 || currentTmp == 3)) {
 */
                && !(currentTmp >= Constants.XUN_TEMPER_OVERHEAT)) {
// End of longcheer:lishuangwei
            Log.d(TAG, "take photo ing ff");
            Looper.prepare();
            Log.d(TAG, "take photo ing prepare");
            sendE2EMsg(recvMsg, 1);
            Log.d(TAG, "take photo ing sendE2EMsg");
            // lock.lock();
            CameraUtil.getCameraInstance().takePhoto(mContext, mXunNetworkManager);
            // lock.unlock();
            Looper.loop();
        } else { // 当前Camera正在使用，无法拍照
            Log.d(TAG, "un take photo");
            sendE2EMsg(recvMsg, -1);
        }
    }

    private void countDown() {
        /** 倒计时25秒，一次1秒 */
        CountDownTimer timer = new CountDownTimer(25 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void onFinish() {
                releaseLTEMode();
                unRegisterReceiver();
            }
        }.start();
    }
}
